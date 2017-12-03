package org.example.basicApp.model;

public class SingleMeasurementValue {
	
	private String measurementName;
	private String value;

	
	public SingleMeasurementValue (String name, String value) {
		
		this.measurementName = name;
		this.value = value;
		
	}
	
	
	public String getMeasurementName() {
		
		return measurementName;
	}
	
	public void setMeasurementName(String name) {
		
		this.measurementName = name;
	}
	
	
	public String getValue() {
		
		return value;
	}
	
	public void setValue (String value) {
		
		this.value = value;
	}
	
   @Override
    public String toString() {
        return String.format("%s %s \n",measurementName, value);
    }

}