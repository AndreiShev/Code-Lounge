package ru.skillbox.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.skillbox.entity.Status;

import java.time.LocalDateTime;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ShortMessageForDialogDto {

    private String messageText;
    private LocalDateTime time;
    private UUID authorId;
    private Status readStatus;
}
