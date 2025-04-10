package com.imperial_net.inventioryApp.providers.repository;

import com.imperial_net.inventioryApp.providers.model.Provider;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repositorio JPA para gestionar operaciones CRUD y consultas personalizadas de la entidad {@link Provider}.
 */
@Repository
public interface ProviderRepository extends JpaRepository<Provider, Long> {

    /**
     * Obtiene todos los proveedores registrados por un usuario específico.
     *
     * @param userId ID del usuario.
     * @return lista de proveedores.
     */
    List<Provider> findAllByCreatedBy_Id(Long userId);

    /**
     * Busca un proveedor por su nombre exacto.
     *
     * @param name nombre del proveedor.
     * @return proveedor encontrado, si existe.
     */
    Optional<Provider> findByName(String name);

    /**
     * Busca un proveedor por su CUIT/CUIL.
     *
     * @param taxId número de CUIT/CUIL.
     * @return proveedor encontrado, si existe.
     */
    Optional<Provider> findByTaxId(String taxId);

    /**
     * Obtiene todos los proveedores activos de un usuario específico.
     *
     * @param userId ID del usuario.
     * @return lista de proveedores activos.
     */
    List<Provider> findAllByCreatedBy_IdAndStateTrue(Long userId);

    /**
     * Cuenta la cantidad de proveedores registrados por un usuario.
     *
     * @param userId ID del usuario.
     * @return cantidad de proveedores.
     */
    Long countByCreatedBy_Id(Long userId);
}
