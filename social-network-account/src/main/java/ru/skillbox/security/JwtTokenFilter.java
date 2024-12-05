package ru.skillbox.security;

import com.nimbusds.jwt.SignedJWT;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.http.HttpHeaders;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;
import ru.skillbox.client.OpenFeignClient;
import ru.skillbox.entity.RoleType;

import java.io.IOException;
import java.text.ParseException;
import java.util.List;

@RequiredArgsConstructor
@Slf4j
@Component
public class JwtTokenFilter extends OncePerRequestFilter {

    private final OpenFeignClient openFeignClient;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        try {
            String token = getToken(request);
            if (token != null && openFeignClient.validateToken(request.getHeader(HttpHeaders.AUTHORIZATION)).isValid()) {

                String accountId = getIdFromToken(token);
                List<SimpleGrantedAuthority> authorities = getRolesFromToken(token).stream()
                        .map(r -> new SimpleGrantedAuthority(r.name()))
                        .toList();

                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                        new User(accountId, "", authorities),
                        null,
                        authorities
                );
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        } catch (Exception e) {
            log.error("Cannot set account authentication: {}", e.getMessage());
        }
        filterChain.doFilter(request, response);
    }

    private String getToken(HttpServletRequest request) {
        String headerAuth = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (StringUtils.hasText(headerAuth) && headerAuth.startsWith("Bearer ")) {
            return headerAuth.substring(7);
        }
        return null;
    }

    private String getIdFromToken(String token) throws ParseException {
        return SignedJWT.parse(token).getPayload().toJSONObject().get("id").toString();
    }

    private List<RoleType> getRolesFromToken(String token) throws ParseException {
        List<String> roles = (List<String>) SignedJWT.parse(token).getPayload().toJSONObject().get("roles");
        return roles.stream()
                .map(RoleType::valueOf)
                .toList();
    }

}