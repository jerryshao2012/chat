package com.ibm.kafkachatserver.consumers;

import com.ibm.kafkachatserver.models.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Component;

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
