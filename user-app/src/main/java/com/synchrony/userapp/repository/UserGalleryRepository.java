package com.synchrony.userapp.repository;

import com.synchrony.userapp.entity.UserGallery;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserGalleryRepository extends JpaRepository<UserGallery, UUID> {

    List<UserGallery> findByUserId(UUID userId);
}
