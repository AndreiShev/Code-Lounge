package ru.skillbox.dto;

import lombok.Getter;
import lombok.Setter;
import ru.skillbox.enums.RoleType;


import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
public class AccountDto {
    private UUID id;
    private String firstName;
    private String lastName;
    private LocalDateTime birthDate;
    private String email;
    private String phone;
    private String photo;
    private String profileCover;
    private String about;
    private String country;
    private String city;
    private RoleType role;
    private Integer emojiStatus;
    private LocalDateTime regDate;
    private LocalDateTime updatedOn;
    private LocalDateTime lastOnlineTime;
    private boolean isOnline;
    private boolean isBlocked;
    private boolean isDeleted;
}
