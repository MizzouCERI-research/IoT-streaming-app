
package org.example.basicApp.client;

import java.net.UnknownHostException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.TimeUnit;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMapper.FailedBatch;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import org.example.basicApp.model.VrMeasurement;

/**
 * Persists counts to DynamoDB. This uses a separate thread to send counts to DynamoDB to decouple any network latency
 * from affecting the thread we use to update counts.
 */
public class DynamoDBMeasurementWriter {
	
    private static final Log LOG = LogFactory.getLog(DynamoDBMeasurementWriter.class);

    // Generate UTC timestamps
    private static final TimeZone UTC = TimeZone.getTimeZone("UTC");

    private DynamoDBMapper mapper;

    /**
     * This is used to limit the in memory queue. This number is the total counts we could generate for 10 unique
     * resources in 10 minutes if our update interval is 100ms.
     *
     * 10 resources * 10 minutes * 60 seconds * 10 intervals per second = 60,000.
     */
    private static final int MAX_COUNTS_IN_MEMORY = 60000;

    // The queue holds all pending referrer pair counts to be sent to DynamoDB.
    private BlockingQueue<Map<String, AttributeValue>> queue = new LinkedBlockingQueue<>(MAX_COUNTS_IN_MEMORY);

    // The thread to use for sending counts to DynamoDB.
    private Thread dynamoDBSender;


    /**
     * Create a new persister with a DynamoDBMapper to translate counts to items and send to Amazon DynamoDB.
     *
     * @param mapper Amazon DynamoDB Mapper to use.
     */
    public DynamoDBMeasurementWriter (DynamoDBMapper mapper) {
        if (mapper == null) {
            throw new NullPointerException("mapper must not be null");
        }
        this.mapper = mapper;
    }

    public void initialize() {

        // This thread is responsible for draining the queue of new counts and sending them in batches to DynamoDB
        dynamoDBSender = new Thread() {

            @Override
            public void run() {
                // Create a reusable buffer to drain our queue into.
                List<Map<String, AttributeValue>> buffer = new ArrayList<>(MAX_COUNTS_IN_MEMORY);

                // Continuously attempt to drain the queue and send counts to DynamoDB until this thread is interrupted
                while (!Thread.currentThread().isInterrupted()) {
                    try {
                        // Drain anything that's in the queue to the buffer and write the items to DynamoDB
                        sendQueueToDynamoDB(buffer);
                        // We wait for an empty queue before checkpointing. Notify that thread when we're empty in
                        // case it is waiting.
                        synchronized(queue) {
                            if (queue.isEmpty()) {
                            	queue.notify();
                            }
                        }
                    } catch (InterruptedException e) {
                        LOG.error("Thread that handles persisting counts to DynamoDB was interrupted. Counts will no longer be persisted!",
                                e);
                        return;
                    } finally {
                        // Clear the temporary buffer to release references to persisted counts
                        buffer.clear();
                    }
                }
            }
        };
        dynamoDBSender.setDaemon(true);
        dynamoDBSender.start();
    }

    public void pushToQueue (VrMeasurement measurementRecord) {
        if (measurementRecord == null) {
            // short circuit to avoid creating a map when we have no objects to persist
            return;
        }
        Map<String, AttributeValue> item = newItem(measurementRecord);
        queue.add(item);
    }
   
    /**
     * We will block until the entire queue of counts has been drained.
     */
    public void checkpoint() throws InterruptedException {
        // We need to make sure all counts are flushed to DynamoDB before we return successfully.
        if (dynamoDBSender.isAlive()) {
            // If the DynamoDB thread is running wait until our counts queue is empty
            synchronized(queue) {
                while (!queue.isEmpty()) {
                	queue.wait();
                }
                // All the counts we currently know about have been persisted. It is now safe to return from this blocking call.
            }
        } else {
            throw new IllegalStateException("DynamoDB persister thread is not running. Counts are not persisted and we should not checkpoint!");
        }
    }

    /**
     * Drain the queue of pending counts into the provided buffer and write those counts to DynamoDB. This blocks until
     * data is available in the queue.
     *
     * @param buffer A reusable buffer with sufficient space to drain the entire queue if necessary. This is provided as
     *        an optimization to avoid allocating a new buffer every interval.
     * @throws InterruptedException Thread interrupted while waiting for new data to arrive in the queue.
     */

    protected void sendQueueToDynamoDB(List<Map<String, AttributeValue>> buffer) throws InterruptedException {
        // Block while waiting for data
        buffer.add(queue.take());
        // Drain as much of the queue as we can.
        // DynamoDBMapper will handle splitting the batch sizes for us.
        queue.drainTo(buffer);
        try {
            long start = System.nanoTime();
            // Write the contents of the buffer as items to our table
            List<FailedBatch> failures = mapper.batchWrite(buffer, Collections.emptyList());
            long end = System.nanoTime();
//            LOG.info(String.format("%d new record sent to DynamoDB in %dms",
//                    buffer.size(),
//                    TimeUnit.NANOSECONDS.toMillis(end - start)));

            for (FailedBatch failure : failures) {
                //LOG.warn("Error sending measurement batch to DynamoDB. This will not be retried!", failure.getException());
            }
        } catch (Exception ex) {
            //LOG.error("Error sending new measurements to DynamoDB. The some measurements may not be persisted.", ex);
        }
    }
  
    
    
    private static Map<String, AttributeValue> newItem(VrMeasurement record) {
    	
    	Map<String, AttributeValue> item = new HashMap<String, AttributeValue>();
        item.put("resource", new AttributeValue(record.getResource()));
        item.put("timestamp", new AttributeValue(record.getTimeStamp()));
        item.put("host", new AttributeValue(record.getHost()));
        item.put("engagement", new AttributeValue(record.getEngagement()));
        item.put("focus", new AttributeValue(record.getFocus()));
        item.put("excitement", new AttributeValue(record.getExcitement()));
        item.put("frustration", new AttributeValue(record.getFrustration()));
        item.put("stress", new AttributeValue(record.getStress()));
        item.put("relaxation", new AttributeValue(record.getRelaxation()));

        return item;
    }

}
