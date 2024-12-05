package ru.skillbox.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GetMessagesRs {

    private String error;
    private String errorDescription;
    private Long timestamp;
    private Integer total;
    private Integer offset;
    private Integer perPage;
    private List<MessageDto> content;

}
