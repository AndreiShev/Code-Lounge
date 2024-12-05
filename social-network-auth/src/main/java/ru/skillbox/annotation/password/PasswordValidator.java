package ru.skillbox.annotation.password;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.text.MessageFormat;
import java.util.regex.Pattern;

public class PasswordValidator implements ConstraintValidator<Password, String> {
    private static final String PATTERN_FOR_NUMBERS = "^(?=.*\\d).*$";
    private static final String PATTERN_FOR_LATIN_LETTERS = "^(?=.*[a-z])(?=.*[A-Z])(?!.*\\s).*$";
    private static final String PATTERN_FOR_SPECIAL_CHARACTERS = "^(?=.*[!@№;%:?*()_=+^$]).*$";
    
    @Override
    public boolean isValid(String s, ConstraintValidatorContext context) {
        if (s.contains(" ")) {
            setContext(context, "Пароль не должен содержать пробелы.");
            return false;
        }

        if (s.length() < 8 || s.length() > 20) {
            setContext(context, MessageFormat.format("Пароль должен содержать от {0} до {1} символлов", 8, 20));
            return false;
        }

        if (!replaceAllAllowedCharacters(s).isEmpty()) {
            setContext(context, "Пароль должен содержать буквы латинского алфавита, цифры, специальные символы.");
            return false;
        }

        if (!Pattern.matches(PATTERN_FOR_NUMBERS, s)) {
            setContext(context, "Пароль должен содержать цифры.");
            return false;
        }

        if (!Pattern.matches(PATTERN_FOR_LATIN_LETTERS, s)) {
            setContext(context, "Пароль должен содержать заглавные и прописные латинские символы.");
            return false;
        }

        if (!Pattern.matches(PATTERN_FOR_SPECIAL_CHARACTERS, s)) {
            setContext(context, "Пароль должен содержать специальные символы.");
            return false;
        }

        return true;
    }

    private String replaceAllAllowedCharacters(String password) {
        String result = password.replaceAll("\\d", "");
        result = result.replaceAll("[a-zA-Z]", "");
        result = result.replaceAll("[!@№;%:?*()_=+^$]", "");

        return result;
    }

    private void setContext(ConstraintValidatorContext context, String resultValidate) {
        context.disableDefaultConstraintViolation();
        context.buildConstraintViolationWithTemplate(resultValidate).addConstraintViolation();
    }
}
