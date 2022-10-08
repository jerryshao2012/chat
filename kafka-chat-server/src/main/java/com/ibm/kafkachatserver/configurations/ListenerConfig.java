package com.ibm.kafkachatserver.configurations;

import com.ibm.kafkachatserver.models.Message;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.support.serializer.JsonDeserializer;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

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
