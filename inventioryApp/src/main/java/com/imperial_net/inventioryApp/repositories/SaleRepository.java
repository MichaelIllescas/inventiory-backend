package com.imperial_net.inventioryApp.repositories;

import com.imperial_net.inventioryApp.models.Sale;
import com.imperial_net.inventioryApp.models.SaleStatus;
import com.imperial_net.inventioryApp.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface SaleRepository extends JpaRepository<Sale, Long> {

    /**
     * Busca todas las ventas realizadas por un usuario en particular.
     */
    List<Sale> findByUser(User user);

    /**
     * Busca todas las ventas dentro de un rango de fechas.
     */
    List<Sale> findBySaleDateBetween(LocalDateTime startDate, LocalDateTime endDate);

    /**
     * Obtiene todas las ventas según su estado (Confirmada, Cancelada, Pendiente).
     */
    List<Sale> findByStatus(SaleStatus status);
}
