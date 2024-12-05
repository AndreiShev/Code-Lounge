package ru.skillbox.dto;

import lombok.*;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class CountryDto implements Serializable {
  private Long id;
  private String title;
  @Builder.Default
  private List<CityDto> cities = new ArrayList<>();
}
