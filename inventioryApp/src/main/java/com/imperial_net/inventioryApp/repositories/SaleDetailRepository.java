package com.imperial_net.inventioryApp.repositories;

import com.imperial_net.inventioryApp.models.Product;
import com.imperial_net.inventioryApp.models.Sale;
import com.imperial_net.inventioryApp.models.SaleDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
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
}
