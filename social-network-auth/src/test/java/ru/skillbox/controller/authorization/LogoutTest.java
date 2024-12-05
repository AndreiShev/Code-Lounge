package ru.skillbox.controller.authorization;

import org.junit.jupiter.api.Test;
import org.springframework.http.HttpHeaders;
import ru.skillbox.TestingContext;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class LogoutTest extends TestingContext {

    @Test
    void testLogout_whenEverythingIsFilledInCorrectly() throws Exception {
        String token = "Bearer " + actualToken;
        mockMvc.perform(post("/api/v1/auth/logout")
                        .header(HttpHeaders.AUTHORIZATION, token))
                .andExpect(status().isOk());

    }
}
