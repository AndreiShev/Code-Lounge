package ru.skillbox.entities;

import jakarta.persistence.*;
import lombok.*;

import java.io.Serializable;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

@Table
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
public class Account implements Serializable {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(columnDefinition="varchar(50)")
    private String email;

    @Column(columnDefinition="varchar")
    private String password;

    @Column(name = "first_name", columnDefinition="varchar(30)")
    private String firstName;

    @Column(name = "last_name", columnDefinition="varchar(30)")
    private String lastName;

    @Column(columnDefinition="varchar(100)")
    private String token;

    @ElementCollection(targetClass = RoleType.class, fetch = FetchType.EAGER)
    @JoinTable(name = "user_roles", joinColumns = @JoinColumn(name = "account_id"))
    @Column(name = "roles", nullable = false)
    @Enumerated(EnumType.STRING)
    @Builder.Default
    private Set<RoleType> roles = new HashSet<>();
}
