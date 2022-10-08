# Realtime Chat application using Kafka, SpringBoot, ReactJS, and WebSockets - Backend Application Development with Java, SpringBoot, and Kafka
We would be developing the backend in Spring Boot.
So, download a fresh Spring Boot Project using [Spring Initializer](https://start.spring.io/) with the following details.

* Project: Maven Project
* Language: Java
* Group: com.ibm
* Artifact: kafka-chat-server
* Dependencies:
    * Spring for Apache Kafka
    * Spring for Websocket
## *Why WebSockets?*
Since Apache Kafka cannot send the Consumer Messages instantly to the client with Classical GET and POST operations.
I performed these operations using WebSockets which provide **full-duplex bidirectional** communication, which means that information can flow from the client to the server and also in the opposite direction simultaneously.
It is widely used in chat applications.

## Developing Producer to push messages to Kafka Topic
First, we would have to write a Config class for the Producer.

*ProducerConfiguration.java*
```java
@EnableKafka
@Configuration
public class ProducerConfiguration {

  // ----------------------------------------------------- Instance Variables

  /**
   * Logger settings
   */
  private static final Logger logger = LoggerFactory.getLogger(ProducerConfiguration.class);

  @Value("${kafka.broker.host}")
  private String kafkaBrokerHost;

  // ----------------------------------------------------- Private Methods

  // ----------------------------------------------------- Protected Methods

  // ----------------------------------------------------- Public Methods

  @Bean
  public ProducerFactory<String, Message> producerFactory() {
    if (logger.isDebugEnabled())
      logger.debug("ProducerFactory constructor started");
    return new DefaultKafkaProducerFactory<>(producerConfigurations());
  }

  @Bean
  public Map<String, Object> producerConfigurations() {
    final Map<String, Object> configurations = new HashMap<>();
    configurations.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaBrokerHost);
    configurations.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
    configurations.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
    if (logger.isDebugEnabled())
      logger.debug("Producer Configurations: %s".formatted(configurations.keySet().stream()
              .map(key -> "%s=%s".formatted(key, configurations.get(key)))
              .collect(Collectors.joining(", ", "{", "}"))));
    return configurations;
  }

  @Bean
  public KafkaTemplate<String, Message> kafkaTemplate() {
    if (logger.isDebugEnabled())
      logger.debug("KafkaTemplate constructor started");
    return new KafkaTemplate<>(producerFactory());
  }
}
```
This class creates a `ProducerFactory` which knows how to create producers based on the configurations we provided.

We also declared a `KafkaTemplate` bean to perform high-level operations on your producer. In other words, the template can do operations such as sending a message to a topic and efficiently hides under-the-hood details from you.

In `producerConfigurations` method, we need to perform the following tasks:

* `BOOTSTRAP_SERVERS_CONFIG` to set the server address on which Kafka is running.
* `KEY_SERIALIZER_CLASS_CONFIG` and `VALUE_SERIALIZER_CLASS_CONFIG` to deserialize the key and value from the Kafka Queue.

The next step is to create an endpoint to send the messages to the Kafka topic.
Create the following controller class for that.

#### *ChatController.java*
```java
@RestController
public class ChatController {

  // ----------------------------------------------------- Instance Variables

  /**
   * Logger settings
   */
  private static final Logger logger = LoggerFactory.getLogger(ChatController.class);

  @Autowired
  private KafkaTemplate<String, Message> kafkaTemplate;

  @Value("${kafka.topic}")
  private String kafkaTopic;

  // ----------------------------------------------------- Private Methods

  // ----------------------------------------------------- Protected Methods

  // ----------------------------------------------------- Public Methods

  @PostMapping(value = "${send.message.api.url}", consumes = "application/json", produces = "application/json")
  public void sendMessage(final @RequestBody Message message) {
    message.setTimestamp(LocalDateTime.now().toString());
    try {
      // Sending the message to kafka topic queue
      if (logger.isDebugEnabled())
        logger.debug("Sending the message to kafka topic queue: %s".formatted(message));
      kafkaTemplate.send(kafkaTopic, message).get();
    } catch (final InterruptedException | ExecutionException e) {
      logger.error("Failed to send out message: %s".formatted(message), e);
      throw new RuntimeException(e);
    }
  }

  ...
}
```
As you can see the endpoint is quite simple. When we do `POST` request to `/api/v1/send` it Injects the KafkaTemplate configured earlier and sends a message to the `kafka-chat` topic which we created earlier.

Let's test everything we build until now. Run the `main` method inside `KafakaJavaApp.java` class. To run from the command line, execute the following command
* Shell script for Linux:
```
mvn spring-boot:run
``` 
* Shell script for Windows:
```
 .\mvnw.cmd spring-boot:run
```
Your server should be running on port 8081, and you can make API requests against it. You can use a curl command to access it (Shell script for Windows):
```
curl -L -X POST http://localhost:3000/api/v1/send -H "Content-Type: application/json" -d "{\"sender\":\"Jerry\",\"content\":\"This is a test from curl\"}"
```
You can use postman to do a POST request as shown below.

But how do you know the command successfully sent a message to the topic? Right now, you don’t consume messages inside your app, which means you cannot be sure!

Fortunately, there is an easy way to create a consumer to test right away. Inside the bin folder of your Kafka directory, run the following command:
* Shell script for Linux:
```
./kafka-console-consumer.sh --bootstrap-server localhost:9092 --topic kafka-chat
```
* Shell script for Windows:
```
bin\windows\kafka-console-consumer.bat --bootstrap-server localhost:9092 --topic kafka-chat
```

Hit `http://localhost:8081/api/v1/send` again to see the message in the terminal running the Kafka consumer

Now let's achieve the same functionality using the Java Code. For that, we would need to build a Consumer or Listener in Java.

## Develop a Consumer to listen to Kafka Topic
Similar to `ProducerConfig.java` we need to have a Consumer Config to enable the consumer to find the broker.

*ListenerConfig.java*
```java
@EnableKafka
@Configuration
public class ListenerConfig {

  // ----------------------------------------------------- Instance Variables

  /**
   * Logger settings
   */
  private static final Logger logger = LoggerFactory.getLogger(ListenerConfig.class);

  @Value("${kafka.group.id}")
  private String kafkaGroupId;

  @Value("${kafka.broker.host}")
  private String kafkaBrokerHost;

  // ----------------------------------------------------- Private Methods

  // ----------------------------------------------------- Protected Methods

  // ----------------------------------------------------- Public Methods

  @Bean
  public ConcurrentKafkaListenerContainerFactory<String, Message> kafkaListenerContainerFactory() {
    final ConcurrentKafkaListenerContainerFactory<String, Message>
            concurrentKafkaListenerContainerFactory = new ConcurrentKafkaListenerContainerFactory<>();
    concurrentKafkaListenerContainerFactory.setConsumerFactory(consumerFactory());
    if (logger.isDebugEnabled())
      logger.debug("ConcurrentKafkaListenerContainerFactory constructor started");
    return concurrentKafkaListenerContainerFactory;
  }

  @Bean
  public ConsumerFactory<String, Message> consumerFactory() {
    if (logger.isDebugEnabled())
      logger.debug("ConsumerFactory constructor started");
    return new DefaultKafkaConsumerFactory<>(consumerConfigurations(),
            new StringDeserializer(), new JsonDeserializer<>(Message.class));
  }

  @Bean
  public Map<String, Object> consumerConfigurations() {
    final Map<String, Object> configurations = new HashMap<>();
    configurations.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaBrokerHost);
    configurations.put(ConsumerConfig.GROUP_ID_CONFIG, kafkaGroupId);
    configurations.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
    configurations.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);
    configurations.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "latest");
    if (logger.isDebugEnabled())
      logger.debug("Consumer Configurations: %s".formatted(configurations.keySet().stream()
              .map(key -> "%s=%s".formatted(key, configurations.get(key)))
              .collect(Collectors.joining(", ", "{", "}"))));
    return configurations;
  }
}
```
In Consumer Config, similar to Producer Config we are setting the deserializer for key and value.
Additionally, we need to set:
* `GROUP_ID_CONFIG` to set the Kafka consumer group ID
* `AUTO_OFFSET_RESET_CONFIG` to set the Offset Configuration.
  In this project, we are using the value "earliest" so that we will get all the values in the queue from the beginning.
  Instead, we can also use "latest" to get only the latest value.

*MessageListener.java*

```java
@Component
public class MessageListener {

  // ----------------------------------------------------- Instance Variables

  /**
   * Logger settings
   */
  private static final Logger logger = LoggerFactory.getLogger(MessageListener.class);

  @Autowired
  private SimpMessagingTemplate simpMessagingTemplate;

  @Value("${kafka.messaging.destination}")
  private String kafkaMessagingDestination;

  // ----------------------------------------------------- Private Methods

  // ----------------------------------------------------- Protected Methods

  // ----------------------------------------------------- Public Methods

  @KafkaListener(
          topics = "${kafka.topic}",
          groupId = "${kafka.group.id}"
  )
  public void listen(final Message message) {
    if (logger.isDebugEnabled())
      logger.debug("Sending via kafka listener: %s".formatted(message));
    simpMessagingTemplate.convertAndSend(kafkaMessagingDestination, message);
  }
}

```

In this class, the @KafkaListener annotated the method that will listen for the Kafka queue messages,
and template.convertAndSend will convert the message and send that to WebSocket topic.

Next, we need to configure the Websocket to send the Message to client system.

### Spring WebSocket Configuration
*WebSocketConfig.java*
```java
@Configuration
@EnableWebSocketMessageBroker
public class WebSocketConfig implements WebSocketMessageBrokerConfigurer {

  // ----------------------------------------------------- Instance Variables

  /**
   * Logger settings
   */
  private static final Logger logger = LoggerFactory.getLogger(WebSocketConfig.class);

  @Value("${websocket.stomp.endpoint}")
  private String websocketStompEndpoint;

  @Value("${websocket.message.broker.application.destination.prefixes}")
  private String websocketMessageBrokerApplicationDestinationPrefixes;

  @Value("${websocket.message.broker.enable.simple.broker.destination.prefixes}")
  private String websocketMessageBrokerEnableSimpleBrokerDestinationPrefixes;

  // ----------------------------------------------------- Private Methods

  // ----------------------------------------------------- Protected Methods

  // ----------------------------------------------------- Public Methods

  @Override
  public void registerStompEndpoints(final StompEndpointRegistry stompEndpointRegistry) {
    // Chat client will use this to connect to the server
    if (logger.isDebugEnabled())
      logger.debug("Chat client will use this to connect to the server");
    stompEndpointRegistry.addEndpoint(websocketStompEndpoint)
            .setAllowedOriginPatterns("*")
            .withSockJS();
  }

  @Override
  public void configureMessageBroker(final MessageBrokerRegistry messageBrokerRegistry) {
    if (logger.isDebugEnabled())
      logger.debug("Configure message broker");
    messageBrokerRegistry.setApplicationDestinationPrefixes(websocketMessageBrokerApplicationDestinationPrefixes);
    messageBrokerRegistry.enableSimpleBroker(websocketMessageBrokerEnableSimpleBrokerDestinationPrefixes);
  }
}
```

Next add the below `MessageMapping` in the `ChatController.java`
```
  @MessageMapping("${websocket.send.message}")
  @SendTo("${kafka.messaging.destination}")
  public Message broadcastGroupMessage(final @Payload Message message) {
    // Sending this message to all the subscribers
    if (logger.isDebugEnabled())
      logger.debug("Sending this message to all the subscribers: %s".formatted(message));
    return message;
  }
``` 
This would broadcast the Message all the client who have subscribed to this topic.

Next, Lets move on to developing the UI part using ReactJS.

### SourceCode

You can find the complete source code in my [Github](https://github.com/jerryshao2012/kafka-chat-app) page.

### Reference Documentation
For further reference, please consider the following sections:

* [Official Apache Maven documentation](https://maven.apache.org/guides/index.html)
* [Spring Boot Maven Plugin Reference Guide](https://docs.spring.io/spring-boot/docs/2.7.4/maven-plugin/reference/html/)
* [Create an OCI image](https://docs.spring.io/spring-boot/docs/2.7.4/maven-plugin/reference/html/#build-image)
* [Spring for Apache Kafka](https://docs.spring.io/spring-boot/docs/2.7.4/reference/htmlsingle/#messaging.kafka)
* [WebSocket](https://docs.spring.io/spring-boot/docs/2.7.4/reference/htmlsingle/#messaging.websockets)

### Guides
The following guides illustrate how to use some features concretely:

* [Using WebSocket to build an interactive web application](https://spring.io/guides/gs/messaging-stomp-websocket/)
