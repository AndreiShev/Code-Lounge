package ru.skillbox.dto;

import lombok.*;

import java.io.Serializable;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class CityDto implements Serializable {
  private Long id;
  private String title;
  private Long countryId;
}
