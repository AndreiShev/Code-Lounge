package ru.skillbox.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.skillbox.dto.requests.AccountRequest;
import ru.skillbox.dto.requests.LoginRequest;
import ru.skillbox.dto.requests.RecoveryRequest;
import ru.skillbox.dto.requests.RefreshTokenRequest;
import ru.skillbox.dto.responses.AccountResponse;
import ru.skillbox.dto.responses.CaptchaResponse;
import ru.skillbox.dto.responses.RefreshTokenResponse;
import ru.skillbox.security.SecurityService;
import ru.skillbox.service.CaptchaService;

import javax.security.auth.login.AccountNotFoundException;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthenticationController {

    private final SecurityService securityService;
    private final CaptchaService captchaService;

    @GetMapping("/tokenValidation")
    public boolean validateToken(@RequestHeader("Authorization") String token) {
        return securityService.validateToken(token);
    }
    
    @PostMapping("/register")
    public ResponseEntity<AccountResponse> register(@RequestBody AccountRequest accountRequest) {
        return ResponseEntity.status(HttpStatus.OK).body(securityService.register(accountRequest));
    }

    @PostMapping("/login")
    public ResponseEntity<RefreshTokenResponse> login(@RequestBody LoginRequest loginRequest) {
        return ResponseEntity.ok(securityService.login(loginRequest));
    }

    @PostMapping("/refresh-token")
    public ResponseEntity<RefreshTokenResponse> refreshToken(@RequestBody RefreshTokenRequest refreshTokenRequest) {
        return ResponseEntity.status(HttpStatus.OK).body(securityService.refreshToken(refreshTokenRequest));
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout() {
        securityService.logout();
        return ResponseEntity.ok().build();
    }

    /**Метод для отправки емэйла пользователю для восстановления пароля через емэйл*/
    @PostMapping("/recovery")
    public ResponseEntity<Void> recovery(@RequestBody RecoveryRequest request) throws AccountNotFoundException {
        securityService.recoveryPassword(request);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/captcha")
    public ResponseEntity<CaptchaResponse> captcha() {
        return ResponseEntity.ok(captchaService.generateCaptcha());
    }
}
