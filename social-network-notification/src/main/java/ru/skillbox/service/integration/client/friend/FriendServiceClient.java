package ru.skillbox.service.integration.client.friend;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestParam;

@FeignClient(name = "social-network-friends")
public interface FriendServiceClient {

    @GetMapping(value = "/api/v1/friends")
    FriendsRs getFriends(
            @RequestHeader("Authorization") String token,
            @RequestParam("statusCode") String statusCode,
            @RequestParam("size") Integer size
    );

}
