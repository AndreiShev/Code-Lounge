package ru.skillbox.utils;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class GetCurrentUsernameTest {

    private MockedStatic<SecurityContextHolder> securityContextHolderMock;

    @BeforeEach
    public void setUp() {
        securityContextHolderMock = Mockito.mockStatic(SecurityContextHolder.class);
    }

    @AfterEach
    public void tearDown() {
        securityContextHolderMock.close();
    }

    @Test
    void getCurrentUsername_shouldReturnUsername_whenAuthenticatedWithUserDetails() {
        SecurityContext securityContext = mock(SecurityContext.class);
        Authentication authentication = mock(Authentication.class);
        UserDetails userDetails = mock(UserDetails.class);

        when(SecurityContextHolder.getContext()).thenReturn(securityContext);
        when(securityContext.getAuthentication()).thenReturn(authentication);
        when(authentication.isAuthenticated()).thenReturn(true);
        when(authentication.getPrincipal()).thenReturn(userDetails);
        when(userDetails.getUsername()).thenReturn("testuser");

        String username = GetCurrentUsername.getCurrentUsername();

        assertEquals("testuser", username);
    }

    @Test
    void getCurrentUsername_shouldThrowUsernameNotFoundException_whenAuthenticationIsNull() {
        SecurityContext securityContext = mock(SecurityContext.class);
        when(SecurityContextHolder.getContext()).thenReturn(securityContext);
        when(securityContext.getAuthentication()).thenReturn(null);

        assertThrows(UsernameNotFoundException.class, GetCurrentUsername::getCurrentUsername);
    }
}
