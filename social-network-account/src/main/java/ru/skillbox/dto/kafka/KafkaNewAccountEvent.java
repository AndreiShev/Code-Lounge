package ru.skillbox.dto.kafka;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@Builder
public class KafkaNewAccountEvent {

    private UUID id;
    private String email;
    private String firstName;
    private String lastName;

}
