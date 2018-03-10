package org.example.basicApp.model;

import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;

// main data record class with 6 measurement metrics generated randomly
public class RawMeasurement {

    private final static ObjectMapper JSON = new ObjectMapper();
    static {
        JSON.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    private String resource;
    private String timestamp;
    private String host;
    private String rawData;
    
    public RawMeasurement() {
        
        Date date = new Date();
        date.setTime(System.currentTimeMillis());
        
        this.resource = "EEG sensor";
        this.timestamp = toISO8601UTC(date);
        this.host = "user1";    
        this.rawData = generateRawData();
    
    }
     
    public String getResource() {
        return resource;
    }
   
    public void setResource(String resource) {
        this.resource = resource;
    }

    public String getTimeStamp() {
        return timestamp;
    }

    public void setTimeStamp(String timestamp) {
        this.timestamp = timestamp;
    }

    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    public String getRawData() {
        return rawData;
    }

    public void setRawData (String rawData) {
        this.rawData = rawData;
    }

    
    public byte[] toJsonAsBytes() {
        try {
            return JSON.writeValueAsBytes(this);
        } catch (IOException e) {
            return null;
        }
    }
    
    public static RawMeasurement fromJsonAsBytes(byte[] bytes) {
        try {
            return JSON.readValue(bytes, RawMeasurement.class);
        } catch (IOException e) {
            return null;
        }
    }
    
     
    public static String toISO8601UTC(Date date) {
  	  TimeZone tz = TimeZone.getTimeZone("America/Chicago");
  	  DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
  	  df.setTimeZone(tz);
  	  return df.format(date);
  	}
    
    private static String generateRawData() {
    	String rawData= "";
    	
    	// 7000 iteration generates ~204KB of data
    	for (int i=0; i<7000; i++) {
    		rawData += "ABCDEFGHIJKLMNOPQRSTUVWXYZZZZ-";
    	}   	
    	
    	return rawData;
    }
    
    @Override
    public String toString() {
        return String.format("Current measurement of values: %s %s %s %s",
               resource, timestamp, host, rawData);
    }
}
