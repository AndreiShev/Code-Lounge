package ru.skillbox.service;

import ru.skillbox.dto.responses.CaptchaResponse;

public interface CaptchaService {

    CaptchaResponse generateCaptcha();
}
