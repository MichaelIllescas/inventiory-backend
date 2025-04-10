/**
 * Repositorio JPA para la entidad Company.
 * Proporciona métodos personalizados para buscar y verificar empresas asociadas a usuarios.
 */
package com.imperial_net.inventioryApp.companies.repository;

import com.imperial_net.inventioryApp.companies.model.Company;
import com.imperial_net.inventioryApp.users.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CompanyRepository extends JpaRepository<Company, Long> {

    /**
     * Busca una empresa asociada a un usuario dado.
     *
     * @param user usuario asociado a la empresa
     * @return empresa si existe, en formato Optional
     */
    Optional<Company> findByUser(User user);

    /**
     * Verifica si ya existe una empresa asociada a un usuario.
     *
     * @param user usuario a verificar
     * @return true si existe una empresa registrada para ese usuario
     */
    boolean existsByUser(User user);
}
