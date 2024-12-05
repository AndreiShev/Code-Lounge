package ru.skillbox.security;

import com.nimbusds.jwt.SignedJWT;
import lombok.experimental.UtilityClass;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import ru.skillbox.exception.InvalidDataException;

import java.text.ParseException;
import java.util.List;

@UtilityClass
@Slf4j
public class JwtTokenUtil {

    public static String getAccountIdFromToken(String token) {
        try {
            return SignedJWT.parse(token).getPayload().toJSONObject().get("id").toString();
        } catch (ParseException e) {
            log.error("Error with parsing account id from token: {}", e.getMessage());
            throw new InvalidDataException("Error with parsing account id from token");
        }
    }

    public static List<SimpleGrantedAuthority> getRolesFromToken(String token) {
        try {
            List<String> roles = (List<String>) SignedJWT.parse(token).getPayload().toJSONObject().get("roles");
            return roles.stream()
                    .map(SimpleGrantedAuthority::new)
                    .toList();
        } catch (ParseException e) {
            log.error("Error with parsing roles from token: {}", e.getMessage());
            throw new InvalidDataException("Error with parsing roles from token");
        }
    }

}
