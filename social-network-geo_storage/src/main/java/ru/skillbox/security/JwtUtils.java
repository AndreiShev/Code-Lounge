package ru.skillbox.security;

import com.nimbusds.jwt.SignedJWT;
import lombok.experimental.UtilityClass;
import org.apache.commons.lang.StringUtils;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import ru.skillbox.exception.BadArgumentException;

import java.text.MessageFormat;
import java.text.ParseException;
import java.util.UUID;

@UtilityClass
public class JwtUtils {

  public String getUserIdFromToken (String token) {
    try {
      return SignedJWT.parse(StringUtils.substringAfter(token, " "))
            .getPayload()
            .toJSONObject()
            .get("id")
            .toString();
    } catch (ParseException | IllegalArgumentException | NullPointerException e) {
      throw new BadArgumentException(MessageFormat.format(
            "Exception occurred while attempting to parse token {0}: {1}", token, e.getLocalizedMessage()));
    }
  }

  public UUID getUserIdFromContext () {
    User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    return UUID.fromString(user.getUsername());
  }
}
