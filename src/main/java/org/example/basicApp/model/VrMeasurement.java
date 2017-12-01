
package org.example.basicApp.model;

import java.io.IOException;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Random;

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

	/*		
	DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
	Date date = new Date();
	//System.out.println(dateFormat.format(date));			
    // Repeatedly send measurements with a 1000 milliseconds wait in between
	VrMeasurement vrMeasurement = new VrMeasurement("EEG-sensor", dateFormat.format(date) ,"host", getRandomFloat(0.9f).toString(), getRandomFloat(0.9f).toString(),getRandomFloat(0.7f).toString(),
			getRandomFloat(0.2f).toString(),getRandomFloat(0.1f).toString(),getRandomFloat(0.7f).toString());
*/	
	
		//DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		//Date date = new Date();
		//System.out.println(dateFormat.format(date));			
    
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

    public String getResource() {
        return resource;
    }

    public String getTimeStamp() {
        return timestamp;
    }

    public String getHost() {
        return host;
    }    
    
    public String getEngagement() {
        return engagement;
    }

    public String getFocus() {
        return focus;
    }

    public String getExcitement() {
        return excitement;
    }

    public String getFrustration() {
        return frustration;
    }

    public String getStress() {
        return stress;
    }

    public String getRelaxation() {
        return relaxation;
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
