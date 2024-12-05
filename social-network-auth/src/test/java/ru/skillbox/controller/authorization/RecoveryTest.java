package ru.skillbox.controller.authorization;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.http.MediaType;
import ru.skillbox.TestingContext;
import ru.skillbox.dto.requests.RecoveryRequest;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class RecoveryTest extends TestingContext {
    private final RecoveryRequest baseRequest = new RecoveryRequest("HhAp^hSx6e421", "Default@vk.com");
    private final ObjectWriter objectWriter = new ObjectMapper().writer().withDefaultPrettyPrinter();

    @Test
    void testRecoveryPassword_whenEverythingIsFilledInCorrectly() throws Exception {
        mockMvc.perform(post("/api/v1/auth/recovery")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectWriter.writeValueAsString(baseRequest)))
                .andExpect(status().isOk());

    }

    @Test
    void testRecoveryPassword_EmailNotExist() throws Exception {
        RecoveryRequest req = new RecoveryRequest(baseRequest);
        req.setEmail("Defaulddt@vk.com");
        mockMvc.perform(post("/api/v1/auth/recovery")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectWriter.writeValueAsString(req)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testRecoveryPassword_passwordIsMax() throws Exception {
        RecoveryRequest req = new RecoveryRequest(baseRequest);
        req.setTemp("HhAp^hSx6e4211111111");
        mockMvc.perform(post("/api/v1/auth/recovery")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectWriter.writeValueAsString(req)))
                .andExpect(status().isOk());
    }

    @Test
    void testRecoveryPassword_passwordIsMin() throws Exception {
        RecoveryRequest req = new RecoveryRequest(baseRequest);
        req.setTemp("H5Ap^hSx");
        mockMvc.perform(post("/api/v1/auth/recovery")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectWriter.writeValueAsString(req)))
                .andExpect(status().isOk());
    }

    @Test
    void testRecoveryPassword_passwordIsGreaterThanMax() throws Exception {
        RecoveryRequest req = new RecoveryRequest(baseRequest);
        req.setTemp("HhAp^hSx6e42111111112");
        mockMvc.perform(post("/api/v1/auth/recovery")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectWriter.writeValueAsString(req)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testRecoveryPassword_passwordLessThanMin() throws Exception {
        RecoveryRequest req = new RecoveryRequest(baseRequest);
        req.setTemp("HhAp^h");
        mockMvc.perform(post("/api/v1/auth/recovery")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectWriter.writeValueAsString(req)))
                .andExpect(status().isBadRequest());
    }

    @ParameterizedTest
    @ValueSource(strings = {"HhAp^   hSx6e421", "   HhAp^hSx6e421   ", "                 "})
    void testRecoveryPassword_passwordContainsSpaces(String input) throws Exception {
        RecoveryRequest req = new RecoveryRequest(baseRequest);
        req.setTemp(input);
        mockMvc.perform(post("/api/v1/auth/recovery")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectWriter.writeValueAsString(req)))
                .andExpect(status().isBadRequest());
    }

    @ParameterizedTest
    @ValueSource(strings = {"HhAp^hSx6e421", "Hh7_____"})
    void testRecoveryPassword_whenPasswordIsCorrect(String input) throws Exception {
        RecoveryRequest req = new RecoveryRequest(baseRequest);
        req.setTemp(input);
        mockMvc.perform(post("/api/v1/auth/recovery")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectWriter.writeValueAsString(req)))
                .andExpect(status().isOk());
    }

}
