package org.example.basicApp.model;

import java.util.Date;
//import java.util.List;

import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBAttribute;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBHashKey;
//import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBMarshalling;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBRangeKey;
import com.amazonaws.services.dynamodbv2.datamodeling.DynamoDBTable;

/**
 * A resource with referrers and the number of occurrences they referred to the resource over a given period of time.
 */
@DynamoDBTable(tableName = "measurementDBTable")
public class MeasurementDynamoDB {
	
	private String resource;
	
    // The timestamp when the counts were calculated
    private Date timestamp;
    // Store the hostname of the worker that updated the count
    private String host;
    // Ordered list of referrer counts in descending order. Top N can be simply obtained by inspecting the first N
    // counts.
    
    private String engagement;
    private String focus;
    private String excitement;
    private String frustration;
    private String stress;
    private String relaxation;
    
    @DynamoDBHashKey
    public String getResource() {
        return resource;
    }

    public void setResource(String resource) {
        this.resource = resource;
    }


    @DynamoDBRangeKey
    public Date getTimeStamp() {
        return timestamp;
    }

    public void setTimeStamp(Date timestamp) {
        this.timestamp = timestamp;
    }

    @DynamoDBAttribute
    public String getHost() {
        return host;
    }

    public void setHost(String host) {
        this.host = host;
    }

    @DynamoDBAttribute
    public String getEngagement() {
        return engagement;
    }

    public void setEngagement(String engagement) {
        this.engagement = engagement;
    }
    
    @DynamoDBAttribute
    public String getFocus() {
        return focus;
    }

    public void setFocus(String focus) {
        this.focus = focus;
    }
    
    @DynamoDBAttribute
    public String getExcitement() {
        return excitement;
    }

    public void setExcitement(String excitement) {
        this.excitement = excitement;
    }
    
    @DynamoDBAttribute
    public String getFrustration() {
        return frustration;
    }

    public void setFrustration(String frustration) {
        this.frustration = frustration;
    }
    
    @DynamoDBAttribute
    public String getStress() {
        return stress;
    }

    public void setStress(String stress) {
        this.stress = stress;
    }
    
    @DynamoDBAttribute
    public String getRelaxation() {
        return relaxation;
    }

    public void setRelaxation(String relaxation) {
        this.relaxation = relaxation;
    }
    
    
}
