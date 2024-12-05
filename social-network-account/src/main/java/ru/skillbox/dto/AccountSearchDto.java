package ru.skillbox.dto;

import lombok.*;

import java.util.List;
import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AccountSearchDto {

    private List<UUID> ids;
    private String author;
    private String firstName;
    private String lastName;
    private String city;
    private String country;
    private boolean isBlocked;
    private boolean isDeleted;
    private Integer ageTo;
    private Integer ageFrom;

}
