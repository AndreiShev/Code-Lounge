package ru.skillbox.dto.hhaccess;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AreaCard {
  private String id;
  @JsonProperty("parent_id")
  private String parentId;
  private String name;
  private AreaCard[] areas;
}
