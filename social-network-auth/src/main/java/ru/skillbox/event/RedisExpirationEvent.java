package ru.skillbox.event;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.data.redis.core.RedisKeyExpiredEvent;
import org.springframework.stereotype.Component;
import ru.skillbox.entities.RefreshToken;
import ru.skillbox.exception.RefreshTokenException;


@Component
@Slf4j
public class RedisExpirationEvent {

    @EventListener
    public void handleRedisKeyExpired(RedisKeyExpiredEvent<RefreshToken> event) {
        RefreshToken expiredRefreshToken = (RefreshToken) event.getValue();

        if (expiredRefreshToken == null) {
            throw new RefreshTokenException("Refresh token is null in handleRedisKeyExpiredEvent function!");
        }

        log.info("Refresh token with key={} has expired! Refresh token is: {}", expiredRefreshToken.getId()
                , expiredRefreshToken.getToken());

    }
}
