package com.imperial_net.inventioryApp.repositories;

import com.imperial_net.inventioryApp.models.Client;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ClientRepository extends JpaRepository<Client, Long> {
    List<Client> findAllByCreatedBy_Id(Long userId);
    Optional<Client> findByDocumentNumber(String documentNumber);
}
