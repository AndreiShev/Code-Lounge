package ru.skillbox.security;

import feign.FeignException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import ru.skillbox.exception.AccessResourseException;
import ru.skillbox.httpclient.InnerFeignClient;

import java.io.IOException;
import java.text.MessageFormat;
import java.util.ArrayList;

@Component
@RequiredArgsConstructor
public class JwtokenFilter extends OncePerRequestFilter {
  private final InnerFeignClient client;
  @Value("${app.fake-psw}")
  private String psw;

  @Override
  protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
        throws ServletException, IOException {
    String authHeader = request.getHeader("Authorization");
    if (authHeader != null && authHeader.startsWith("Bearer ") && confirmToken(authHeader)) {
      SecurityContextHolder.getContext().setAuthentication(new UsernamePasswordAuthenticationToken(
            new User(JwtUtils.getUserIdFromToken(authHeader), psw, new ArrayList<>()),
            null, new ArrayList<>()));
    }
    filterChain.doFilter(request, response);
  }

  private boolean confirmToken(String token) {
    boolean result;
    try {
      result = client.validateToken(token);
    } catch (FeignException ex) {
      throw new AccessResourseException(MessageFormat.format(
            "Can`t retrieve answer from auth-service to confirm token: >>{0}<<!", token));
    }
    return result;
  }
}
