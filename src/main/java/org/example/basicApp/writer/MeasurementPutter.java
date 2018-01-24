package org.example.basicApp.writer;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.concurrent.TimeUnit;
import java.lang.String;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.example.basicApp.model.VrMeasurement;
import com.amazonaws.AmazonClientException;
import com.amazonaws.services.kinesis.AmazonKinesis;
import com.amazonaws.services.kinesis.model.ProvisionedThroughputExceededException;
import com.amazonaws.services.kinesis.model.PutRecordRequest;
import com.fasterxml.jackson.databind.ObjectMapper;

// Sends measurements to Amazon Kinesis.
public class MeasurementPutter {
    private static final Log LOG = LogFactory.getLog(MeasurementPutter.class);
   	
	//private VrMeasurement vrMeasurement;
    private AmazonKinesis kinesis;
    private String streamName;

    private final ObjectMapper JSON = new ObjectMapper();

    public MeasurementPutter(/*VrMeasurement vrMeasurement,*/ AmazonKinesis kinesis, String streamName) {
        //if (vrMeasurement == null) {
        //   throw new IllegalArgumentException("vrMeasurement must not be null");
        //}
        if (kinesis == null) {
            throw new IllegalArgumentException("kinesis must not be null");
        }
        if (streamName == null || streamName.isEmpty()) {
            throw new IllegalArgumentException("streamName must not be null or empty");
        }
        //this.vrMeasurement = vrMeasurement;
        this.kinesis = kinesis;
        this.streamName = streamName;
    }	

    // Send a fixed number of data records to Amazon Kinesis. 
    public void sendMeasurements(long n, long delayBetweenRecords, TimeUnit unitForDelay) throws InterruptedException {
        for (int i = 0; i < n && !Thread.currentThread().isInterrupted(); i++) {
            sendMeasurement();
            Thread.sleep(unitForDelay.toMillis(delayBetweenRecords));
        }
    }

    // Continuously sends data records to Amazon Kinesis sequentially
    public void sendMeasurementsIndefinitely(long delayBetweenRecords, TimeUnit unitForDelay) throws InterruptedException {
        while (!Thread.currentThread().isInterrupted()) {
            sendMeasurement();
            if (delayBetweenRecords > 0) {
                Thread.sleep(unitForDelay.toMillis(delayBetweenRecords));
            }
        }
    }

    // Send a single record to Amazon Kinesis using PutRecord.
    private void sendMeasurement() {  
    	
    	final VrMeasurement vrMeasurement = new VrMeasurement();    	
        byte[] bytes;
        try {
            bytes = JSON.writeValueAsBytes(vrMeasurement);
        } catch (IOException e) {
            LOG.warn("Skipping vrMeasurement. Unable to serialize: '" + vrMeasurement + "'", e);
            return;
        }

        PutRecordRequest putRecord = new PutRecordRequest();
        putRecord.setStreamName(streamName);
        putRecord.setPartitionKey("key");
		putRecord.setData(ByteBuffer.wrap(bytes));
        putRecord.setSequenceNumberForOrdering(null);

        try {
            kinesis.putRecord(putRecord);
            LOG.info(String.format("one data record is put in stream, data include: %s \n", vrMeasurement.toString()));
        } catch (ProvisionedThroughputExceededException ex) {
            if (LOG.isDebugEnabled()) {
                LOG.debug(String.format("Thread %s's Throughput exceeded. Waiting 10ms", Thread.currentThread().getName()));
            }
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        } catch (AmazonClientException ex) {
            LOG.warn("Error sending record to Amazon Kinesis.", ex);
        }
    }
}

