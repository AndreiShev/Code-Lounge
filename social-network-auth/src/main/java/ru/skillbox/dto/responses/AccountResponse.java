package ru.skillbox.dto.responses;

import lombok.*;


@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
public class AccountResponse {
    private String email;
    private String firstName;
    private String lastName;
}
