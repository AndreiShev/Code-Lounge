package ru.skillbox.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.Instant;
import java.util.UUID;

@Entity
@Table(name = "images")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ImageRecord {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;
  @Column(name = "user_id")
  private UUID userId;
  @Column(name = "value_", columnDefinition = "VARCHAR(255)")
  private String value;
  @Column(name = "last_updated", columnDefinition = "TIMESTAMP")
  private Instant lastUpdated;
}
