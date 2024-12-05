package ru.skillbox.client;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(name = "authClient", url = "http://89.111.174.153:9090/api/v1/auth")
public interface AuthClient {

    @GetMapping("/tokenValidation")
    Boolean validateToken(@RequestHeader("Authorization") String authorization);
}
