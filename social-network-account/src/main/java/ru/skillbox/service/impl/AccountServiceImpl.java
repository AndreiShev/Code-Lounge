package ru.skillbox.service.impl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.InvalidDataAccessApiUsageException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.beans.factory.annotation.Value;
import ru.skillbox.annotation.LogAspect;
import ru.skillbox.aop.LogType;
import ru.skillbox.dto.AccountDto;
import ru.skillbox.dto.AccountSearchDto;
import ru.skillbox.dto.kafka.KafkaAuthEvent;
import ru.skillbox.entity.Account;
import ru.skillbox.exception.AccountNotFoundException;
import ru.skillbox.exception.AlreadyExistsException;
import ru.skillbox.mapper.AccountUpdateFactory;
import ru.skillbox.repository.AccountRepository;
import ru.skillbox.repository.specification.AccountSpecification;
import ru.skillbox.security.SecurityUtils;
import ru.skillbox.service.AccountService;
import ru.skillbox.mapper.AccountMapper;
import ru.skillbox.utils.GlobalConstants;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class AccountServiceImpl implements AccountService {

    private final AccountRepository accountRepository;

    @Value("${app.scheduled.offline-status-minutes}")
    private int offlineStatusMinutes;

    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Value("${app.kafka.kafka-producer-deleted-account-topic}")
    private String topicName;

    @Override
    @LogAspect(type = LogType.SERVICE)
    public AccountDto getAccount() {
        Account account = getAccountById(SecurityUtils.getUUIDFromSecurityContext());
        return AccountMapper.accountToAccountDto(account);
    }

    @Override
    @LogAspect(type = LogType.SERVICE)
    public AccountDto getAccountDtoById(UUID accountId) {
        return AccountMapper.accountToAccountDto(getAccountById(accountId));
    }

    @Override
    public Account getAccountById(UUID accountId) {
        return accountRepository.findById(accountId)
                .orElseThrow(() -> new AccountNotFoundException(
                        String.format(GlobalConstants.ACCOUNT_NOT_FOUND_FORMAT, accountId)));
    }

    @Override
    @Transactional
    @LogAspect(type = LogType.SERVICE)
    public Account createAccount(KafkaAuthEvent kafkaAuthEvent) {
        if (accountRepository.existsByEmail(kafkaAuthEvent.getEmail())) {
            throw new AlreadyExistsException(
                    String.format("An account with email %s already exists!", kafkaAuthEvent.getEmail()));
        }
        return accountRepository.save(AccountMapper.kafkaAuthEventToAccount(kafkaAuthEvent));
    }

    @Override
    @Transactional
    @LogAspect(type = LogType.SERVICE) // test+
    public AccountDto updateAccount(AccountDto accountDto) {
        Account updatedAccount = getAccountById(SecurityUtils.getUUIDFromSecurityContext());
        AccountUpdateFactory.updateFields(updatedAccount, accountDto);
        return AccountMapper.accountToAccountDto(accountRepository.save(updatedAccount));
    }

    @Override
    @Transactional
    @LogAspect(type = LogType.SERVICE)
    public void deleteAccount() {
        UUID accountId = SecurityUtils.getUUIDFromSecurityContext();
        int updated = accountRepository.updateDeleted(accountId, true);
        if (updated == 0) {
            throw new AccountNotFoundException(
                    String.format(GlobalConstants.ACCOUNT_NOT_FOUND_FORMAT, accountId));
        }
        kafkaTemplate.send(topicName, accountId);
    }

    @Override
    @Transactional
    @LogAspect(type = LogType.SERVICE)
    public void manageAccountBlock(UUID accountId, boolean block) {
        int updated = accountRepository.updateBlocked(accountId, block);
        if (updated == 0) {
            throw new AccountNotFoundException(
                    String.format(GlobalConstants.ACCOUNT_NOT_FOUND_FORMAT, accountId));
        }
    }

    @Override
    @LogAspect(type = LogType.SERVICE)
    public List<UUID> getAllAccountIds() {
        return accountRepository.findAllIds();
    }

    @Override
    @LogAspect(type = LogType.SERVICE)
    public PageImpl<AccountDto> getAccountsByTheirId(List<UUID> ids, Pageable page) {
        Page<Account> accountsPage = accountRepository.findAccountsByIds(ids, page);
        List<AccountDto> accountDtoList = AccountMapper.accountListToAccountDtoList(accountsPage.getContent());
        return new PageImpl<>(accountDtoList, page, accountsPage.getTotalElements());
    }

    @Override
    @LogAspect(type = LogType.SERVICE)
    public PageImpl<AccountDto> getAllAccounts(Pageable page) {
        Page<Account> accountsPage = accountRepository.findAll(page);
        List<AccountDto> accountDtoList = AccountMapper.accountListToAccountDtoList(accountsPage.getContent());
        return new PageImpl<>(accountDtoList, page, accountsPage.getTotalElements());
    }

    @Override
    @LogAspect(type = LogType.SERVICE)
    public PageImpl<AccountDto> filterBy(AccountSearchDto accountSearchDto, Pageable page) {
        Page<Account> accountsPage = accountRepository.findAll(AccountSpecification.withFilter(accountSearchDto), page);
        List<AccountDto> accountDtoList = AccountMapper.accountListToAccountDtoList(accountsPage.getContent());
        return new PageImpl<>(accountDtoList, page, accountsPage.getTotalElements());
    }

    @Override
    @LogAspect(type = LogType.SERVICE)
    public PageImpl<AccountDto> searchAccount(AccountSearchDto accountSearchDto, Pageable page) {
        return filterBy(accountSearchDto, page);
    }

    @Transactional
    @Scheduled(cron = "${app.scheduled.interval-in-cron}")
    public void updateOfflineStatus() {
        try {
            accountRepository.updateOfflineStatus(LocalDateTime.now().minusMinutes(offlineStatusMinutes));
        } catch (InvalidDataAccessApiUsageException e) {
            log.error("Error updating offline status: {}", e.getMessage(), e);
        }
    }

    @Override
    @LogAspect(type = LogType.SERVICE)
    public List<AccountDto> searchAccountsByAuthor(String author) {
        List<Account> accounts = accountRepository.findAll(AccountSpecification.byAuthor(author));
        return AccountMapper.accountListToAccountDtoList(accounts);
    }

}
