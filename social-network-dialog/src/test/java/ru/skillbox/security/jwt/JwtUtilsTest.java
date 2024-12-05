package ru.skillbox.security.jwt;

import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import ru.skillbox.client.AuthClient;
import ru.skillbox.exception.JwtParseException;

import static org.junit.jupiter.api.Assertions.*;
class JwtUtilsTest {

    @Test
    void validateToken_ShouldReturnTrue() {
        String correctToken = "Bearer TEST";
        AuthClient authClient = Mockito.mock(AuthClient.class);
        Mockito.when(authClient.validateToken(Mockito.anyString())).thenReturn(true);
        authClient.validateToken(correctToken);
        Mockito.verify(authClient, Mockito.times(1)).validateToken(Mockito.anyString());
        assertTrue(authClient.validateToken(correctToken));
    }


    @Test
    void getUsername_ShouldReturnCorrectUsername() {
        String correctToken = "eyJhbGciOiJIUzI1NiJ9.eyJyb2xlcyI6WyJST0xFX1VTRVIiXSwiaWQiOiJjZmU1ZjEzYi1lZWQyLTRhYTktOGRjMC1hODBhMTcwMTg0OGUiLCJleHAiOjE3Mjg2MzYyNjcsImlhdCI6MTcyODU0OTg2N30.YOLI76k_cvmr8xCacojnsDX_JmgdrOK-oarp4cX2yOw";
        String currentUsername = JwtUtils.getUsername(correctToken);
        assertEquals("cfe5f13b-eed2-4aa9-8dc0-a80a1701848e", currentUsername );
    }

    @Test
    void getUsername_ShouldThrowJwtException() {
        String correctToken = "TEST TOKEN";
        assertThrows(JwtParseException.class, () -> JwtUtils.getUsername(correctToken));
    }

}