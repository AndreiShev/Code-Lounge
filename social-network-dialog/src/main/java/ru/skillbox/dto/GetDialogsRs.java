package ru.skillbox.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class GetDialogsRs {

    private String error;
    private String errorDescription;
    private Long timestamp;
    private Integer total;
    private Integer offset;
    private Integer perPage;
    private UUID currentUserId;
    private List<DialogDto> content;
}
