package ru.skillbox.dto;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.PositiveOrZero;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AccountByFilterDto {

    private AccountSearchDto accountSearchDto;
    @NotNull(message = "Параметр 'pageSize' должен быть заполнен")
    @Positive
    private Integer pageSize;
    @NotNull(message = "Параметр 'pageNumber' должен быть заполнен")
    @PositiveOrZero
    private Integer pageNumber;

}
