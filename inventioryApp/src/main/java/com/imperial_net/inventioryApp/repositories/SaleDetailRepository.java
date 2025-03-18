package com.imperial_net.inventioryApp.repositories;

import com.imperial_net.inventioryApp.models.Product;
import com.imperial_net.inventioryApp.models.Sale;
import com.imperial_net.inventioryApp.models.SaleDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

@Repository
public interface SaleDetailRepository extends JpaRepository<SaleDetail, Long> {

    /**
     * Busca todos los detalles de una venta específica.
     */
    List<SaleDetail> findBySale(Sale sale);

    /**
     * Obtiene todas las ventas en las que se vendió un producto específico.
     */
    List<SaleDetail> findByProduct(Product product);

    @Query("SELECT p.name, p.code, SUM(sd.quantity), SUM(sd.subtotal) " +
            "FROM SaleDetail sd " +
            "JOIN sd.product p " +
            "JOIN sd.sale s " +
            "WHERE s.saleDate BETWEEN :startDate AND :endDate " +
            "AND s.status = 'CONFIRMED' " +
            "GROUP BY p.name, p.code " +
            "ORDER BY SUM(sd.quantity) DESC " +
            "LIMIT 10") // Solo devuelve los 10 más vendidos
    List<Object[]> findTop10SellingProducts(@Param("startDate") LocalDate startDate,
                                            @Param("endDate") LocalDate endDate);

}
