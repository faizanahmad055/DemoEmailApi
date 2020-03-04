# Demo Email Api

## Problem
Design and write a Micro Service responsible of Asynchronous sending of Emails with attachments.
It should support retry in case of SMTP server connection failure.
Implement a consumer who listen and react to a topic in front of the service. To solve this problem please use Kafka and Consumer-API.

## Solution

### Email SMTP Setup

We have used `JavaMailSender` and `MimeMessage` to send the emails with attachments.
For SMTP server configuration, we can either use Gmail or any other free SMTP server.
Max retry attempts can be configured using constant: `ResilientValue.MAX_RETRY`

#### Gmail
By default Gmail account is highly secured. When we use gmail smtp from non gmail tool, email is blocked.  To test in our local environment, make your gmail account less secure as

1. Login to Gmail. 
2. Access the URL as https://www.google.com/settings/security/lesssecureapps 
3. Select "Turn on"

#### Mailjet
If using mailjet, then add the sender address and verify it. Also add the sender address (setFrom) in Java mailClient

#### Configuration
Configuration for both Gmail and Mailjet SMTP server is similar. Below is a snippet containing the sample configuration via `application.yaml`

```yaml
spring:
  mail:
    host: <SMTP-server>
    port: 587
    username: <user-name>
    password: <password>
    properties:
      mail:
        smtp:
          auth: true
          connectiontimeout: 5000
          timeout: 5000
          writetimeout: 5000
          starttls:
            enable: true
```

### Kafka
Use [this](https://kafka.apache.org/quickstart) handy quick start guide to setup kafka. Below are few commands to run and create topics.

#### Start ZooKeeper
```bash
zookeeper-server-start /usr/local/etc/kafka/zookeeper.properties
```
#### Start Kafka
```bash
kafka-server-start /usr/local/etc/kafka/server.properties
```
#### Create Topic
```bash
kafka-topics --create --zookeeper localhost:2181 --replication-factor 1 --partitions 1 --topic test
```
#### List Topics
```bash
kafka-topics --list --zookeeper localhost:2181
```
#### Delete Topic
```bash
kafka-topics --delete --zookeeper localhost:2181 --topic <topic-name>
```

### Usage

Before running the micro-service, please make sure to update `application.yaml` with working SMTP host, username and password values.
Configure the kafka topic using property `kafka.mail.topic` in `application.yaml`
#### Run the program
Run below command to start the program. 
```bash
mvn clean package spring-boot:run
```
By default, the `kafka` profile is active, which means you have to setup the kafka and zookeeper and update the `application.yaml` file before running the application.
But if you want to run the application without `kafka`, use below command.

```bash
mvn clean package spring-boot:run -Dspring-boot.run.profiles=default
```

There are three ways to use this microservice.

#### Expose Generic Rest Endpoint

`/v1/group/mail/sent` path has been exposed that take `Email` object as a post request body.
Below curl request can be used to send the email directly via this exposed rest endpoint

```bash
curl --location --request POST 'localhost:8080/v1/group/mail/sent' \
--header 'Content-Type: application/json' \
--data-raw '{
	  "to" : "***@gmail.com", 
	  "from" : "***@gmail.com", 
	  "subject" : "Demo Test Email with Attachment", 
	  "content" : "<h1>Testing from Spring Boot</h1>", 
	  "isHtmlContent" : true, 
	  "attachmentUriList" : [
	    "/tmp/test1.yml",
	    "/tmp/test2.JPG"
	  ]
}'
```

#### Expose Producer Rest Endpoint
`/v1/group/mail/publish` path has been exposed that take `Email` object as a post request body. This endpoint redirects the data to a kafka producer that sends it to topic `mailjet`
Below curl request can be used to send the email directly via this exposed rest endpoint
```bash
curl --location --request POST 'localhost:8080/v1/group/mail/publish' \
--header 'Content-Type: application/json' \
--data-raw '{
	  "to" : "***@gmail.com", 
	  "from" : "***@gmail.com", 
	  "subject" : "Demo Test Email with Attachment", 
	  "content" : "<h1>Testing from Spring Boot</h1>", 
	  "isHtmlContent" : true, 
	  "attachmentUriList" : [
	    "/tmp/test1.yml",
	    "/tmp/test2.JPG"
	  ]
}'
```

#### Use Kafka Producer console
You can also use the kafka producer console to send the data directly to a topic. Configure that topic in `Consumer.java` class and the consumer will listen to it.
```bash
kafka-console-producer --broker-list localhost:9092 --topic <topic-name>
```
Below json can be used to send the request via kafka Producer console.
```json
{"to":"***@gmail.com","from":"***@gmail.com","subject":"Demo Test Email with Attachment","content":"<h1>Testing from Spring Boot</h1>","isHtmlContent":true,"attachmentUriList":["/tmp/test1.yml","/tmp/test2.JPG"]}
```
