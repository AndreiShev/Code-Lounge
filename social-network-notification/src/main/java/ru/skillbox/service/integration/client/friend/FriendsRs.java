package ru.skillbox.service.integration.client.friend;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class FriendsRs {

    private Long totalElements;
    private Integer totalPages;
    private List<FriendDto> content;

}
