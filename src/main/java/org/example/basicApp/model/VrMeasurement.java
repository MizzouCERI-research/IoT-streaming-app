
package org.example.basicApp.model;

import java.io.IOException;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Random;

import org.example.basicApp.ddb.MeasurementRecordMarshaller;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMarshalling;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBRangeKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

public class VrMeasurement {

	private static final float deviation = 0.1f;
	Random rand = new Random();	

    private final static ObjectMapper JSON = new ObjectMapper();
    static {
        JSON.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    private String resource;
    private String timestamp;
    private String host;
    private String engagement;
    private String focus;
    private String excitement;
    private String frustration;
    private String stress;
    private String relaxation;

	private Map<String, AttributeValue> measurementValues;
	
    public VrMeasurement() {
        
        Date date = new Date();
        date.setTime(System.currentTimeMillis());
        String formattedDate = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss").format(date);
        
        this.resource = "EEG sensor";
        this.timestamp = formattedDate;
        this.host = "host";    	
    	this.engagement = getRandomFloat(0.9f).toString();
        this.focus = getRandomFloat(0.9f).toString();
        this.excitement = getRandomFloat(0.7f).toString();
        this.frustration = getRandomFloat(0.2f).toString();
        this.stress = getRandomFloat(0.1f).toString();
        this.relaxation = getRandomFloat(0.7f).toString();
    }
    
   
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

    //@DynamoDBAttribute(attributeName = "engagement")
    public String getEngagement() {
        return engagement;
    }

    public void setEngagement(String engagement) {
        this.engagement = engagement;
    }
    
    //@DynamoDBAttribute(attributeName = "focus")
    public String getFocus() {
        return focus;
    }

    public void setFocus(String focus) {
        this.focus = focus;
    }
    
    //@DynamoDBAttribute(attributeName = "excitement")
    public String getExcitement() {
        return excitement;
    }

    public void setExcitement(String excitement) {
        this.excitement = excitement;
    }
    
    //@DynamoDBAttribute(attributeName = "frustration")
    public String getFrustration() {
        return frustration;
    }

    public void setFrustration(String frustration) {
        this.frustration = frustration;
    }
    
    //@DynamoDBAttribute(attributeName = "stress")
    public String getStress() {
        return stress;
    }

    public void setStress(String stress) {
        this.stress = stress;
    }
   
    //@DynamoDBAttribute(attributeName = "relaxation")
    public String getRelaxation() {
        return relaxation;
    }

    public void setRelaxation(String relaxation) {
        this.relaxation = relaxation;
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
    
    
    
    
    
    public byte[] toJsonAsBytes() {
        try {
            return JSON.writeValueAsBytes(this);
        } catch (IOException e) {
            return null;
        }
    }
    
    public static VrMeasurement fromJsonAsBytes(byte[] bytes) {
        try {
            return JSON.readValue(bytes, VrMeasurement.class);
        } catch (IOException e) {
            return null;
        }
    }
    
    public Float getRandomFloat(Float mean) {
        
    	// set the price using the deviation and mean price
        
    	Float max = mean + deviation;
    	Float min = mean - deviation;

        // randomly pick a quantity of shares
        Float value = rand.nextFloat() * (max - min) + min; 

        return value;
    }
    
    @Override
    public String toString() {
        return String.format("Current measurement of values: %s %s %s %s %s %s %s %s %s ",
               resource, timestamp, host, engagement, focus, excitement, frustration, stress, relaxation);
    }

}
