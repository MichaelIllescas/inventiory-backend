package com.imperial_net.inventioryApp.repositories;


import com.imperial_net.inventioryApp.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);
    Optional<User> findByDocumentNumber(String documentNumber);
}
