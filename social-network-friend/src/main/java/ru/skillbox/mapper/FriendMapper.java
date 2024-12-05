package ru.skillbox.mapper;


import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.skillbox.dto.AccountDto;
import ru.skillbox.dto.FriendDto;
import ru.skillbox.dto.RecommendationFriendsDto;

import java.util.List;

@Mapper(componentModel = "spring")
public interface FriendMapper {
    @Mapping(target = "friendId", source = "id")
    @Mapping(target = "statusCode", ignore = true)
    FriendDto toFriendDto(AccountDto accountDto);
    RecommendationFriendsDto toRecommendationFriendsDto(AccountDto accountDto);
    List<FriendDto> toFriendDtoList(List<AccountDto> accountDtoList);
    List<RecommendationFriendsDto> toRecommendationFriendsDtoList(List<AccountDto> accountDtoList);
}
