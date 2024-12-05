package ru.skillbox.service;

import feign.FeignException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import ru.skillbox.config.security.TokenFilter;
import ru.skillbox.dto.AccountRs;
import ru.skillbox.dto.FriendsRs;
import ru.skillbox.dto.RecommendationFriendsDto;
import ru.skillbox.entity.Friend;
import ru.skillbox.enums.NotificationType;
import ru.skillbox.enums.StatusCode;
import ru.skillbox.event.NotificationEvent;
import ru.skillbox.exception.AlreadyExistsFriendException;
import ru.skillbox.exception.IllegalFriendRequestException;
import ru.skillbox.exception.NotFoundException;
import ru.skillbox.feign.FriendFeignClient;
import ru.skillbox.mapper.FriendMapper;
import ru.skillbox.repository.FriendRepository;


import java.security.Principal;
import java.text.MessageFormat;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@RequiredArgsConstructor
@Slf4j
@Service
public class FriendService {

    private final FriendRepository repository;
    private final FriendMapper mapper;
    private final FriendFeignClient feignClient;
    private final KafkaService kafkaService;
    private final TokenFilter filter;

    public FriendsRs getFriends(HttpServletRequest request, Principal principal, StatusCode statusCode, Pageable pageable) {
        UUID principalId = UUID.fromString(principal.getName());

        String friendsIds = repository.findAllByAccountIdAndStatusCode(principalId, statusCode)
                .stream()
                .map(Friend::getFriendId)
                .map(UUID::toString)
                .reduce((acc, str) -> acc.concat(",").concat(str))
                .orElse(UUID.randomUUID().toString());

        AccountRs accountRs = new AccountRs(0L, 0, List.of());

        try {
            accountRs = feignClient.getAccountsByIds(
                    friendsIds,
                    pageable.getPageSize(),
                    request.getHeader(HttpHeaders.AUTHORIZATION)
            );
        } catch (FeignException e) {
            log.warn(e.getMessage());
        }

        return new FriendsRs(
                accountRs.getTotalElements(),
                accountRs.getTotalPages(),
                mapper.toFriendDtoList(accountRs.getContent())
                        .stream()
                        .peek(friendDto -> friendDto.setStatusCode(statusCode))
                        .toList()
        );
    }

    public Long count(Principal principal) {
        UUID principalId = UUID.fromString(principal.getName());
        return repository.countByAccountIdAndStatusCode(principalId, StatusCode.FRIEND);
    }

    public void requestFriend(Principal principal, UUID friendId) {
        UUID principalId = UUID.fromString(principal.getName());

        if (principalId.equals(friendId)) {
            throw new IllegalFriendRequestException("Недействительный запрос на добавление в друзья");
        }
        if (repository.existsByAccountIdAndFriendId(principalId, friendId)) {
            throw new AlreadyExistsFriendException(MessageFormat.format("Запись с account_id {0} и friend_id {1} уже существует", principalId, friendId));
        }
        Friend friend = new Friend();
        friend.setAccountId(principalId);
        friend.setFriendId(friendId);
        friend.setStatusCode(StatusCode.REQUEST_TO);
        repository.save(friend);
        friend = new Friend();
        friend.setAccountId(friendId);
        friend.setFriendId(principalId);
        friend.setStatusCode(StatusCode.REQUEST_FROM);
        repository.save(friend);
        kafkaService.sendNotification(
                new NotificationEvent(
                        friendId,
                        principalId,
                        NotificationType.FRIEND_REQUEST,
                        LocalDateTime.now(),
                        NotificationType.FRIEND_REQUEST.name().toLowerCase(),
                        true
                )
        );
    }

    public void approveFriend(Principal principal, UUID friendId) {
        UUID principalId = UUID.fromString(principal.getName());
        Friend friend = repository.findByAccountIdAndFriendId(principalId, friendId).orElseThrow(() -> new NotFoundException(""));
        friend.setStatusCode(StatusCode.FRIEND);
        repository.save(friend);
        friend = repository.findByAccountIdAndFriendId(friendId, principalId).orElseThrow(() -> new NotFoundException(""));
        friend.setStatusCode(StatusCode.FRIEND);
        repository.save(friend);
        kafkaService.sendNotification(
                new NotificationEvent(
                        friendId,
                        principalId,
                        NotificationType.FRIEND_APPROVE,
                        LocalDateTime.now(),
                        NotificationType.FRIEND_APPROVE.name().toLowerCase(),
                        true
                )
        );
    }

    public List<RecommendationFriendsDto> getRecommendations(HttpServletRequest request, Principal principal, Pageable pageable) {
        UUID principalId = UUID.fromString(principal.getName());
        List<UUID> recommendationIds = repository.findAllByAccountIdAndStatusCode(principalId, StatusCode.FRIEND)
                .stream()
                .map(Friend::getFriendId)
                .toList();

        List<UUID> friendsIds = repository.findAllByAccountIdAndStatusCode(principalId, StatusCode.FRIEND)
                .stream()
                .map(Friend::getFriendId)
                .toList();

        Set<UUID> set = new HashSet<>(friendsIds);
        set.add(principalId);

        AccountRs accountRs = new AccountRs(0L, 0, List.of());

        try {
            accountRs = feignClient.getAccountsByIds(
                    repository.findAll(
                            Specification.allOf(
                                    friendsIds.stream()
                                            .map(id -> getSpecification(id, set))
                                            .toList()
                            ),
                            pageable
                    )
                            .stream()
                            .map(Friend::getFriendId)
                            .map(UUID::toString)
                            .reduce((acc, str) -> acc.concat(",").concat(str))
                            .orElse(UUID.randomUUID().toString()),

                    pageable.getPageSize(),
                    request.getHeader(HttpHeaders.AUTHORIZATION)
            );
        } catch (FeignException e) {
            log.warn(e.getMessage());
        }

        return mapper.toRecommendationFriendsDtoList(accountRs.getContent())
                        .stream()
                        .toList();
    }

    private static Specification<Friend> getSpecification(UUID id, Set<UUID> set) {
        return Specification.allOf(
                List.of(
                        (root, query, cb) -> cb.equal(root.get("accountId"), id),
                        (root, query, cb) -> cb.isFalse(root.get("friendId").in(set)),
                        (root, query, cb) -> cb.equal(root.get("statusCode"), StatusCode.FRIEND)
                )
        );
    }

    public List<UUID> getFriendsIds(UUID accountId) {
        return repository.findAllByAccountIdAndStatusCode(accountId, StatusCode.FRIEND)
                .stream()
                .map(Friend::getFriendId)
                .toList();
    }
}
