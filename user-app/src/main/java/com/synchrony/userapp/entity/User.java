package com.synchrony.userapp.entity;

import lombok.*;
import org.hibernate.annotations.GenericGenerator;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.util.UUID;

@Data
@Entity
@Table(name = "users")
@AllArgsConstructor
@NoArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Type(type="uuid-char")
    @Column(name="id", columnDefinition = "VARCHAR(255)",updatable = false, nullable = false)
    private UUID id;
    private String email;
    private String password;
}
