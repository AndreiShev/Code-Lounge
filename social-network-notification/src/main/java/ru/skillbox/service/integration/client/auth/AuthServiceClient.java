package ru.skillbox.service.integration.client.auth;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(name = "social-network-auth")
public interface AuthServiceClient {

    @GetMapping("/api/v1/auth/tokenValidation")
    ValidTokenResponse validateToken(@RequestHeader(HttpHeaders.AUTHORIZATION) String token);

    @PostMapping("/api/v1/auth/login")
    TokenResponse login(@RequestBody LoginRequest loginRequest);

}
