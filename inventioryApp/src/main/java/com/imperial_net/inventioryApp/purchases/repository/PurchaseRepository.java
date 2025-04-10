package com.imperial_net.inventioryApp.purchases.repository;

import com.imperial_net.inventioryApp.purchases.model.Purchase;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Repositorio JPA para la entidad {@link Purchase}.
 * Proporciona métodos de acceso a datos para operaciones con compras.
 */
@Repository
public interface PurchaseRepository extends JpaRepository<Purchase, Long> {

    /**
     * Obtiene las compras activas (state = true) de un producto, ordenadas por fecha de compra ascendente.
     *
     * @param productId ID del producto.
     * @param b         estado (true para activas).
     * @return lista de compras ordenadas.
     */
    List<Purchase> findByProductIdAndStateOrderByPurchaseDateAsc(Long productId, boolean b);

    /**
     * Obtiene todas las compras de un producto, sin filtrar por estado, ordenadas por fecha ascendente.
     *
     * @param productId ID del producto.
     * @return lista de compras.
     */
    @Query("SELECT p FROM Purchase p WHERE p.product.id = :productId ORDER BY p.purchaseDate ASC")
    List<Purchase> findByProductIdOrderByPurchaseDateAsc(@Param("productId") Long productId);

    /**
     * Obtiene las compras activas de un usuario que aún tienen stock disponible.
     *
     * @param userId ID del usuario.
     * @return lista de compras activas con stock.
     */
    @Query("SELECT p FROM Purchase p WHERE p.registredBy.id = :userId AND p.state = true AND p.remainingStock > 0")
    List<Purchase> findActivePurchasesWithStockByUser(@Param("userId") Long userId);

    /**
     * Obtiene todas las compras registradas por un usuario.
     *
     * @param userId ID del usuario.
     * @return lista de compras.
     */
    List<Purchase> findAllByRegistredById(Long userId);

    /**
     * Cuenta la cantidad de compras registradas por un usuario.
     *
     * @param userId ID del usuario.
     * @return total de compras.
     */
    Long countByRegistredById(Long userId);
}
