package com.synchrony.userapp.repository;

import com.synchrony.userapp.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserRepository  extends JpaRepository<User, UUID> {

    Optional<User> findByEmail(String email);

    Optional<User>  findByEmailAndPassword(String email, String password);

    Optional<User> findByIdAndEmail(String userId, String userNameFromJwtToken);
}
