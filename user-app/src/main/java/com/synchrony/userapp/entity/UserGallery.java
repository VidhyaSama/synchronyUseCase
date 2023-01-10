package com.synchrony.userapp.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.Type;

import javax.persistence.*;
import java.util.UUID;

@Data
@NoArgsConstructor
@Entity
@Table(name = "user_gallery")
public class UserGallery {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Type(type="uuid-char")
    @Column(name="id", columnDefinition = "VARCHAR(255)",updatable = false, nullable = false)
    private UUID id;

    @ManyToOne
    private User user;

    @Lob
    @Column(name = "image", length = Integer.MAX_VALUE)
    private byte[] image;

    private String fileName;


}
