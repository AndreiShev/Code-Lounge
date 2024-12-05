package ru.skillbox.mapper;

import org.mapstruct.DecoratedWith;
import org.mapstruct.Mapper;
import org.mapstruct.ReportingPolicy;
import org.springframework.stereotype.Component;
import ru.skillbox.dto.requests.AccountRequest;
import ru.skillbox.dto.responses.AccountResponse;
import ru.skillbox.entities.Account;

@Component
@DecoratedWith(AccountMapperDelegate.class)
@Mapper(componentModel = "spring", unmappedTargetPolicy =  ReportingPolicy.IGNORE)
public interface AccountMapper {

    Account requestToAccount(AccountRequest request);

    AccountResponse accountToResponse(Account account);

}
