/*
*
* Developed and adpated by Songjie Wang
* Department of EECS
* University of Missouri
*
*/

package org.example.basicApp.writer;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.util.concurrent.TimeUnit;
import java.lang.String;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.example.basicApp.model.VrMeasurement;
import org.example.basicApp.model.RawMeasurement;
import com.amazonaws.AmazonClientException;
import com.amazonaws.services.kinesis.AmazonKinesis;
import com.amazonaws.services.kinesis.model.ProvisionedThroughputExceededException;
import com.amazonaws.services.kinesis.model.PutRecordRequest;
import com.fasterxml.jackson.databind.ObjectMapper;

// Sends measurements to Amazon Kinesis.
public class MeasurementPutter {
    private static final Log LOG = LogFactory.getLog(MeasurementPutter.class);

    private AmazonKinesis kinesis;
    private String streamName;
    private final int numUsers = 1;
    private final ObjectMapper JSON = new ObjectMapper();
	private Date time1 = new Date();
	private Date time2 = new Date();
        

    public MeasurementPutter(AmazonKinesis kinesis, String streamName) {

        if (kinesis == null) {
            throw new IllegalArgumentException("kinesis must not be null");
        }
        if (streamName == null || streamName.isEmpty()) {
            throw new IllegalArgumentException("streamName must not be null or empty");
        }
        this.kinesis = kinesis;
        this.streamName = streamName;
    }

    // Send a fixed number of data records to Amazon Kinesis. 
    //public void sendMeasurements(long n, long delayBetweenRecords, TimeUnit unitForDelay) throws InterruptedException {
      //  for (int i = 0; i < n && !Thread.currentThread().isInterrupted(); i++) {
        //    sendMeasurement();
          //  Thread.sleep(unitForDelay.toMillis(delayBetweenRecords));
       // }
   // }

    // Continuously sends data records to Amazon Kinesis sequentially
    public void sendMeasurementsIndefinitely(long delayBetweenRecords, TimeUnit unitForDelay) throws InterruptedException {
        while (!Thread.currentThread().isInterrupted()) {
			time1.setTime(System.currentTimeMillis());
			LOG.info(String.format("time before written into stream is %s \n", toISO8601UTC(time1)));
                for (int i=1; i<=numUsers; i++){
                    sendMeasurement(i);
                }
			time2.setTime(System.currentTimeMillis());
			LOG.info(String.format("time after written into stream is %s \n", toISO8601UTC(time2)));
			LOG.info(String.format("time differnce before and after written into stream is %d \n", (time2.getTime() - time1.getTime())));
			long sleepTime = time2.getTime() - time1.getTime();
			if (sleepTime < 0) {sleepTime = 0;};
            Thread.sleep(sleepTime);           
        }
    }

    // Send a single record to Amazon Kinesis using PutRecord.
    private void sendMeasurement(int i) {

	final RawMeasurement rawMeasurement = new RawMeasurement();
        rawMeasurement.setHost("user"+ i);
        byte[] bytes;
        try {
            bytes = JSON.writeValueAsBytes(rawMeasurement);
        } catch (IOException e) {
            LOG.warn("Skipping rawMeasurement. Unable to serialize: '" + rawMeasurement + "'", e);
            return;
       }

        PutRecordRequest putRecord = new PutRecordRequest();
        putRecord.setStreamName(streamName);
        putRecord.setPartitionKey("key");
                putRecord.setData(ByteBuffer.wrap(bytes));
        putRecord.setSequenceNumberForOrdering(null);

        try {
            kinesis.putRecord(putRecord);
            LOG.info(String.format("one data record is put in stream, data include: %s \n", rawMeasurement.toString()));
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

	public static String toISO8601UTC(Date date) {
  	  TimeZone tz = TimeZone.getTimeZone("UTC");
  	  DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
  	  df.setTimeZone(tz);
  	  return df.format(date);
  	}	

}


