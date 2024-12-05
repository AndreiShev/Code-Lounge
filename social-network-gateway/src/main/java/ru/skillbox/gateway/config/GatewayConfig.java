package ru.skillbox.gateway.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.cloud.gateway.route.RouteLocator;
import org.springframework.cloud.gateway.route.builder.RouteLocatorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.server.reactive.ServerHttpRequest;
import reactor.core.publisher.Mono;

@Configuration
@Slf4j
public class GatewayConfig {

    @Bean
    public RouteLocator routes(RouteLocatorBuilder builder) {
        return builder.routes()
                .route("social-network-account",
                        r -> r.path("/api/v1/account/**")
                                .uri("lb://SOCIAL-NETWORK-ACCOUNT"))
                .route("social-network-auth",
                        r -> r.path("/api/v1/auth/**")
                                .uri("lb://SOCIAL-NETWORK-AUTH"))
                .route("social-network-dialog",
                        r -> r.path("/api/v1/dialogs/**", "/api/v1/streaming/ws")
                                .uri("lb://SOCIAL-NETWORK-DIALOG"))
                .route("social-network-post",
                        r -> r.path("/api/v1/post/**")
                                .uri("lb://SOCIAL-NETWORK-POST"))
                .route("social-network-geo-storage",
                        r -> r.path("/api/v1/geo/**", "/api/v1/storage/**")
                                .uri("lb://SOCIAL-NETWORK-GEO-STORAGE"))
                .route("social-network-friend6",
                        r -> r.path("/api/v1/friends/**")
                                .uri("lb://SOCIAL-NETWORK-FRIENDS"))
                .route("social-network-notification",
                        r -> r.path("/api/v1/notifications/**")
                                .uri("lb://SOCIAL-NETWORK-NOTIFICATION"))
                .build();
    }

    @Bean
    public GlobalFilter loggingFilter() {
        return (exchange, chain) -> {
            ServerHttpRequest request = exchange.getRequest();
            log.info("Request: {} {} {}", request.getMethod(), request.getPath(), request.getURI());
            return chain.filter(exchange).then(Mono.fromRunnable(() ->
                    log.info("Response: {} {} {}", exchange.getResponse().getStatusCode(), request.getPath(), request.getURI())));
        };
    }

}
