package ru.skillbox.feign;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.HttpHeaders;
import org.springframework.web.bind.annotation.*;
import ru.skillbox.dto.AccountRs;
import ru.skillbox.dto.JwtRq;

import java.util.Map;

@FeignClient(name = "friend", url = "${app.feignClient.url}")
public interface FriendFeignClient {
    @PostMapping("/auth/getclaims")
    Map<String, String> validateToken(@RequestBody JwtRq token);
    @GetMapping("/account/search")
    AccountRs getAccountsByIds(
            @RequestParam String ids,
            @RequestParam Integer size,
            @RequestHeader(HttpHeaders.AUTHORIZATION) String token
    );
}
