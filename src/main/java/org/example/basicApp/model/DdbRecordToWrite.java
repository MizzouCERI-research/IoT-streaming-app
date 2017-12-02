package org.example.basicApp.model;

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
	private Map<String, AttributeValue> measurementValues;
    //private List<SingleMeasurementValue> measurementValues;

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
    
    @DynamoDBAttribute
    @DynamoDBMarshalling(marshallerClass = MeasurementRecordMarshaller.class)
    public Map<String, AttributeValue> getValues() {
        return measurementValues;
    }

    
    public void setValues(VrMeasurement record) {
  
		//Map<String, AttributeValue> item = new HashMap<String, AttributeValue>();
		
		this.measurementValues.put("engagement", new AttributeValue(record.getEngagement()));
		this.measurementValues.put("focus", new AttributeValue(record.getFocus()));
		this.measurementValues.put("excitement", new AttributeValue(record.getExcitement()));
		this.measurementValues.put("frustration", new AttributeValue(record.getFrustration()));
		this.measurementValues.put("stress", new AttributeValue(record.getStress()));
		this.measurementValues.put("relaxation", new AttributeValue(record.getRelaxation()));
	}
	
}