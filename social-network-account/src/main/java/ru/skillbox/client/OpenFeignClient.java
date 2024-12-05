package ru.skillbox.client;

import org.apache.http.HttpHeaders;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(name = "social-network-auth")
public interface OpenFeignClient {

    @GetMapping("/api/v1/auth/tokenValidation")
    ValidTokenResponse validateToken(@RequestHeader(HttpHeaders.AUTHORIZATION) String token);

}
