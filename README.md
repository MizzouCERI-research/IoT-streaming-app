# IoT Streaming and Visualization Application using AWS Kinesis/DynamoDB

This repo is an application that collects sensor data from EEG IoT devices, and send them to AWS Kinesis streams for storage in DynamoDB, and visualize on http servlet. 

The IoT application can be run in three different architecture: Cloud-only, Edge-cloud, and Edge-only, as illustrated below: 
<!-- 
[Architecture] (https://github.com/MizzouCERI-research/IoT-streaming-app/blob/master/img/architecture.png)
 -->
![Architecture](https://user-images.githubusercontent.com/10638886/178799429-c01bf642-6380-43e1-9889-fa235bfc65e8.png)

### Cloud-only architecture
- Raw data
- Kinesis stream/Shards
- EC2 instance processor
- DynamoDB + S3

### Edge-cloud architecture
- Processed data
- DynamoDB + S3

### Edge-only architecture
- Processed data
- DynamoDB-local






## If running the app on AWS cloud, setup environment to run the visualApp application on an EC2 instance:

1. Create an AWS EC2 instance and attach an IAM role that has Kinesis/DynamoDB full access.
2. SSH into the EC2 instance
3. Clone this repo onto the instance 
4. Run the following commands: 

	a. Install JAVA SDK: look up how to on internet
	
	b. Install Apache Maven compiler: look up how to on internet
	
	c. Add both maven compiler and JAVA SDK to your environment path variable (if they are not in there already)
	
	d. Change into the repo root directory

## If running the app from local, To run the application on edge-cloud architecture: 

1. type the following (replace ??? with your AWS account accessKeyId and secretKey, or create an environment variable as below), each in a separate terminal:
		
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

2. To change data rate:
   a. Open visualApp/src/main/java/org/example/basicApp/ddb/DynamoDBWriter.java, 
   b. Go to Line 116 and change the value from sleep(1000) to sleep(5000) for 5 sec and sleep(10000) for 10 sec.
   c. Open visualApp/src/main/static-content/wwwroot/overview.js, 
   d. go to line 269 and change the 1000 to 5000 or 10000 based on step 2.b.

3. To change name of DynamoDB:
   a. Edit visualApp/pom.xml (rename the DB table to whatever you want to)

4. To change number of users:
   a. Open visualApp/src/main/java/org/example/basicApp/ddb/DynamoDBWriter.java
   b. Go to Line 62 and change numUsers=1 to 5, 10, etc.

### To run the application on cloud only architecture: 

1. type the following (replace ??? with your AWS account accessKeyId and secretKey, or create an environment variable as below), each in a separate terminal:
		
	```bash
	$ MAVEN_OPTS="-Daws.accessKeyId=??? -Daws.secretKey=???" mvn compile -Pstream-writer exec:java
	$ MAVEN_OPTS="-Daws.accessKeyId=??? -Daws.secretKey=???" mvn compile -PclientApp exec:java
	$ MAVEN_OPTS="-Daws.accessKeyId=??? -Daws.secretKey=???" mvn compile -Pwebserver exec:java
	```

	Open web browser: type http://localhost:8080 (if not working, use http://localhost:8080/overview.html)

	or

	```bash
	$ export MAVEN_OPTS="-Daws.accessKeyId=??? -Daws.secretKey=???"
	$ mvn compile -PdbWriter exec:java
	$ mvn compile -Pwebserver exec:java
	``` 

	Open from web browser: http://localhost:8080 (if not working, use http://localhost:8080/overview.html)

2. To change data rate:

   a. Open visualApp /src/main/java/org/example/basicApp/writer/MeasurementWriter.java, 
   b. Go to Line 29 and change the value from 1000 to 5000 for 5 sec and 10000 for 10 sec.
   c. Open visualApp/src/main/static-content/wwwroot/overview.js, 
   d. Go to line 269 and change the 1000 to 5000 or 10000 based on step 2.a.

3. To change name of DynamoDB: Edit visualApp/pom.xml (rename: sample-application.name, sample-application.stream, and sample-application.measurement-table, to whatever you like)

4. To change number of users: Open visualApp/src/main/java/org/example/basicApp/ writer/MeasurementPutter.java

   Go to Line 23 and change numUsers=1 to 5, 10, etc.

