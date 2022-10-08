package com.ibm.kafkachatserver.configurations;

import com.ibm.kafkachatserver.models.Message;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JsonSerializer;

import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

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
