package ru.skillbox.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UnreadCountRs {

    private String error;
    private Long timestamp;
    private UnreadCountDto data;
    private String errorDescription;
}
