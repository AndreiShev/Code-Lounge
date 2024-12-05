package ru.skillbox.configuration;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.logging.LoggingRebinder;
import org.springframework.context.annotation.Configuration;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.simp.config.ChannelRegistration;
import org.springframework.messaging.simp.config.MessageBrokerRegistry;
import org.springframework.messaging.support.ChannelInterceptor;
import org.springframework.web.socket.config.annotation.*;


@Configuration
@EnableWebSocketMessageBroker
@RequiredArgsConstructor
@Slf4j
public class WebSocketConfig  implements WebSocketMessageBrokerConfigurer {

    private final LoggingRebinder loggingRebinder;
    @Value("${app.rabbitMQ.host}")
    private String rabbitMqHost;

    @Value("${app.rabbitMQ.port}")
    private Integer rabbitMqPort;

    @Value("${app.rabbitMQ.login}")
    private String rabbitMqLogin;

    @Value("${app.rabbitMQ.password}")
    private String rabbitMqPassword;

    @Override
    public void configureMessageBroker(MessageBrokerRegistry config) {
        log.info("[WEBSOCKET] Configuring Message Broker...");
        config.enableStompBrokerRelay("/topic")
                .setRelayHost(rabbitMqHost)
                .setRelayPort(rabbitMqPort)
                .setClientLogin(rabbitMqLogin)
                .setClientPasscode(rabbitMqPassword)
                .setSystemLogin(rabbitMqLogin)
                .setSystemPasscode(rabbitMqPassword);
        config.setApplicationDestinationPrefixes("/app");
        log.info("[WEBSOCKET] Message Broker configured with prefixes: /topic, /queue");
    }

    @Override
    public void registerStompEndpoints(StompEndpointRegistry registry) {
        registry.addEndpoint("/api/v1/streaming/ws")
                .setAllowedOriginPatterns("*");
    }


    @Override
    public void configureClientInboundChannel(ChannelRegistration registration) {
        registration.interceptors(new ChannelInterceptor() {
            @Override
            public Message<?> preSend(Message<?> message, MessageChannel channel) {
                log.info("[WEBSOCKET] Received message from client: " + message);
                return message;
            }
        });
    }
}


