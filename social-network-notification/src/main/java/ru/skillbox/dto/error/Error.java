package ru.skillbox.dto.error;

import lombok.*;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Error {
    private String message;
    private String description;
}
