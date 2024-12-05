package ru.skillbox.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.*;

@Entity
@Table(name = "accounts")
@Getter
@Setter
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
public class Account {

    @Id
    private UUID id;
    @Column(nullable = false)
    private String email;
    private String city;
    private String country;
    @Column(name = "first_name", nullable = false)
    private String firstName;
    @Column(name = "last_name", nullable = false)
    private String lastName;
    @Column(name = "birth_date")
    private LocalDateTime birthDate;
    @Column(name = "blocked", nullable = false)
    private Boolean isBlocked;
    @Column(name = "deleted", nullable = false)
    private Boolean isDeleted;
    @Column(name = "online", nullable = false)
    private Boolean isOnline;
    private String phone;
    private String about;
    @Column(name = "profile_cover")
    private String profileCover;
    @Column(name = "emoji_status")
    private String emojiStatus;
    @Column(name = "photo")
    private String photo;
    @CreationTimestamp
    private LocalDateTime createdOn;
    @UpdateTimestamp
    private LocalDateTime updatedOn;
    @Column(name = "last_online_time")
    private LocalDateTime lastOnlineTime;
    @Transient
    @Builder.Default
    private List<RoleType> roles = new ArrayList<>();

}
