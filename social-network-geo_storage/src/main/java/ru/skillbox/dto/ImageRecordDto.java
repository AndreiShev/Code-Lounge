package ru.skillbox.dto;

import lombok.*;

import java.time.Instant;
import java.util.UUID;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ImageRecordDto {
  private Long id;
  private UUID userId;
  private String value;
  private Instant lastUpdated;
}
