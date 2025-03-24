package com.imperial_net.inventioryApp.repositories;

import com.imperial_net.inventioryApp.models.Purchase;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PurchaseRepository extends JpaRepository<Purchase,Long> {
    List<Purchase> findByProductIdAndStateOrderByPurchaseDateAsc(Long productId, boolean b);
    @Query("SELECT p FROM Purchase p WHERE p.product.id = :productId ORDER BY p.purchaseDate ASC")
    List<Purchase> findByProductIdOrderByPurchaseDateAsc(@Param("productId") Long productId);
    @Query("SELECT p FROM Purchase p WHERE p.registredBy.id = :userId AND p.state = true AND p.remainingStock > 0")
    List<Purchase> findActivePurchasesWithStockByUser(@Param("userId") Long userId);

}
