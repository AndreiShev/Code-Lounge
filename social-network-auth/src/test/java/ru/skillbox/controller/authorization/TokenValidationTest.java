package ru.skillbox.controller.authorization;


import org.awaitility.Awaitility;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;
import org.springframework.http.HttpHeaders;
import ru.skillbox.TestingContext;

import java.util.concurrent.TimeUnit;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;


@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class TokenValidationTest extends TestingContext {

    @Test
    void testTokenValidation_whenEverythingIsFilledInCorrectly() throws Exception {
        String token = "Bearer " + actualToken;
        mockMvc.perform(get("/api/v1/auth/tokenValidation")
                        .header(HttpHeaders.AUTHORIZATION, token))
                .equals(true);
    }

    @Test
    void testTokenValidation_withoutBearer() throws Exception {
        mockMvc.perform(get("/api/v1/auth/tokenValidation")
                        .header(HttpHeaders.AUTHORIZATION, actualToken))
                .equals(true);
    }

    @Test
    void testTokenValidation_randomString() throws Exception {
        mockMvc.perform(get("/api/v1/auth/tokenValidation")
                        .header(HttpHeaders.AUTHORIZATION, "piuashfgipujhasdfiogj"))
                .equals(false);
    }

    @Test
    void testTokenValidation_expiration() throws Exception {
        Awaitility.await()
                .atMost(16, TimeUnit.SECONDS)
                .until(() -> true);

        mockMvc.perform(get("/api/v1/auth/tokenValidation")
                        .header(HttpHeaders.AUTHORIZATION, actualToken))
                .equals(false);
    }
}
