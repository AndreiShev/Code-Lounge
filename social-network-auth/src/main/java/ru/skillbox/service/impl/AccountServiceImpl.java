package ru.skillbox.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.skillbox.entities.Account;
import ru.skillbox.exception.EntityNotFoundException;
import ru.skillbox.repository.AccountRepository;
import ru.skillbox.service.AccountService;

import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AccountServiceImpl implements AccountService {
    private final AccountRepository accountRepository;

    @Override
    public Account getAccountById(UUID id) {
        return accountRepository.findById(id).orElseThrow(
                () -> new EntityNotFoundException("Account with id " + id + " not found")
        );
    }
}
