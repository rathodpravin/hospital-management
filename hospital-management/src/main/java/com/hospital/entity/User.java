package com.hospital.entity;
import jakarta.persistence.*;
import lombok.*;
@Entity @Table(name = "users") @Getter @Setter @NoArgsConstructor
public class User {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY) private Long id;
    @Column(unique = true, nullable = false) private String username;
    @Column(nullable = false) private String password; // BCrypt hash
    @Enumerated(EnumType.STRING) @Column(nullable = false) private Role role;
}
