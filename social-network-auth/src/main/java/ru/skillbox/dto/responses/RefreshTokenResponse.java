package ru.skillbox.dto.responses;


import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
public class RefreshTokenResponse {

    private String accessToken;

    private String refreshToken;
}
