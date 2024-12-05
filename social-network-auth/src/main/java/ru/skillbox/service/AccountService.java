package ru.skillbox.service;

import ru.skillbox.entities.Account;

import java.util.UUID;

public interface AccountService {

    Account getAccountById(UUID id);

}
