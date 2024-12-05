package ru.skillbox.mapper;

import lombok.experimental.UtilityClass;
import ru.skillbox.dto.AccountDto;
import ru.skillbox.dto.kafka.KafkaAuthEvent;
import ru.skillbox.dto.kafka.KafkaNewAccountEvent;
import ru.skillbox.entity.Account;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@UtilityClass
public class AccountMapper {

    public Account kafkaAuthEventToAccount(KafkaAuthEvent kafkaAuthEvent) {
        return Account.builder()
                .id(kafkaAuthEvent.getUuid())
                .email(kafkaAuthEvent.getEmail())
                .firstName(kafkaAuthEvent.getFirstName().trim().toUpperCase())
                .lastName(kafkaAuthEvent.getLastName().trim().toUpperCase())
                .isBlocked(false)
                .isDeleted(false)
                .isOnline(true)
                .lastOnlineTime(LocalDateTime.now())
                .build();
    }

    public AccountDto accountToAccountDto(Account account) {
        return AccountDto.builder()
                .id(account.getId())
                .email(account.getEmail())
                .city(account.getCity())
                .country(account.getCountry())
                .firstName(account.getFirstName())
                .lastName(account.getLastName())
                .birthDate(account.getBirthDate())
                .isBlocked(account.getIsBlocked())
                .isDeleted(account.getIsDeleted())
                .isOnline(account.getIsOnline())
                .createdOn(account.getCreatedOn())
                .updatedOn(account.getUpdatedOn())
                .phone(account.getPhone())
                .about(account.getAbout())
                .profileCover(account.getProfileCover())
                .emojiStatus(account.getEmojiStatus())
                .photo(account.getPhoto())
                .lastOnlineTime(account.getLastOnlineTime())
                .build();
    }

    public KafkaNewAccountEvent accountToKafkaNewAccountEvent(Account account) {
        return KafkaNewAccountEvent.builder()
                .id(account.getId())
                .email(account.getEmail())
                .firstName(account.getFirstName())
                .lastName(account.getLastName())
                .build();
    }

    public List<AccountDto> accountListToAccountDtoList(List<Account> accounts) {
        return accounts.stream()
                .map(AccountMapper::accountToAccountDto)
                .toList();
    }

}
