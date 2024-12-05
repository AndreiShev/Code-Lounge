package ru.skillbox.mapper;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonDeserializer;
import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;

@Slf4j
public class LocalDateTimeDeserializer extends JsonDeserializer<LocalDateTime> {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");

    @Override
    public LocalDateTime deserialize(JsonParser parser, DeserializationContext ctxt) {
        try {
            String value = parser.getText();
            return LocalDateTime.parse(value, FORMATTER);
        } catch (DateTimeParseException | IOException e) {
            log.warn("Birthdate parsing error: " + e.getMessage());
            return null;
        }
    }
}
