# testKinesisApp


# TO run the program, in the root folder, run the following commands (requires installation of Maven compiler):

MAVEN_OPTS="-Daws.accessKeyId=yourAccessKeyID -Daws.secretKey=yourSecretKey" mvn compile -Pstream-writer exec:java

MAVEN_OPTS="-Daws.accessKeyId=yourAccessKeyID -Daws.secretKey=yourSecretKey" mvn compile -PclientApp exec:java

MAVEN_OPTS="-Daws.accessKeyId=yourAccessKeyID -Daws.secretKey=yourSecretKey" mvn compile -Pwebserver exec:java


# to view live stream data, go to:

http://localhost:8080