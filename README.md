# IoT Streaming and Visualization Application using AWS Kinesis/DynamoDB

This repo is an application that collects sensor data from EEG IoT devices, and send them to AWS Kinesis streams for storage in DynamoDB, and visualize on http servlet. 

The IoT application can be run in three different architecture: **Cloud-only**, **Edge-cloud**, and **Edge-only**, as illustrated below: 
<!-- 
[Architecture] (https://github.com/MizzouCERI-research/IoT-streaming-app/blob/master/img/architecture.png)
 -->
<img width="800" alt="image" src="https://user-images.githubusercontent.com/10638886/178800334-dd2c2d19-3ea2-4aa0-8352-96fe876b2eab.png">

### Cloud-only architecture
- Raw data are collected from IoT sensors and sent to Kinesis stream
- Kinesis stream/Shards
- EC2 instance as RAW data processor, 
- Processor processes data, and send the processed data to DynamoDB
- RAW data can be stored on S3 
- Processed data are visualized via http servlet pulling data from DynamoDB

### Edge-cloud architecture
- Edge computer collect data from IoT sensors, and processes RAW data
- Processed data are sent to and stored in DynamoDB
- No data processor needed in the cloud
- Processed data can be stored on S3 
- Processed data are visualized via http servlet pulling data from DynamoDB

### Edge-only architecture
- Edge computer collect and process RAW data
- Processed data are sent to and stored in DynamoDB-local
- No Kinesis streams needed
- Processed data are visualized via http servlet pulling data from DynamoDB-local
 

## Running the app in Cloud-Only architecture:

1. Use local computer or create an AWS EC2 instance and attach an IAM role that has Kinesis/DynamoDB full access.
2. Clone this repo to local computer or the EC2 instance 
3. Setup the following: 

	a. Install JAVA SDK: look up how to on internet
	
	b. Install Apache Maven compiler: look up how to on internet
	
	c. Add both maven compiler and JAVA SDK to your environment path variable (if they are not in there already)
	
	d. Configure AWS credentials (accessKey and secretKey) if using local computer

4. From inside the repo root directory, run the following commands (replace ??? with your AWS account accessKeyId and secretKey, or create an environment variable as below), each in a separate terminal:
		
	```bash
	$ MAVEN_OPTS="-Daws.accessKeyId=??? -Daws.secretKey=???" mvn compile -Pstream-writer exec:java
	$ MAVEN_OPTS="-Daws.accessKeyId=??? -Daws.secretKey=???" mvn compile -PclientApp exec:java
	$ MAVEN_OPTS="-Daws.accessKeyId=??? -Daws.secretKey=???" mvn compile -Pwebserver exec:java
	```
	Open web browser: type http://localhost:8080 (if not working, use http://EC2_IP:8080/overview.html) to visualize sensor data

	or

	```bash
	$ export MAVEN_OPTS="-Daws.accessKeyId=??? -Daws.secretKey=???"
	$ mvn compile -Pstream-writer exec:java
	$ mvn compile -PclientApp exec:java
	$ mvn compile -Pwebserver exec:java
	``` 
	Open from web browser: http://localhost:8080 (if not working, use http://EC2_IP:8080/overview.html) to visualize sensor data

5. To change data rate:

   a. Open /src/main/java/org/example/basicApp/writer/MeasurementWriter.java, 
   b. Find and change the value of the variable "DELAY_BETWEEN_RECORDS_IN_MILLIS" from 1000 to 5000 (5 sec) or 10000 (10 sec).
   c. Open visualApp/src/main/static-content/wwwroot/overview.js, 
   d. Find and change the variable "data_interval" from 1000 to 5000 (5 sec) or 10000 (10 sec) similar to step 2.a.

6. To change name of DynamoDB: Edit visualApp/pom.xml (rename: sample-application.name, sample-application.stream, and sample-application.measurement-table, to whatever you like)

7. To change number of users: Open visualApp/src/main/java/org/example/basicApp/ writer/MeasurementPutter.java

   Find and change the variable "numUsers" from 1 to 5, 10, etc. 	
	
	
	
## Running the application in Edge-Cloud architecture: 

1. 2. 3. All follow the Cloud-Only architecture steps, except only local computer is used 

4. From the repo root directory, type the following (replace ??? with your AWS account accessKeyId and secretKey, or create an environment variable as below), each in a separate terminal:
		
	```bash
	$ MAVEN_OPTS="-Daws.accessKeyId=??? -Daws.secretKey=???" mvn compile -PdbWriter exec:java
	$ MAVEN_OPTS="-Daws.accessKeyId=??? -Daws.secretKey=???" mvn compile -Pwebserver exec:java
	```
	Open from web browser: http://localhost:8080 (if not working, use http://localhost:8080/overview.html)

	or 

	```bash
	$ export MAVEN_OPTS="-Daws.accessKeyId=??? -Daws.secretKey=???"
	$ mvn compile -PdbWriter exec:java
	$ mvn compile -Pwebserver exec:java
	```

	Open from web browser: http://localhost:8080 (if not working, use http://localhost:8080/overview.html)
	
5. To change data rate:

   a. Open /src/main/java/org/example/basicApp/writer/MeasurementWriter.java, 
   b. Find and change the value of the variable "DELAY_BETWEEN_RECORDS_IN_MILLIS" from 1000 to 5000 (5 sec) or 10000 (10 sec).
   c. Open visualApp/src/main/static-content/wwwroot/overview.js, 
   d. Find and change the variable "data_interval" from 1000 to 5000 (5 sec) or 10000 (10 sec) similar to step 2.a.

6. To change name of DynamoDB: Edit visualApp/pom.xml (rename: sample-application.name, sample-application.stream, and sample-application.measurement-table, to whatever you like)

7. To change number of users: Open visualApp/src/main/java/org/example/basicApp/ writer/MeasurementPutter.java

   Find and change the variable "numUsers" from 1 to 5, 10, etc. 	
	

## We herein are not providing instructions for Edge-Only architecture 

1. Check DynamoDB-local manual: [DynamoDB-local](https://docs.aws.amazon.com/amazondynamodb/latest/developerguide/DynamoDBLocal.html)

2. Everything else is similar to Edge-Cloud architecture


## Visualization screenshots:

### To view the current EEG sensor data for selected users: 
<img width="800" alt="image" src="https://user-images.githubusercontent.com/10638886/178807441-026fae98-d847-41e4-a360-ed63f037d7f9.png">

### To view the historical EEG sensor data for a specific user: 
<img width="800" alt="image" src="https://user-images.githubusercontent.com/10638886/178807667-ff265a83-4b05-445b-94cd-30cd544601ed.png">


