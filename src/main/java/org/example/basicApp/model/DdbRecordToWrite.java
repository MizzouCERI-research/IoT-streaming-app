package org.example.basicApp.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.example.basicApp.ddb.MeasurementRecordMarshaller;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMarshalling;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBRangeKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;


@DynamoDBTable(tableName = "measurementDBTable")
public class DdbRecordToWrite {
	
    private String resource;
    private String timestamp;
    private String host;
	private List<SingleMeasurementValue> measurementValues = new ArrayList<SingleMeasurementValue>();

/*	
	public DdbRecordToWrite (VrMeasurement vrMeasurement) {
		
       	this.resource = vrMeasurement.getResource();
        this.timestamp = vrMeasurement.getTimeStamp();
        this.host = vrMeasurement.getHost();    	
        //setValues(vrMeasurement);

		
	}
*/	
	@DynamoDBHashKey(attributeName = "resource")
	public String getResource() {
	    return resource;
	}
	
	public void setResource(String resource) {
	    this.resource = resource;
	}
	
	
	@DynamoDBRangeKey(attributeName = "timestamp")
	public String getTimeStamp() {
	    return timestamp;
	}
	
	public void setTimeStamp(String timestamp) {
	    this.timestamp = timestamp;
	}
	
	@DynamoDBAttribute(attributeName = "host")
	public String getHost() {
	    return host;
	}

    public void setHost(String host) {
        this.host = host;
    }
    
    @DynamoDBAttribute(attributeName = "values")
    @DynamoDBMarshalling(marshallerClass = MeasurementRecordMarshaller.class)
    public List<SingleMeasurementValue> getValues() {
        return measurementValues;
    }
/*
    public void setValues(VrMeasurement vrMeasurement) {
    	
    	SingleMeasurementValue value1 = new SingleMeasurementValue("engagement", vrMeasurement.getEngagement());
    	System.out.println("value1 is " + value1.toString()+ "\n");
    	SingleMeasurementValue value2 = new SingleMeasurementValue("focus", vrMeasurement.getFocus());
    	SingleMeasurementValue value3 = new SingleMeasurementValue("excitement", vrMeasurement.getExcitement());
    	SingleMeasurementValue value4 = new SingleMeasurementValue("frustration", vrMeasurement.getFrustration());
    	SingleMeasurementValue value5 = new SingleMeasurementValue("stress", vrMeasurement.getStress());
    	SingleMeasurementValue value6 = new SingleMeasurementValue("relaxation", vrMeasurement.getRelaxation());
    	
    	this.measurementValues.add(value1);
		this.measurementValues.add(value2);
		this.measurementValues.add(value3);
		this.measurementValues.add(value4);
		this.measurementValues.add(value5);
		this.measurementValues.add(value6);
        
    }    
 */
    public void setValues(List<SingleMeasurementValue> measurementValues) {

    	this.measurementValues = measurementValues;
        
    }    
    
    
    @Override
    public String toString() {
        return String.format("Measurement record ready to write into DB is: %s %s %s %s \n",
               resource, timestamp, host, 
               measurementValues.get(0).getMeasurementName(), measurementValues.get(0).getValue(),
               measurementValues.get(1).getMeasurementName(), measurementValues.get(1).getValue(),
               measurementValues.get(2).getMeasurementName(), measurementValues.get(2).getValue(),
               measurementValues.get(3).getMeasurementName(), measurementValues.get(3).getValue(),
               measurementValues.get(4).getMeasurementName(), measurementValues.get(4).getValue(),
               measurementValues.get(5).getMeasurementName(), measurementValues.get(5).getValue()
        		);
    }

}