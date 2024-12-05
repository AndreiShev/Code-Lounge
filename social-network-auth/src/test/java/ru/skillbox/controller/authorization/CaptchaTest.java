package ru.skillbox.controller.authorization;

import com.github.tomakehurst.wiremock.WireMockServer;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import ru.skillbox.TestingContext;
import ru.skillbox.WireMockConfig;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static ru.skillbox.CaptchaMocks.setupMockCaptchaResponse;

@ContextConfiguration(classes = { WireMockConfig.class })
class CaptchaTest extends TestingContext {

    @Autowired
    private WireMockServer mockCaptcha;

    @BeforeEach
    void setUp() {
        setupMockCaptchaResponse(mockCaptcha);
    }

    @Test
    void testCaptcha_whenAllIsCorrect() throws Exception {
        mockMvc.perform(get("/api/v1/auth/captcha")).andExpect(status().isOk());
    }
}
