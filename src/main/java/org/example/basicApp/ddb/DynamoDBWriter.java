package org.example.basicApp.ddb;

import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.example.basicApp.client.MeasurementProcessor;
import org.example.basicApp.model.VrMeasurement;
import org.example.basicApp.utils.DynamoDBUtils;
import org.example.basicApp.utils.SampleUtils;
import org.example.basicApp.utils.StreamUtils;

import com.amazonaws.AmazonClientException;
import com.amazonaws.AmazonServiceException;
import com.amazonaws.ClientConfiguration;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.DefaultAWSCredentialsProviderChain;
import com.amazonaws.auth.profile.ProfileCredentialsProvider;
import com.amazonaws.regions.Region;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
//import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClientBuilder;
import com.amazonaws.services.dynamodbv2.model.AttributeDefinition;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.ComparisonOperator;
import com.amazonaws.services.dynamodbv2.model.Condition;
import com.amazonaws.services.dynamodbv2.model.CreateTableRequest;
import com.amazonaws.services.dynamodbv2.model.DescribeTableRequest;
import com.amazonaws.services.dynamodbv2.model.KeySchemaElement;
import com.amazonaws.services.dynamodbv2.model.KeyType;
import com.amazonaws.services.dynamodbv2.model.ProvisionedThroughput;
import com.amazonaws.services.dynamodbv2.model.PutItemRequest;
import com.amazonaws.services.dynamodbv2.model.PutItemResult;
import com.amazonaws.services.dynamodbv2.model.ScalarAttributeType;
import com.amazonaws.services.dynamodbv2.model.ScanRequest;
import com.amazonaws.services.dynamodbv2.model.ScanResult;
import com.amazonaws.services.dynamodbv2.model.TableDescription;
import com.amazonaws.services.dynamodbv2.util.TableUtils;
import com.amazonaws.services.kinesis.AmazonKinesis;
import com.amazonaws.services.kinesis.AmazonKinesisClient;

/**
 * This sample demonstrates how to perform a few simple operations with the
 * Amazon DynamoDB service.
 */
public class DynamoDBWriter {
	
    private static final Log LOG = LogFactory.getLog(DynamoDBWriter.class);

    static AmazonDynamoDB dynamoDB;
        
    public static void main(String[] args) throws Exception {
    	
        if(args.length != 2) {
        System.err.println("Usage: " + DynamoDBWriter.class.getSimpleName()
                + "<DynamoDB table name> <region>");
        System.exit(1);
        }
        
	    String dynamoTableName = args[0];
	    Region region = SampleUtils.parseRegion(args[1]);
	    
        AWSCredentialsProvider credentialsProvider = new DefaultAWSCredentialsProviderChain();
        ClientConfiguration clientConfig = SampleUtils.configureUserAgentForSample(new ClientConfiguration());
        AmazonKinesis kinesis = new AmazonKinesisClient(credentialsProvider, clientConfig);
        kinesis.setRegion(region);
        AmazonDynamoDB dynamoDB = new AmazonDynamoDBClient(credentialsProvider, clientConfig);
        dynamoDB.setRegion(region);
         
        DynamoDBUtils dynamoDBUtils = new DynamoDBUtils(dynamoDB);
        dynamoDBUtils.createDynamoTableIfNotExists(dynamoTableName);
        LOG.info(String.format("%s DynamoDB table is ready for use", dynamoTableName));
	
        // Describe our new table
        DescribeTableRequest describeTableRequest = new DescribeTableRequest().withTableName(dynamoTableName);
        TableDescription tableDescription = dynamoDB.describeTable(describeTableRequest).getTable();
        System.out.println("Table Description: " + tableDescription);
        
        try {
        	
            while(true) {
	            
	            // Add an item
	            VrMeasurement measurementRecord =  new VrMeasurement();
	            System.out.println("record generated: %s " + measurementRecord.toString());
	            Map<String, AttributeValue> item = newItem(measurementRecord);
	            for (Map.Entry entry : item.entrySet())
	            {
	                System.out.println("key: " + entry.getKey() + "; value: " + entry.getValue());
	            }
	            
	            PutItemRequest putItemRequest = new PutItemRequest(dynamoTableName, item);
	            PutItemResult putItemResult = dynamoDB.putItem(putItemRequest);
	            System.out.println("Result: " + putItemResult);	 
	            Thread.sleep(1000);
            }

        } catch (AmazonServiceException ase) {
            System.out.println("Caught an AmazonServiceException, which means your request made it "
                    + "to AWS, but was rejected with an error response for some reason.");
            System.out.println("Error Message:    " + ase.getMessage());
            System.out.println("HTTP Status Code: " + ase.getStatusCode());
            System.out.println("AWS Error Code:   " + ase.getErrorCode());
            System.out.println("Error Type:       " + ase.getErrorType());
            System.out.println("Request ID:       " + ase.getRequestId());
        } catch (AmazonClientException ace) {
            System.out.println("Caught an AmazonClientException, which means the client encountered "
                    + "a serious internal problem while trying to communicate with AWS, "
                    + "such as not being able to access the network.");
            System.out.println("Error Message: " + ace.getMessage());
        }
    }
/*
    private static Map<String, AttributeValue> newItem(String name, int year, String rating, String... fans) {
        Map<String, AttributeValue> item = new HashMap<String, AttributeValue>();
        item.put("name", new AttributeValue(name));
        item.put("year", new AttributeValue().withN(Integer.toString(year)));
        item.put("rating", new AttributeValue(rating));
        item.put("fans", new AttributeValue().withSS(fans));

        return item;
    }
*/    
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