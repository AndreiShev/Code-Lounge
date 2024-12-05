package ru.skillbox.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
public class RecommendationFriendsRs {
    private Long totalElements;
    private Integer totalPages;
    private List<RecommendationFriendsDto> content;
}
