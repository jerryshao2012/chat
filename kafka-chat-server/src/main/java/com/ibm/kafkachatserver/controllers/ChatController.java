package com.ibm.kafkachatserver.controllers;

import com.ibm.kafkachatserver.models.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessageHeaderAccessor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.concurrent.ExecutionException;

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

    // ----------------------------------------------------- WebSocket API
    @MessageMapping("${websocket.send.message}")
    @SendTo("${kafka.messaging.destination}")
    public Message broadcastGroupMessage(final @Payload Message message) {
        // Sending this message to all the subscribers
        if (logger.isDebugEnabled())
            logger.debug("Sending this message to all the subscribers: %s".formatted(message));
        return message;
    }

    @MessageMapping("${websocket.new.user}")
    @SendTo("${kafka.messaging.destination}")
    public Message addUser(final @Payload Message message,
                           final SimpMessageHeaderAccessor simpMessageHeaderAccessor) {
        // Add user in web socket session
        if (logger.isDebugEnabled())
            logger.debug("Add user (%s) in web socket session".formatted(message.getSender()));
        simpMessageHeaderAccessor.getSessionAttributes().put("username", message.getSender());
        return message;
    }
}
