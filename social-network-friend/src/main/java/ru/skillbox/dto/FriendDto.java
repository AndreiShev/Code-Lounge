package ru.skillbox.dto;

import lombok.Getter;
import lombok.Setter;
import ru.skillbox.enums.StatusCode;


import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
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
