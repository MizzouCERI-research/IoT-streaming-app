
package org.example.basicApp.model;

import java.io.IOException;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;


public class VrMeasurement {

    private final static ObjectMapper JSON = new ObjectMapper();
    static {
        JSON.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    private String resource;
    private String timeStamp;
    private String host;
    private String engagement;
    private String focus;
    private String excitement;
    private String frustration;
    private String stress;
    private String relaxation;


    public VrMeasurement() {
    }

    public VrMeasurement(String resource, String timeStamp, String host, String engagement, String focus, String excitement,String frustration,String stress,String relaxation) {
        this.resource = resource;
        this.timeStamp = timeStamp;
        this.host = host;    	
    	this.engagement = engagement;
        this.focus = focus;
        this.excitement = excitement;
        this.frustration = frustration;
        this.stress = stress;
        this.relaxation = relaxation;
    }

    public String getResource() {
        return resource;
    }

    public String getTimeStamp() {
        return timeStamp;
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

    @Override
    public String toString() {
        return String.format("Current measurement of values: %s %s %s %s %s %s %s %s %s ",
               resource, timeStamp, host, engagement, focus, excitement, frustration, stress, relaxation);
    }

}
