
package org.example.basicApp.writer;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import java.util.concurrent.TimeUnit;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import com.amazonaws.ClientConfiguration;
import com.amazonaws.AmazonClientException;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.AmazonClientException;
import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.auth.DefaultAWSCredentialsProviderChain;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.RegionUtils;
import com.amazonaws.services.kinesis.AmazonKinesis;
import com.amazonaws.services.kinesis.AmazonKinesisClient;
import com.amazonaws.services.kinesis.model.DescribeStreamResult;
import com.amazonaws.services.kinesis.model.PutRecordRequest;
import com.amazonaws.services.kinesis.model.ResourceNotFoundException;
import org.example.basicApp.model.VrMeasurement;
import org.example.basicApp.writer.MeasurementPutter;
import org.example.basicApp.utils.ConfigurationUtils;
import org.example.basicApp.utils.CredentialUtils;
import org.example.basicApp.utils.StreamUtils;
import org.example.basicApp.utils.SampleUtils;


/**
 * This 1.3 version of the writter is modified from version 1.2 with added threads
 */



/**
 * Continuously sends simulated measurement to Kinesis
 *
 */
public class MeasurementWriter {

    private static final Log LOG = LogFactory.getLog(MeasurementWriter.class);

   /**
     * The amount of time to wait between records.
     *
     * We want to send at most 10 records per second per thread so we'll delay 100ms between records.
     * This keeps the overall cost low for this sample.
     */
    private static final long DELAY_BETWEEN_RECORDS_IN_MILLIS = 1000;

    /**
     * Start a number of threads and send randomly generated measurements to a Kinesis Stream until the
     * program is terminated.
     *
     * @param args Expecting 3 arguments: A numeric value indicating the number of threads to use to send
     *        data to Kinesis and the name of the stream to send records to, and the AWS region in which these resources
     *        exist or should be created.
     * @throws InterruptedException If this application is interrupted while sending records to Kinesis.
     */
	
    private static void checkUsage(String[] args) {
        if (args.length != 2) {
            System.err.println("Usage: " + MeasurementWriter.class.getSimpleName()
                    + " <stream name> <region>");
            System.exit(1);
        }
    }

    public static void main(String[] args) throws Exception {
        checkUsage(args);

        String streamName = args[0];
        String regionName = args[1];
        Region region = RegionUtils.getRegion(regionName);
        if (region == null) {
            System.err.println(regionName + " is not a valid AWS region.");
            System.exit(1);
        }

        //AWSCredentials credentials = CredentialUtils.getCredentialsProvider().getCredentials();
        AWSCredentialsProvider credentials = new DefaultAWSCredentialsProviderChain();
        ClientConfiguration clientConfig = SampleUtils.configureUserAgentForSample(new ClientConfiguration());
        AmazonKinesis kinesisClient = new AmazonKinesisClient(credentials, ConfigurationUtils.getClientConfigWithUserAgent());
        kinesisClient.setRegion(region);
        
        // Creates a stream to write to with 1 shards if it doesn't exist
			StreamUtils streamUtils = new StreamUtils(kinesisClient);
			streamUtils.createStreamIfNotExists(streamName, 1);
			LOG.info(String.format("%s stream is ready for use", streamName));

	        // Repeatedly send measurements with a 1000 milliseconds wait in between
			//VrMeasurement vrMeasurement = new VrMeasurement();		
		
		final MeasurementPutter measurementPutter = new MeasurementPutter(/*vrMeasurement,*/ kinesisClient, streamName);

		ExecutorService es = Executors.newCachedThreadPool();

        Runnable measurementSender = new Runnable() {
            @Override
            public void run() {
                try {
                    measurementPutter.sendMeasurementsIndefinitely(DELAY_BETWEEN_RECORDS_IN_MILLIS, TimeUnit.MILLISECONDS);
                } catch (Exception ex) {
                    LOG.warn("Thread encountered an error while sending records. Records will no longer be put by this thread.",
                            ex);
                }
            }
        };

        es.submit(measurementSender);        

        LOG.info(String.format("Sending measurements with a %dms delay between records .",
                DELAY_BETWEEN_RECORDS_IN_MILLIS));

        es.shutdown();
        es.awaitTermination(Long.MAX_VALUE, TimeUnit.DAYS);
        
    }

}
