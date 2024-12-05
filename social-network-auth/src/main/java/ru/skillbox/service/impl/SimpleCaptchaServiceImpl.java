package ru.skillbox.service.impl;

import feign.FeignException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import nl.captcha.Captcha;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import ru.skillbox.dto.responses.CaptchaResponse;
import ru.skillbox.exception.CaptchaException;
import ru.skillbox.httpclient.RecordingResources;
import ru.skillbox.service.CaptchaService;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.Instant;

@Service
@RequiredArgsConstructor
@Slf4j
public class SimpleCaptchaServiceImpl implements CaptchaService {

    private final RecordingResources recordingResources;

    @Override
    public CaptchaResponse generateCaptcha() throws FeignException {
        Captcha captcha = new Captcha.Builder(200, 50)
                .addText()
                .addBackground()
                .addNoise()
                .build();
        MultipartFile file = null;
        try {
            file = new MockMultipartFile("captcha_" + Instant.now().getNano()
                    , "captcha_" + Instant.now().getNano()
                    , MediaType.IMAGE_PNG_VALUE
                    , imageToBytesArray(captcha.getImage()));
        } catch (IOException e) {
            throw new CaptchaException("Ошибка создания капчи. Message: " + e.getMessage());
        }

        return new CaptchaResponse(captcha.getAnswer(), recordingResources.uploadCaptcha(file).getFileName());
    }

    private byte[] imageToBytesArray(BufferedImage image) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(image, "png", baos);
        return baos.toByteArray();
    }
}
