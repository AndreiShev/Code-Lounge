package ru.skillbox.controller.authorization;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.ObjectWriter;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.NullSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.http.MediaType;
import ru.skillbox.TestingContext;
import ru.skillbox.dto.requests.AccountRequest;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@Slf4j
class RegisterTest extends TestingContext {
    private final AccountRequest baseRequest = AccountRequest.builder()
            .firstName("Андрей")
            .lastName("Шевелев")
            .email("shevelv_andrey@vk.com")
            .password1("HhAp^_hSx6e421")
            .password2("HhAp^_hSx6e421")
            .build();
    private final ObjectWriter objectWriter = new ObjectMapper().writer().withDefaultPrettyPrinter();


    @Test
    void testRegisterNewAccount_whenEverythingIsFilledInCorrectly() throws Exception {
        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectWriter.writeValueAsString(baseRequest)))
                .andExpect(status().isOk());
    }

    @Test
    void testRegisterNewAccount_whenEmailAlreadyExist() throws Exception {
        mockMvc.perform(post("/api/v1/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectWriter.writeValueAsString(baseRequest)));


        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectWriter.writeValueAsString(baseRequest)))
                .andExpect(status().isBadRequest());
    }

    @Test
    void testRegisterNewAccount_whenPasswordsNotEquals() throws Exception {
        AccountRequest req = new AccountRequest(baseRequest);
        req.setPassword2("HhAp^hSx6e422");
        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectWriter.writeValueAsString(req)))
                .andExpect(status().isBadRequest());

    }

    @Test
    void testRegisterNewAccount_whenEmailNotCorrect() throws Exception {
        AccountRequest req = new AccountRequest(baseRequest);
        req.setEmail("HhAp^hSx6e422");
        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectWriter.writeValueAsString(req)))
                .andExpect(status().isBadRequest());
    }

    @ParameterizedTest
    @NullSource
    @ValueSource(strings = {"", "    "})
    void testRegisterNewAccount_whenLastNameNotCorrect(String input) throws Exception {
        AccountRequest req = new AccountRequest(baseRequest);
        req.setLastName(input);
        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectWriter.writeValueAsString(req)))
                .andExpect(status().isBadRequest());

    }

    @ParameterizedTest
    @NullSource
    @ValueSource(strings = {"", "    "})
    void testRegisterNewAccount_whenFirstNameNotCorrect(String input) throws Exception {
        AccountRequest req = new AccountRequest(baseRequest);
        req.setFirstName(input);
        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectWriter.writeValueAsString(req)))
                .andExpect(status().isBadRequest());

    }

    @ParameterizedTest
    @ValueSource(strings = {"HhAp^hSx6e4211111111", "H5Ap^hSx"})
    void testRegisterNewAccount_passwordIsMaxAndMin(String value) throws Exception {
        AccountRequest req = new AccountRequest(baseRequest);
        req.setPassword1(value);
        req.setPassword2(value);
        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectWriter.writeValueAsString(req)))
                .andExpect(status().isOk());
    }

    @ParameterizedTest
    @ValueSource(strings = {"HhAp^hSx6e42111111112", "HhAp^h", "^64211111111", "HhAp^hSxe", "HhAphSx6e421", "HhAp^_hSxФФа6e421"})
    void testRegisterNewAccount_passwordCheckBoundaryValues(String value) throws Exception {
        AccountRequest req = new AccountRequest(baseRequest);
        req.setPassword1(value);
        req.setPassword2(value);
        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectWriter.writeValueAsString(req)))
                .andExpect(status().isBadRequest());
    }

    @ParameterizedTest
    @ValueSource(strings = {"HhAp^   hSx6e421", "   HhAp^hSx6e421   ", "                 "})
    void testRegisterNewAccount_passwordContainsSpaces(String input) throws Exception {
        AccountRequest req = new AccountRequest(baseRequest);
        req.setPassword1(input);
        req.setPassword2(input);
        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectWriter.writeValueAsString(req)))
                .andExpect(status().isBadRequest());
    }

    @ParameterizedTest
    @ValueSource(strings = {"HhAp^hSx6e421", "Hh7_____"})
    void testRegisterNewAccount_whenPasswordIsCorrect(String input) throws Exception {
        AccountRequest req = new AccountRequest(baseRequest);
        req.setPassword1(input);
        req.setPassword2(input);
        mockMvc.perform(post("/api/v1/auth/register")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectWriter.writeValueAsString(req)))
                .andExpect(status().isOk());
    }
}
