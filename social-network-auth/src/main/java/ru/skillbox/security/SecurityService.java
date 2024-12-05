package ru.skillbox.security;


import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import ru.skillbox.dto.requests.AccountRequest;
import ru.skillbox.dto.requests.LoginRequest;
import ru.skillbox.dto.requests.RecoveryRequest;
import ru.skillbox.dto.requests.RefreshTokenRequest;
import ru.skillbox.dto.responses.AccountResponse;
import ru.skillbox.dto.responses.RefreshTokenResponse;
import ru.skillbox.entities.Account;
import ru.skillbox.entities.RefreshToken;
import ru.skillbox.entities.RoleType;
import ru.skillbox.exception.AlreadyExistsException;
import ru.skillbox.exception.AuthException;
import ru.skillbox.exception.EntityNotFoundException;
import ru.skillbox.exception.RefreshTokenException;
import ru.skillbox.mapper.AccountMapper;
import ru.skillbox.repository.AccountRepository;
import ru.skillbox.security.jwt.JwtUtils;
import ru.skillbox.service.AccountService;
import ru.skillbox.service.KafkaService;
import ru.skillbox.service.RefreshTokenService;

import javax.security.auth.login.AccountNotFoundException;
import java.text.MessageFormat;
import java.util.Collections;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Validated
public class SecurityService {
    private final AuthenticationManager authenticationManager;

    private final JwtUtils jwtUtils;

    private final RefreshTokenService refreshTokenService;

    private final AccountRepository accountRepository;

    private final PasswordEncoder passwordEncoder;

    private final AccountMapper accountMapper;

    private final KafkaService kafkaService;

    private final AccountService accountService;

    public boolean validateToken(String token) throws RefreshTokenException {
        new Thread(() -> kafkaService.sendActionEvent(JwtUtils.getIdFromToken(prepareToken(token)))).start();
        return jwtUtils.validateToken(prepareToken(token));
    }

    public void recoveryPassword(@Valid RecoveryRequest recoveryRequest) throws AccountNotFoundException {
        Account account = accountRepository.findAccountByEmail(recoveryRequest.getEmail())
                .orElseThrow( () -> new EntityNotFoundException(MessageFormat.format("Account with email {0} not found", recoveryRequest.getEmail())));

        account.setPassword(passwordEncoder.encode(recoveryRequest.getTemp()));
        accountRepository.save(account);
    }


    public RefreshTokenResponse login(@Valid LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                loginRequest.getEmail(),
                loginRequest.getPassword()
        ));

        SecurityContextHolder.getContext().setAuthentication(authentication);
        AppUserDetails userDetails = (AppUserDetails) authentication.getPrincipal();
        RefreshToken refreshToken = refreshTokenService.createRefreshToken(userDetails.getId());
        return new RefreshTokenResponse(jwtUtils.generateToken(userDetails), refreshToken.getToken());
    }



    public AccountResponse register(@Valid AccountRequest request) {
        Account newAccount = accountMapper.requestToAccount(request);
        if (!request.getPassword1().equals(request.getPassword2())) {
            throw new AuthException("Passwords do not match");
        }

        if (accountRepository.existsAccountByEmail(request.getEmail())) {
            throw new AlreadyExistsException("Email already exists");
        }


        var account = Account.builder()
                .firstName(newAccount.getFirstName())
                .lastName(newAccount.getLastName())
                .email(newAccount.getEmail())
                .password(passwordEncoder.encode(newAccount.getPassword()))
                .build();

        account.setRoles(Collections.singleton(RoleType.ROLE_USER));
        Account savedAccount = accountRepository.save(account);
        kafkaService.sendAuthEvent(savedAccount);
        return accountMapper.accountToResponse(savedAccount);
    }


    public RefreshTokenResponse refreshToken(@Valid RefreshTokenRequest request) throws RefreshTokenException {
        RefreshToken refreshToken = refreshTokenService.findByAccountId(JwtUtils
                .getIdFromToken(prepareToken(request.getToken())));
        jwtUtils.generateTokenFromAccount(accountService.getAccountById(refreshToken.getAccountId()));

        return new RefreshTokenResponse(jwtUtils.generateTokenFromAccount(accountService.getAccountById(refreshToken.getAccountId()))
                , refreshTokenService.updateRefreshToken(refreshToken).getToken());
    }


    public void logout() {
        var currentAccount = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (currentAccount instanceof AppUserDetails userDetails) {
            UUID accountId = userDetails.getId();
            refreshTokenService.deleteRefreshToken(accountId);
        }
    }

    private String prepareToken(String token) {
        return token.replace("Bearer ", "");
    }
}
