package ru.skillbox.controller.authorization;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.http.MediaType;
import ru.skillbox.TestingContext;
import ru.skillbox.dto.requests.LoginRequest;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

class LoginTest extends TestingContext {

    @Test
    void testLogin_whenEverythingIsFilledInCorrectly() throws Exception {
        LoginRequest request = LoginRequest.builder()
                .email("Default@vk.com")
                .password("HhAp^hSx6e421")
                .build();

        ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(ow.writeValueAsString(request)))
                .andExpect(status().isOk());

    }

    @Test
    void testLogin_whenEmailNotExist() throws Exception {
        LoginRequest request = LoginRequest.builder()
                .email("Defaut@vk.com")
                .password("HhAp^hSx6e421")
                .build();

        ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(ow.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }

    @ParameterizedTest
    @ValueSource(strings = {"HhAp^hSx6ffe421", "", "    "})
    void testLogin_whenPasswordNotCorrect(String input) throws Exception {
        LoginRequest request = LoginRequest.builder()
                .email("Default@vk.com")
                .password(input)
                .build();

        ObjectWriter ow = new ObjectMapper().writer().withDefaultPrettyPrinter();
        mockMvc.perform(post("/api/v1/auth/login")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(ow.writeValueAsString(request)))
                .andExpect(status().isBadRequest());
    }
}
