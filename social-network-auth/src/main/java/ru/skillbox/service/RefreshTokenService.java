package ru.skillbox.service;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import ru.skillbox.entities.RefreshToken;
import ru.skillbox.exception.RefreshTokenException;
import ru.skillbox.repository.RefreshTokenRepositoryAuth;

import java.time.Duration;
import java.time.Instant;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RefreshTokenService {
    @Value("${app.jwt.refreshTokenExpiration}")
    private Duration refreshTokenExpiration;

    private final RefreshTokenRepositoryAuth refreshTokenRepository;

    public RefreshToken findByAccountId(UUID accountId) {
        return refreshTokenRepository.findByAccountId(accountId).orElseThrow(() ->
                new RefreshTokenException("Exception trying to find account by id: " + accountId));
    }

    public RefreshToken createRefreshToken(UUID accountId) {
        RefreshToken refreshToken = RefreshToken.builder()
                .accountId(accountId)
                .expireDate(Instant.now().plusMillis(refreshTokenExpiration.toMillis()))
                .token(UUID.randomUUID().toString())
                .build();

        refreshToken = refreshTokenRepository.save(refreshToken);

        return refreshToken;
    }

    public RefreshToken updateRefreshToken(RefreshToken refreshToken) {
        refreshToken.setExpireDate(Instant.now().plusMillis(refreshTokenExpiration.toMillis()));

        return refreshTokenRepository.save(refreshToken);
    }


    public void deleteRefreshToken(UUID accountId) {
        refreshTokenRepository.deleteByAccountId(accountId);
    }
}
