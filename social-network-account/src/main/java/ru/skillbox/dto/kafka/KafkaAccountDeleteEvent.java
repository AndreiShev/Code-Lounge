package ru.skillbox.dto.kafka;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class KafkaAccountDeleteEvent {

    private UUID userId;

}
