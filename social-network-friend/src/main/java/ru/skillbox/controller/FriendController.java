package ru.skillbox.controller;

import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.*;
import ru.skillbox.dto.FriendsRs;
import ru.skillbox.dto.RecommendationFriendsDto;
import ru.skillbox.enums.StatusCode;
import ru.skillbox.service.FriendService;


import java.security.Principal;
import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@RestController
@RequestMapping("/api/v1/friends")
public class FriendController {

    private final FriendService service;

    @GetMapping("")
    public FriendsRs getFriends(HttpServletRequest request, Principal principal,
                                @RequestParam(required = false, defaultValue = "0") Integer page,
                                @RequestParam(required = false, defaultValue = "20") Integer size,
                                @RequestParam(required = false) String ids,
                                @RequestParam(required = false) String firstName,
                                @RequestParam(required = false) String lastName,
                                @RequestParam(required = false) Integer ageFrom,
                                @RequestParam(required = false) Integer ageTo,
                                @RequestParam(required = false) String country,
                                @RequestParam(required = false) String city,
                                @RequestParam(required = false, defaultValue = "FRIEND") StatusCode statusCode) {

        return service.getFriends(
                request,
                principal,
                statusCode,
                PageRequest.of(page, size)
        );
    }

    @GetMapping("/count")
    public Long count(Principal principal) {
        return service.count(principal);
    }

    @GetMapping("/recommendations")
    public List<RecommendationFriendsDto> getRecommendations(HttpServletRequest request, Principal principal,
                                                             @RequestParam(required = false) String param) {
        return service.getRecommendations(request, principal, PageRequest.of(0, 20));
    }

    @PostMapping("/{accountId}/request")
    public void requestFriend(Principal principal, @PathVariable UUID accountId) {
        service.requestFriend(principal, accountId);
    }

    @PutMapping("/{accountId}/approve")
    public void approveFriend(Principal principal, @PathVariable UUID accountId) {
        service.approveFriend(principal, accountId);
    }

    @GetMapping("/accountIds")
    public List<UUID> getFriendsIds(@RequestParam UUID accountId) {
        return service.getFriendsIds(accountId);
    }
}
