package com.imperial_net.inventioryApp.products.repository;

import com.imperial_net.inventioryApp.products.models.Brand;
import com.imperial_net.inventioryApp.products.models.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Repositorio JPA para la entidad {@link Product}.
 * Proporciona métodos CRUD y consultas personalizadas para gestionar productos.
 */
@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    /**
     * Busca un producto por su nombre exacto.
     * Este método es útil cuando se necesita buscar un producto específico por su nombre.
     *
     * @param name nombre del producto.
     * @return un {@link Optional} que contiene el producto si se encuentra, o vacío si no.
     */
    Optional<Product> findByName(String name);

    /**
     * Verifica si ya existe un producto con el mismo nombre, código, marca y descripción.
     * Se utiliza para evitar la creación de productos duplicados con los mismos atributos clave.
     *
     * @param name        nombre del producto.
     * @param code        código del producto.
     * @param brand       marca asociada.
     * @param description descripción del producto.
     * @return true si existe un producto con esos datos, false en caso contrario.
     */
    boolean existsByNameAndCodeAndBrandAndDescription(String name, String code, Brand brand, String description);

    /**
     * Obtiene todos los productos registrados por un usuario específico.
     * Este método devuelve todos los productos que han sido registrados por un usuario determinado.
     *
     * @param userId ID del usuario.
     * @return lista de productos asociados a un usuario.
     */
    List<Product> findAllByRegistratedBy_Id(Long userId);

    /**
     * Obtiene todos los productos activos registrados por un usuario específico.
     * Este método filtra los productos activos, excluyendo aquellos con estado inactivo.
     *
     * @param userId ID del usuario.
     * @return lista de productos activos registrados por el usuario.
     */
    List<Product> findAllByRegistratedBy_IdAndStateTrue(Long userId);

    /**
     * Verifica si ya existe un producto con determinado código para un usuario.
     * Este método evita la creación de productos duplicados por código para un usuario determinado.
     *
     * @param code   código del producto.
     * @param userId ID del usuario.
     * @return true si el producto con ese código ya está registrado para el usuario, false en caso contrario.
     */
    boolean existsByCodeAndRegistratedBy_Id(String code, Long userId);

    /**
     * Cuenta la cantidad de productos registrados por un usuario específico.
     * Este método es útil para saber cuántos productos tiene registrados un usuario.
     *
     * @param userId ID del usuario.
     * @return cantidad de productos registrados por el usuario.
     */
    Long countByRegistratedBy_Id(Long userId);

    /**
     * Busca un producto por su código.
     * Este método es útil para buscar productos utilizando su código único.
     *
     * @param code código del producto.
     * @return un {@link Optional} que contiene el producto si se encuentra, o vacío si no.
     */
    Optional<Product> findByCode(String code);

    /**
     * Obtiene los productos con bajo stock registrados por un usuario.
     * Se consideran con bajo stock aquellos cuyo stock actual es menor o igual al stock mínimo.
     * Este método es útil para identificar productos cuyo inventario está por debajo de los niveles deseados.
     *
     * @param userId ID del usuario.
     * @return lista de productos con stock bajo.
     */
    @Query("SELECT p FROM Product p " +
            "WHERE p.stock <= p.minStock " +
            "AND p.registratedBy.id = :userId " +
            "AND p.state = true " +
            "ORDER BY p.stock ASC")
    List<Product> findLowStockProductsByUser(@Param("userId") Long userId);

    /**
     * Busca un producto por código y por el ID del usuario que lo registró.
     * Este método es útil cuando se necesita buscar un producto específico por su código,
     * pero también asegurar que el producto pertenece a un usuario específico.
     *
     * @param productCode código del producto.
     * @param userId      ID del usuario.
     * @return un {@link Optional} que contiene el producto si se encuentra, o vacío si no.
     */
    Optional<Product> findByCodeAndRegistratedBy_Id(String productCode, Long userId);
}
