package ru.skillbox.dto.responses;


import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
public class ErrorResponse {
    @JsonProperty(namespace = "error_description")
    private String errorDescription;
}
