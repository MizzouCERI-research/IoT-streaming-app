package org.example.basicApp.muse;

import java.io.File;
//import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
//import java.io.PrintStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.TimeZone;

//import javax.xml.bind.JAXBElement.GlobalScope;

import org.example.basicApp.ddb.DynamoDBWriter;
import org.example.basicApp.model.DdbRecordToWrite;
import org.example.basicApp.model.SingleMeasurementValue;
import org.example.basicApp.model.VrMeasurement;
import org.example.basicApp.utils.DynamoDBUtils;
import org.example.basicApp.utils.SampleUtils;

import com.amazonaws.ClientConfiguration;
import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.DefaultAWSCredentialsProviderChain;
import com.amazonaws.regions.Region;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDB;
import com.amazonaws.services.dynamodbv2.AmazonDynamoDBClient;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.DescribeTableRequest;
import com.amazonaws.services.dynamodbv2.model.PutItemRequest;
import com.amazonaws.services.dynamodbv2.model.PutItemResult;
import com.amazonaws.services.dynamodbv2.model.TableDescription;
//import com.amazonaws.services.kinesis.AmazonKinesis;
//import com.amazonaws.services.kinesis.AmazonKinesisClient;


import oscP5.*;
//import org.example.basicApp.muse.oscP5.*;
//import java.util.HashMap;
//import java.util.Map; 

public class MuseOscServer {

    static MuseOscServer museOscServer;
    OscP5 museServer;    
    static int recvPort = 5002;
    static AmazonDynamoDB dynamoDB;
    private static final int numUsers=1;
    private static DdbRecordToWrite ddbRecordToWrite;
    private static String dynamoTableName;
    
    public static void main(String[] args) throws FileNotFoundException {
    	
//    	File filename = new File("trial.csv");
//    	PrintStream o = new PrintStream(filename);
//    	System.setOut(o);
    	
    	if(args.length != 2) {
            System.err.println("Usage: " + DynamoDBWriter.class.getSimpleName()
                    + "<DynamoDB table name> <region>");
            System.exit(1);
            }
            
    	    dynamoTableName = args[0];
    	    Region region = SampleUtils.parseRegion(args[1]);
    	    
            AWSCredentialsProvider credentialsProvider = new DefaultAWSCredentialsProviderChain();
            ClientConfiguration clientConfig = SampleUtils.configureUserAgentForSample(new ClientConfiguration());
           // AmazonKinesis kinesis = new AmazonKinesis(credentialsProvider);
           // kinesis.setRegion(region);
            dynamoDB = new AmazonDynamoDBClient(credentialsProvider, clientConfig);
            dynamoDB.setRegion(region);
             
            DynamoDBUtils dynamoDBUtils = new DynamoDBUtils(dynamoDB);
            dynamoDBUtils.createDynamoTableIfNotExists(dynamoTableName);
            //LOG.info(String.format("%s DynamoDB table is ready for use", dynamoTableName));
    	
            // Describe our new table
            DescribeTableRequest describeTableRequest = new DescribeTableRequest().withTableName(dynamoTableName);
            TableDescription tableDescription = dynamoDB.describeTable(describeTableRequest).getTable();
            System.out.println("Table Description: " + tableDescription);

    	museOscServer = new MuseOscServer();
    	museOscServer.museServer = new OscP5(museOscServer,recvPort);
    }
    void oscEvent(OscMessage meg) throws IOException{

    	System.out.println("Start receiving EEG Data!");       

        VrMeasurement measurementRecord =  new VrMeasurement();	            
        try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}  
        for (int i=1; i<numUsers+1; i++) {
            	ddbRecordToWrite = generateDBRecord(meg, measurementRecord, "user"+i);
            	System.out.println("I am here at 6!");       

                System.out.printf("record ready to write for user %s is: %s \n" ,i, ddbRecordToWrite.toString());

                Map<String, AttributeValue> item = newItem(ddbRecordToWrite);			            
                System.out.println(""+item.get("resource")+",");  
                        
                System.out.print(""+item.get("timestamp")+",");                    
                System.out.print(""+item.get("host")+",");                    
                System.out.print(""+item.get("values"));                    
                System.out.println("\n");
			            	
			    try {        	
	                PutItemRequest putItemRequest = new PutItemRequest(dynamoTableName, item);
	            	System.out.println("I am here at 10!");       
	
		            PutItemResult putItemResult = dynamoDB.putItem(putItemRequest);
	            	System.out.println("I am here at 11!");       
	
		            System.out.println("Result: " + putItemResult);	
	            	System.out.println("I am here at 12!");       
			    } catch (Exception ex) {
			    	ex.printStackTrace();
			    }
	            		            
	     }
	             
	 }

    private static DdbRecordToWrite generateDBRecord(OscMessage msg, VrMeasurement measurementRecord, String user) {
    	
    	DdbRecordToWrite ddbRecordToWrite = new DdbRecordToWrite();
        ddbRecordToWrite.setResource(measurementRecord.getResource());
        //ddbRecordToWrite.setTimeStamp(measurementRecord.getTimeStamp());
        ddbRecordToWrite.setHost(user);
        
        Date date = new Date();
        date.setTime(System.currentTimeMillis());
        ddbRecordToWrite.setTimeStamp(toISO8601UTC(date));
        
     
        Object[] arguments=msg.arguments();
    	System.out.println("I am here at 1!");       

        Float avg1= 0.0f;
        float avg2=0;
        float avg3=0;
        float avg4=0;
        float avg5=0;
        float avg6=0;
        
        
		Double A = (Double)arguments[0];
		Double B = (Double)arguments[1];
		Double C = (Double)arguments[2];
		Double D = (Double)arguments[3];
		
		Float AA = A.floatValue();
		Float BB = A.floatValue();
		Float CC = A.floatValue();
		Float DD = A.floatValue();
		
		
		System.out.printf("B is %f \n", B);
		
		System.out.println(arguments[0].getClass());
    	System.out.println("I am here at 2! ");  
        
    	ArrayList<SingleMeasurementValue> measurementValues = new ArrayList<SingleMeasurementValue>();
    	System.out.println("I am here at 3!");       

//		avg1 = (A.floatValue()+B.floatValue()+C.floatValue()+D.floatValue())/4.0f;
//       	SingleMeasurementValue value1 = new SingleMeasurementValue("{\"measurement\":\"engagement\",\"value\":", AA,"}");
//    	SingleMeasurementValue value2 = new SingleMeasurementValue("{\"measurement\":\"focus\",\"value\":", BB,"}");
//    	SingleMeasurementValue value3 = new SingleMeasurementValue("{\"measurement\":\"excitement\",\"value\":", CC,"}");
//    	SingleMeasurementValue value4 = new SingleMeasurementValue("{\"measurement\":\"frustration\",\"value\":", DD,"}");
//    	SingleMeasurementValue value5 = new SingleMeasurementValue("{\"measurement\":\"stress\",\"value\":", CC,"}");
//    	SingleMeasurementValue value6 = new SingleMeasurementValue("{\"measurement\":\"relaxation\",\"value\":", DD,"}");
//		measurementValues.add(value1);
//		measurementValues.add(value2);
//		measurementValues.add(value3);
//		measurementValues.add(value4);
//		measurementValues.add(value5);
//		measurementValues.add(value6);
    	System.out.println("I am here at 4!");       

    	
    	if(msg.checkAddrPattern("/elements/alpha_absolute")==true) {
    		avg1 = (AA + BB + CC+ DD)/4;
    		SingleMeasurementValue value1 = new SingleMeasurementValue("{\"measurement\":\"alpha\",\"value\":",avg1,"}");
    		measurementValues.add(value1);
       		}
    	if(msg.checkAddrPattern("/elements/beta_absolute")==true) {
    		avg2 = (AA + BB + CC+ DD)/4;
    		SingleMeasurementValue value2 = new SingleMeasurementValue("{\"measurement\":\"beta\",\"value\":",avg2,"}");
    		measurementValues.add(value2);
    		}
    	if(msg.checkAddrPattern("/elements/gamma_absolute")==true) {
    		avg3 = (AA + BB + CC+ DD)/4;
    		SingleMeasurementValue value3 = new SingleMeasurementValue("{\"measurement\":\"gamma\",\"value\":",avg3,"}");
    		measurementValues.add(value3);
    		}
    	if(msg.checkAddrPattern("/elements/delta_absolute")==true) {
    		avg4 = (AA + BB + CC+ DD)/4;
    		SingleMeasurementValue value4 = new SingleMeasurementValue("{\"measurement\":\"delta\",\"value\":",avg4,"}");
    		measurementValues.add(value4);
    		}
    	if(msg.checkAddrPattern("/elements/theta_absolute")==true) {
    		avg5 = (AA + BB + CC+ DD)/4;
    		SingleMeasurementValue value5 = new SingleMeasurementValue("{\"measurement\":\"theta\",\"value\":",avg5,"}");
    		measurementValues.add(value5);
    		}
    	if(msg.checkAddrPattern("/eeg")==true) {
    		avg6 = (AA + BB + CC+ DD)/4;
    		SingleMeasurementValue value6 = new SingleMeasurementValue("{\"measurement\":\"eeg\",\"value\":",avg6,"}");
    		measurementValues.add(value6);
    		}
       	
        ddbRecordToWrite.setValues(measurementValues);		            	            
    	System.out.println("I am here at 5!"); 
    	return ddbRecordToWrite;
    }



    private static Map<String, AttributeValue> newItem(DdbRecordToWrite record) {
    	
    	Map<String, AttributeValue> item = new HashMap<String, AttributeValue>();
        item.put("resource", new AttributeValue(record.getResource()));
        item.put("timestamp", new AttributeValue(record.getTimeStamp()));
        item.put("host", new AttributeValue(record.getHost()));
        item.put("values", new AttributeValue(record.getValues().toString()));


        return item;
    }

    public static String toISO8601UTC(Date date) {
    	  TimeZone tz = TimeZone.getTimeZone("UTC");
    	  DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
    	  df.setTimeZone(tz);
    	  return df.format(date);
    	}    
}
