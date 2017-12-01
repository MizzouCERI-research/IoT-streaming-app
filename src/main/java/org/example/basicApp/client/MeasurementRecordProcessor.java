/*
 * Copyright 2014 Amazon.com, Inc. or its affiliates. All Rights Reserved.
 *
 * Licensed under the Amazon Software License (the "License").
 * You may not use this file except in compliance with the License.
 * A copy of the License is located at
 *
 * http://aws.amazon.com/asl/
 *
 * or in the "license" file accompanying this file. This file is distributed
 * on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either
 * express or implied. See the License for the specific language governing
 * permissions and limitations under the License.
 */

package org.example.basicApp.client;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.amazonaws.services.kinesis.clientlibrary.exceptions.InvalidStateException;
import com.amazonaws.services.kinesis.clientlibrary.exceptions.ShutdownException;
import com.amazonaws.services.kinesis.clientlibrary.exceptions.ThrottlingException;
import com.amazonaws.services.kinesis.clientlibrary.interfaces.IRecordProcessor;
import com.amazonaws.services.kinesis.clientlibrary.interfaces.IRecordProcessorCheckpointer;
import com.amazonaws.services.kinesis.clientlibrary.types.ShutdownReason;
import com.amazonaws.services.kinesis.model.Record;
import org.example.basicApp.model.VrMeasurement;
import org.example.basicApp.client.DBWriter;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;


/**
 * Computes a map of (HttpReferrerPair -> count(pair)) over a fixed range of time. Counts are computed at the intervals
 * provided.
 *
 * @param <T> The type of records this processor is capable of counting.
 */
public class MeasurementRecordProcessor<T> implements IRecordProcessor {

	private static final Log LOG = LogFactory.getLog(MeasurementRecordProcessor.class);
    
    // Our JSON object mapper for deserializing records
    private final ObjectMapper objectMapper;

    // The shard this processor is processing
    private String kinesisShardId;

    // The type of record we expect to receive as JSON
    private Class<T> recordType;
    
    // This is responsible for writing record to dynamoDB every interval
    private DBWriter<T> dbWriter;

    // Backoff and retry settings
    private static final long BACKOFF_TIME_IN_MILLIS = 3000L;
    private static final int NUM_RETRIES = 10;

    // Checkpoint about once a minute
    private static final long CHECKPOINT_INTERVAL_MILLIS = 60000L;
    private long nextCheckpointTimeInMillis;
    
    
    /**
     * Create a new processor.
     *
     * @param config Configuration for this record processor.
     * @param recordType The type of record we expect to receive as a UTF-8 JSON string.
     * @param persister Counts will be persisted with this persister.
     * @param computeRangeInMillis Range to compute distinct counts across
     * @param computeIntervalInMillis Interval between computing total count for the overall time range.
     */
    public MeasurementRecordProcessor(Class<T> recordType, DBWriter<T> dbWriter) {
    	
        if (recordType == null) {
            throw new NullPointerException("recordType must not be null");
        }
        if (dbWriter == null) {
            throw new NullPointerException("dbWriter must not be null");
        }
        
        this.recordType = recordType;
        this.dbWriter = dbWriter;

        // Create an object mapper to deserialize records that ignores unknown properties
        objectMapper = new ObjectMapper();
        objectMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    @Override
    public void initialize(String shardId) {
        LOG.info("Initializing record processor for shard: " + shardId);
        this.kinesisShardId = shardId;
        dbWriter.initialize();

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void processRecords(List<Record> records, IRecordProcessorCheckpointer checkpointer) {
        LOG.info("Processing " + records.size() + " records from " + kinesisShardId);
        
        for (Record record : records) {
            boolean processedSuccessfully = false;
            for (int i = 0; i < NUM_RETRIES; i++) {
                try {
                    //
                    // Logic to process record goes here.
                    //
                    processSingleRecord(record);

                    processedSuccessfully = true;
                    break;
                } catch (Throwable t) {
                    LOG.warn("Caught throwable while processing record " + record, t);
                }

                // backoff if we encounter an exception.
                try {
                    Thread.sleep(BACKOFF_TIME_IN_MILLIS);
                } catch (InterruptedException e) {
                    LOG.debug("Interrupted sleep", e);
                }
            }

            if (!processedSuccessfully) {
                LOG.error("Couldn't process record " + record + ". Skipping the record.");
            }
        }

        // Checkpoint once every checkpoint interval.
        if (System.currentTimeMillis() > nextCheckpointTimeInMillis) {
            checkpoint(checkpointer);
            nextCheckpointTimeInMillis = System.currentTimeMillis() + CHECKPOINT_INTERVAL_MILLIS;
        }
    }


    public void processSingleRecord(Record r) {
     
        // Deserialize each record as an UTF-8 encoded JSON String of the type provided
        T data = null;
        	
        try {
           data = objectMapper.readValue(r.getData().array(), recordType);
           //LOG.info(String.format("Measurement record read from stream is: %s \n", data.toString()));
           LOG.info(String.format("one record has been processed......... %s", data.toString()));
               
        } catch (IOException e) {
           LOG.warn("Skipping record. Unable to parse record into Measurements. Partition Key: "
                + r.getPartitionKey() + ". Sequence Number: " + r.getSequenceNumber(),e);           
        }
        
        // Persist the counts if we have a full range
        if (data != null) {
            dbWriter.pushToQueue(data);
        }
        
     }

    @Override
    public void shutdown(IRecordProcessorCheckpointer checkpointer, ShutdownReason reason) {
        LOG.info("Shutting down record processor for shard: " + kinesisShardId);
        if (reason == ShutdownReason.TERMINATE) {
            try {
                checkpointer.checkpoint();
            }
            catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    
    /** Checkpoint with retries.
     * @param checkpointer
     */
    private void checkpoint(IRecordProcessorCheckpointer checkpointer) {
        LOG.info("Checkpointing shard " + kinesisShardId);
        for (int i = 0; i < NUM_RETRIES; i++) {
            try {
            	dbWriter.checkpoint();
                checkpointer.checkpoint();
                break;
            } catch (ShutdownException se) {
                // Ignore checkpoint if the processor instance has been shutdown (fail over).
                LOG.info("Caught shutdown exception, skipping checkpoint.", se);
                break;
            } catch (ThrottlingException e) {
                // Backoff and re-attempt checkpoint upon transient failures
                if (i >= (NUM_RETRIES - 1)) {
                    LOG.error("Checkpoint failed after " + (i + 1) + "attempts.", e);
                    break;
                } else {
                    LOG.info("Transient issue when checkpointing - attempt " + (i + 1) + " of "
                            + NUM_RETRIES, e);
                }
            } catch (InvalidStateException e) {
                // This indicates an issue with the DynamoDB table (check for table, provisioned IOPS).
                LOG.error("Cannot save checkpoint to the DynamoDB table used by the Amazon Kinesis Client Library.", e);
                break;
            }catch (InterruptedException e) {
                LOG.error("Error encountered while checkpointing dbWriter.", e);
                // Fall through to attempt retry
            }
            try {
                Thread.sleep(BACKOFF_TIME_IN_MILLIS);
            } catch (InterruptedException e) {
                LOG.debug("Interrupted sleep", e);
            }
        }
    }
    
}
