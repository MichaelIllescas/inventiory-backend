package com.imperial_net.inventioryApp.products.repository;

import com.imperial_net.inventioryApp.products.models.Brand;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Repositorio para acceder y gestionar datos de marcas en la base de datos.
 * Extiende {@link JpaRepository} para operaciones CRUD y agrega métodos personalizados.
 */
@Repository
public interface BrandRepository extends JpaRepository<Brand, Long> {

    /**
     * Busca una marca por su nombre.
     *
     * @param name nombre de la marca.
     * @return un {@link Optional} con la marca si existe, o vacío si no se encuentra.
     */
    Optional<Brand> findByName(String name);
}
