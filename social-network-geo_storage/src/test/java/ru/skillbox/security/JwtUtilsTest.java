package ru.skillbox.security;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import ru.skillbox.exception.BadArgumentException;

import static org.junit.jupiter.api.Assertions.*;

class JwtUtilsTest {
  @Autowired
  JwtUtils jwtUtils;

  @Test
  void testGetUserIdFromToken() {
    String header = "Bearer eyJhbGciOiJIUzI1NiJ9.eyJyb2xlcyI6WyJST0xFX1VTRVIiXSwiaWQiOiJhYTE1YzAyNS1lZWQxLTQ2ZTQtYjEzNi1kNjUzOGRmODM3MDMiLCJleHAiOjE3Mjg5MTM2MDcsImlhdCI6MTcyODgyNzIwN30.8ZyMCDl1zUjhbKfS24u7VVXaIDHFW6qDx2jrDtsshKQ";
    assertEquals("aa15c025-eed1-46e4-b136-d6538df83703",
         JwtUtils.getUserIdFromToken(header));
  }

  @Test
  void testGetUserIdFromTokenFiled() {
    String header = "Bearer eyJhbGciOiJIUzI1NiJ9.eyJyb2xlcyI6WIiXSwiaWQiOiJhYTE1YzAyNS1lZQtYjEzNi1kNjUzOGRmODM3MDMiLCJleHAiOjE3Mjg5MTM2MDcsImlhdCI6MTcyODgyNzIwN30.8ZyMCDl1zUjhbKfS24u7VVXaIDHFW6qDx2jrDtsshKQ";
    BadArgumentException thrown = assertThrows(BadArgumentException.class,
          () -> JwtUtils.getUserIdFromToken(header));
    assertTrue(thrown.getMessage().contains("Exception occurred while attempting to parse token"));
  }
}