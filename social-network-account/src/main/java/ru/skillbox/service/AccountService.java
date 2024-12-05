package ru.skillbox.service;

import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import ru.skillbox.dto.AccountDto;
import ru.skillbox.dto.AccountSearchDto;
import ru.skillbox.dto.kafka.KafkaAuthEvent;
import ru.skillbox.entity.Account;

import java.util.List;
import java.util.UUID;

public interface AccountService {

    AccountDto getAccount();

    Account getAccountById(UUID accountId);

    AccountDto getAccountDtoById(UUID accountId);

    Account createAccount(KafkaAuthEvent kafkaAuthEvent);

    AccountDto updateAccount(AccountDto accountDto);

    void deleteAccount();

    void manageAccountBlock(UUID accountId, boolean block);

    List<UUID> getAllAccountIds();

    PageImpl<AccountDto> getAccountsByTheirId(List<UUID> ids, Pageable page);

    PageImpl<AccountDto> getAllAccounts(Pageable page);

    PageImpl<AccountDto> filterBy(AccountSearchDto accountSearchDto, Pageable page);

    PageImpl<AccountDto> searchAccount (AccountSearchDto accountSearchDto, Pageable page);
    List<AccountDto> searchAccountsByAuthor(String author);

}
