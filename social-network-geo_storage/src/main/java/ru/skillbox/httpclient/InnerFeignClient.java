package ru.skillbox.httpclient;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;

@FeignClient(name = "innerFeignClient", url = "${app.gateway-point}")
public interface InnerFeignClient {

  @GetMapping("/auth/tokenValidation")
  boolean validateToken(@RequestHeader("Authorization") String token);
}
