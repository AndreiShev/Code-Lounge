package ru.skillbox.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import ru.skillbox.enums.StatusCode;


import java.util.UUID;

@Getter
@Setter
@Entity
@Table(
        name = "friends",
        indexes = {
                @Index(name = "friend_account_id_index", columnList = "account_id"),
                @Index(name = "friend_friend_id_index", columnList = "friend_id"),
        },
        uniqueConstraints = {
                @UniqueConstraint(
                        name = "account_id_friend_id_constraint", columnNames = {"account_id", "friend_id"}
                )
        }
)
public class Friend {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(name = "account_id", nullable = false)
    private UUID accountId;
    @Column(name = "friend_id", nullable = false)
    private UUID friendId;
    @Column(name = "status_code", nullable = false)
    @Enumerated(EnumType.STRING)
    private StatusCode statusCode;
}
