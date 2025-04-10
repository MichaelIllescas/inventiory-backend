/**
 * Repositorio JPA para la entidad Client.
 * Proporciona métodos para acceder, consultar y contar clientes asociados a un usuario.
 */
package com.imperial_net.inventioryApp.clients.repository;

import com.imperial_net.inventioryApp.clients.models.Client;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface ClientRepository extends JpaRepository<Client, Long> {

    /**
     * Busca todos los clientes creados por un usuario dado.
     *
     * @param userId ID del usuario
     * @return lista de clientes
     */
    List<Client> findAllByCreatedBy_Id(Long userId);

    /**
     * Busca un cliente por su número de documento y el ID del usuario creador.
     *
     * @param documentNumber número de documento del cliente
     * @param userId ID del usuario
     * @return cliente correspondiente si existe
     */
    Optional<Client> findByDocumentNumberAndCreatedBy_Id(String documentNumber, Long userId);

    /**
     * Cuenta la cantidad total de clientes asociados a un usuario.
     *
     * @param userId ID del usuario
     * @return cantidad de clientes
     */
    @Query("SELECT COUNT(c) FROM Client c WHERE c.createdBy.id = :userId")
    Long countClientsByUserId(@Param("userId") Long userId);

    /**
     * Busca un cliente por email y por el ID del usuario que lo creó.
     *
     * @param email correo electrónico del cliente
     * @param id ID del usuario
     * @return cliente correspondiente si existe
     */
    Optional<Client> findByEmailAndCreatedBy_Id(
            @Email(message = "Debe ingresar un correo electrónico válido.")
            @Size(max = 100, message = "El email no puede superar los 100 caracteres.")
            String email,
            Long id);

    /**
     * Busca un cliente por su email sin importar el usuario creador.
     *
     * @param email correo electrónico del cliente
     * @return cliente correspondiente si existe
     */
    Optional<Client> findByEmail(
            @Email(message = "Debe ingresar un correo electrónico válido.")
            @Size(max = 100, message = "El email no puede superar los 100 caracteres.")
            String email);
}