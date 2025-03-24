package com.imperial_net.inventioryApp.repositories;

import com.imperial_net.inventioryApp.models.Client;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import org.apache.el.stream.Stream;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ClientRepository extends JpaRepository<Client, Long> {
    List<Client> findAllByCreatedBy_Id(Long userId);
    Optional<Client> findByDocumentNumberAndCreatedBy_Id(String documentNumber, Long userId);

    @Query("SELECT COUNT(c) FROM Client c WHERE c.createdBy.id = :userId")
    Long countClientsByUserId(@Param("userId") Long userId);


    Optional<Client> findByEmailAndCreatedBy_Id(@Email(message = "Debe ingresar un correo electrónico válido.") @Size(max = 100, message = "El email no puede superar los 100 caracteres.") String email, Long id);

    Optional<Client>  findByEmail(@Email(message = "Debe ingresar un correo electrónico válido.") @Size(max = 100, message = "El email no puede superar los 100 caracteres.") String email);
}
