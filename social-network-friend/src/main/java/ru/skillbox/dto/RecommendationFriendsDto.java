package ru.skillbox.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
public class RecommendationFriendsDto {
    private UUID id;
    private String photo;
    private String firstName;
    private String lastName;
}
