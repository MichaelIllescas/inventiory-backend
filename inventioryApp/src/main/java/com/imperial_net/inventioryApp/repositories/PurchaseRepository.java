package com.imperial_net.inventioryApp.repositories;

import com.imperial_net.inventioryApp.models.Purchase;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PurchaseRepository extends JpaRepository<Purchase,Long> {
    List<Purchase> findByProductIdAndStateOrderByPurchaseDateAsc(Long productId, boolean b);
}
