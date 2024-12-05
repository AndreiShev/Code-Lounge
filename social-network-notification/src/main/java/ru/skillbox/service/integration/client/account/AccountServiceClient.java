package ru.skillbox.service.integration.client.account;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.UUID;


@FeignClient(name = "social-network-account")
public interface AccountServiceClient {

    @GetMapping("/api/v1/account/{id}")
    AccountDto getAccountById(@RequestHeader("Authorization") String token, @PathVariable("id") UUID accountId);

    @GetMapping(value = "/api/v1/accountIds")
    List<AccountDto> getAccountsByIds(@RequestHeader("Authorization") String token, @RequestParam List<UUID> ids);

}
