package ru.skillbox.security;

import lombok.experimental.UtilityClass;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import ru.skillbox.exception.BadRequestException;

import java.util.UUID;

@UtilityClass
public class SecurityUtils {

    public static UUID getUUIDFromSecurityContext() {
        var currentPrincipal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (currentPrincipal instanceof User user && user.getUsername() != null) {
            return UUID.fromString(user.getUsername());
        }
        throw new BadRequestException("Account id is null!");
    }

}

