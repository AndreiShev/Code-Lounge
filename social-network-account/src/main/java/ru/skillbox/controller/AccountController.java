package ru.skillbox.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.*;
import org.springframework.data.web.PageableDefault;
import org.springframework.web.bind.annotation.*;
import ru.skillbox.annotation.LogAspect;
import ru.skillbox.aop.LogType;
import ru.skillbox.dto.AccountDto;
import ru.skillbox.dto.AccountSearchDto;
import ru.skillbox.service.AccountService;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/account")
@RequiredArgsConstructor
@Slf4j
public class AccountController {

    private static final String SUCCESSFULLY = "Successfully";

    private final AccountService accountService;

    @GetMapping("/me")
    @LogAspect(type = LogType.CONTROLLER)
    public AccountDto getAccount() {
        return accountService.getAccount();
    }

    @PutMapping("/me")
    @LogAspect(type = LogType.CONTROLLER)
    public AccountDto updateAccount(@RequestBody AccountDto accountDto) {
        return accountService.updateAccount(accountDto);
    }

    @DeleteMapping("/me")
    @LogAspect(type = LogType.CONTROLLER)
    public String deleteAccount() {
        accountService.deleteAccount();
        return SUCCESSFULLY;
    }

    @PutMapping("/block/{id}")
    @LogAspect(type = LogType.CONTROLLER)
    public String blockAccount(@PathVariable("id") UUID accountId) {
        accountService.manageAccountBlock(accountId, true);
        return SUCCESSFULLY;
    }

    @DeleteMapping("/block/{id}")
    @LogAspect(type = LogType.CONTROLLER)
    public String unblockAccount(@PathVariable("id") UUID accountId) {
        accountService.manageAccountBlock(accountId, false);
        return SUCCESSFULLY;
    }

    @GetMapping
    @LogAspect(type = LogType.CONTROLLER)
    public PageImpl<AccountDto> getAllAccounts(@PageableDefault(sort = "firstName", direction = Sort.Direction.ASC) Pageable page) {
        return accountService.getAllAccounts(page);
    }

    @GetMapping("/{id}")
    @LogAspect(type = LogType.CONTROLLER)
    public AccountDto getAccountById(@PathVariable("id") UUID accountId) {
        return accountService.getAccountDtoById(accountId);
    }

    @GetMapping("/search")
    @LogAspect(type = LogType.CONTROLLER)
    public PageImpl<AccountDto> searchAccount(@ModelAttribute AccountSearchDto dto,
                                              @PageableDefault(sort = "firstName", direction = Sort.Direction.ASC, size = 100) Pageable page) {
        return accountService.searchAccount(dto, page);
    }

    @GetMapping("/ids")
    @LogAspect(type = LogType.CONTROLLER)
    public List<UUID> getAllAccountIds() {
        return accountService.getAllAccountIds();
    }

    @GetMapping("/accountIds")
    @LogAspect(type = LogType.CONTROLLER)
    public PageImpl<AccountDto> getAccountsByTheirIds(@RequestParam List<UUID> ids,
                                                  @PageableDefault(sort = "firstName", direction = Sort.Direction.ASC) Pageable page) {
        return accountService.getAccountsByTheirId(ids, page);
    }
    @GetMapping("/searchs")
    @LogAspect(type = LogType.CONTROLLER)
    public List<AccountDto> searchAccountsByAuthor(@RequestParam String author) {
        return accountService.searchAccountsByAuthor(author);
    }
}