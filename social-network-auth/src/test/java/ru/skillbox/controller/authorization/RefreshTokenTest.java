package ru.skillbox.controller.authorization;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import org.junit.jupiter.api.Test;
import org.springframework.http.MediaType;
import ru.skillbox.TestingContext;
import ru.skillbox.dto.requests.RefreshTokenRequest;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class RefreshTokenTest extends TestingContext {
    private final ObjectWriter objectWriter = new ObjectMapper().writer().withDefaultPrettyPrinter();

    @Test
    void refreshToken_whenEverythingIsFilledInCorrectly() throws Exception {
        mockMvc.perform(post("/api/v1/auth/refresh-token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectWriter.writeValueAsString(new RefreshTokenRequest(actualToken))))
                .andExpect(status().isOk());

    }

    @Test
    void refreshToken_whenTokenIsRandom() throws Exception {
        mockMvc.perform(post("/api/v1/auth/refresh-token")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectWriter.writeValueAsString(new RefreshTokenRequest("sadf.gsdf.gdsgh"))))
                .andExpect(status().isForbidden());
    }
}
