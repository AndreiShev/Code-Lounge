package ru.skillbox.entities;

import jakarta.persistence.Id;
import lombok.*;
import org.springframework.data.redis.core.RedisHash;
import org.springframework.data.redis.core.index.Indexed;

import java.time.Instant;
import java.util.UUID;

@RedisHash("refresh_tokens")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
public class RefreshToken {

    @Id
    @Indexed
    private Long id;

    @Indexed
    private UUID accountId;

    @Indexed
    private String token;

    @Indexed
    private Instant expireDate;
}
