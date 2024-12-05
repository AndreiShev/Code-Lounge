package ru.skillbox.service.integration.client.friend;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class FriendDto {

    private UUID friendId;
    private String firstName;
    private String lastName;
    private LocalDateTime birthDate;
    private String country;
    private String city;
    private String photo;
    private StatusCode statusCode;
    private boolean isOnline;

}
