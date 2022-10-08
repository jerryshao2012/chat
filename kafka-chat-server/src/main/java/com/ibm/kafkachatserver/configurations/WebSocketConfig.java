package com.ibm.kafkachatserver.configurations;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.web.socket.config.annotation.EnableWebSocketMessageBroker;
import org.springframework.web.socket.config.annotation.StompEndpointRegistry;
import org.springframework.web.socket.config.annotation.WebSocketMessageBrokerConfigurer;

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
