package com.imperial_net.inventioryApp.repositories;

import com.imperial_net.inventioryApp.dto.SaleResponseDTO;
import com.imperial_net.inventioryApp.models.Sale;
import com.imperial_net.inventioryApp.models.SaleStatus;
import com.imperial_net.inventioryApp.models.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
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
    List<Sale> findBySaleDateBetween(LocalDate startDate, LocalDate endDate);

    /**
     * Obtiene todas las ventas según su estado (Confirmada, Cancelada, Pendiente).
     */
    List<Sale> findByStatus(SaleStatus status);

    /**
     * Filtra las ventas por el ID del usuario que las creó.
     */
    List<Sale> findAllByUser_Id(Long userId);

    /**
     * Obtiene las ventas de un usuario en una fecha específica con estado confirmado.
     */
    @Query("SELECT s FROM Sale s WHERE s.saleDate = :date AND s.user.id = :userId AND s.status = com.imperial_net.inventioryApp.models.SaleStatus.CONFIRMED")
    List<Sale> findSalesByDate(@Param("date") LocalDate date, @Param("userId") Long userId);

    /**
     * Obtiene las ventas de un usuario en un rango de fechas con estado confirmado.
     */
    @Query("SELECT s FROM Sale s WHERE s.saleDate BETWEEN :startDate AND :endDate AND s.user.id = :userId AND s.status = com.imperial_net.inventioryApp.models.SaleStatus.CONFIRMED")
    List<Sale> findSalesBetweenDates(@Param("startDate") LocalDate startDate,
                                     @Param("endDate") LocalDate endDate,
                                     @Param("userId") Long userId);

    /**
     * Obtiene los clientes con mayores compras en un período de tiempo.
     */
    @Query("SELECT c.name, SUM(s.totalSale) FROM Sale s " +
            "JOIN s.customer c " +
            "WHERE s.saleDate BETWEEN :startDate AND :endDate  AND s.user.id = :userId " +
            "GROUP BY c.name " +
            "ORDER BY SUM(s.totalSale) DESC")
    List<Object[]> findTopCustomers(@Param("startDate") LocalDate startDate,
                                    @Param("endDate") LocalDate endDate,
                                    @Param("userId") Long userId
                                    );

    /**
     * Obtiene los 10 mejores clientes de un mes basado en el total gastado y cantidad de compras.
     */
    @Query("SELECT c.name, c.lastname, c.documentNumber, SUM(s.totalSale), COUNT(s.id) " +
            "FROM Sale s " +
            "JOIN s.customer c " +
            "WHERE s.saleDate BETWEEN :startDate AND :endDate AND s.user.id = :userId " +
            "AND s.status = com.imperial_net.inventioryApp.models.SaleStatus.CONFIRMED " +
            "GROUP BY c.name, c.lastname, c.documentNumber " +
            "ORDER BY SUM(s.totalSale) DESC")
    List<Object[]> findTopCustomersForMonth(@Param("startDate") LocalDate startDate,
                                            @Param("endDate") LocalDate endDate,
                                            @Param("userId") Long userId);

    List<Sale>findAllByCustomerId(Long id);
}
