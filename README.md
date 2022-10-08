# Realtime Chat application using Kafka, SpringBoot, ReactJS, and WebSockets

In this tutorial, we would be building a simple real-time chat application that demonstrates how to use Kafka as a message broker along with Java, SpringBoot as Backend, and ReactJS on the front-end.

This project is just for learning purposes. It doesn't contain a production-ready code.

## The Architecture Diagram of the Realtime Chat application
To understand how the building blocks work, let's first take a look of the Architecture Diagram of the Realtime Chat application:


## What is Kafka
Apache Kafka is a widely popular distributed messaging system that provides a fast, distributed, highly scalable, highly available, publish-subscribe messaging system.

In turn, this solves part of a much harder problem:

Communication and integration between components of large software systems.

### What can we do with Kafka?
* Messaging - communicating between apps
* Website Activity Tracking (click, searches...)
* Metrics collection - instead of writing to logs
* Source and target stream processing.

### Installation
Before starting the project, We need to download [Zookeeper](https://zookeeper.apache.org) and [Kafka](https://kafka.apache.org/).

You can download Kafka from [here](https://kafka.apache.org/downloads).

Extract the contents of the compressed file into a folder of your preference.
Inside the Kafka directory, go to the `bin` folder. Here you’ll find many bash scripts that will be useful for running a Kafka application.

If you are using Windows, you also have the same scripts inside the `windows` folder. This tutorial uses Linux commands, but you just need to use the equivalent Windows version if you’re running a Microsoft OS.

##### *Start Zookeeper*

**Zookeeper** is basically to manage the Kafka cluster. It comes bundled with the downloaded Kafka directory. So, we need not download it separately.

To start the zookeeper, go to the *bin* directory and enter the below command.
* Shell script for Linux:
```
./zookeeper-server-start.sh ../config/zookeeper.properties
```
* Shell script for Windows:
```
bin\windows\zookeeper-server-start.bat config\zookeeper.properties
```
##### *Start Kafka Broker*
Update `server.properties`:

Add `listeners=PLAINTEXT://127.0.0.1:9092` in `Socket Server Settings`

Next, To start the Kafka broker, run the below command in the same directory
* Shell script for Linux:
```
./kafka-server-start.sh ../config/server.properties
```
* Shell script for Windows:
```
bin\windows\kafka-server-start.bat config\server.properties
```
Make sure the zookeeper is running before starting Kafka because Kafka receives information such as Offset information kept in the partitions from Zookeeper.

##### *Create a Kafka Topic*

After running Zookeeper and Apache Kafka respectively, We can create a Topic and send and receive data as Producer and Consumer.
* Shell script for Linux:
```
kafka-topics --create --topic kafka-chat --zookeeper localhost:2181 --replication-factor 1 --partitions 1
```
* Shell script for Windows:
```
bin\windows\kafka-topics.bat --create --bootstrap-server localhost:9092 --replication-factor 1 --partitions 1 --topic kafka-chat
```
Here we are creating a topic `kafka-chat` to handle chat messages. We would be using this topic later in the chat application.

##### *Consume the topic (optional: to test)*
* Shell script for Linux:
```
kafka-console-consumer --bootstrap-server localhost:9092 --topic kafka-chat
```
* Shell script for Windows:
```
bin\windows\kafka-console-consumer.bat --bootstrap-server localhost:9092 --topic kafka-chat
```

Now, Let's write some code starting from backend.
