package ru.skillbox.mapper;

import ru.skillbox.dto.requests.AccountRequest;
import ru.skillbox.entities.Account;

public abstract class AccountMapperDelegate implements AccountMapper {

    @Override
    public Account requestToAccount(AccountRequest request) {
        if (request == null ) {
            return null;
        }

        Account account = new Account();
        account.setEmail(request.getEmail());
        account.setPassword(request.getPassword1());
        account.setFirstName(request.getFirstName());
        account.setLastName(request.getLastName());

        return account;
    }
}
