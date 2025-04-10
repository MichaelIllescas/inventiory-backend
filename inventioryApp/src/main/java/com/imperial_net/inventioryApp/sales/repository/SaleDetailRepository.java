package com.imperial_net.inventioryApp.sales.repository;

import com.imperial_net.inventioryApp.products.models.Product;
import com.imperial_net.inventioryApp.sales.model.Sale;
import com.imperial_net.inventioryApp.sales.model.SaleDetail;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.time.LocalDate;
import java.util.List;

/**
 * Repositorio JPA para la entidad {@link SaleDetail}.
 * Este repositorio proporciona métodos para interactuar con los detalles de ventas,
 * como la obtención de los productos vendidos y la consulta de los productos más vendidos.
 */
@Repository
public interface SaleDetailRepository extends JpaRepository<SaleDetail, Long> {

    /**
     * Busca todos los detalles de una venta específica.
     * Este método se utiliza para obtener todos los productos que forman parte de una venta dada.
     *
     * @param sale la venta de la que se desean obtener los detalles.
     * @return una lista de detalles de la venta.
     */
    List<SaleDetail> findBySale(Sale sale);

    /**
     * Obtiene todas las ventas en las que se vendió un producto específico.
     * Este método es útil para obtener todos los detalles de ventas asociadas a un producto determinado.
     *
     * @param product el producto del que se desean obtener los detalles de ventas.
     * @return una lista de detalles de ventas asociadas a un producto.
     */
    List<SaleDetail> findByProduct(Product product);

    /**
     * Devuelve los 10 productos más vendidos en un rango de fechas determinado.
     * Este método consulta la base de datos para obtener los productos más vendidos durante un periodo de tiempo específico,
     * ordenados por la cantidad vendida en orden descendente.
     * Se utiliza para analizar las ventas y determinar cuáles son los productos más populares.
     *
     * @param startDate la fecha de inicio del rango.
     * @param endDate   la fecha de finalización del rango.
     * @param userId    el ID del usuario para filtrar las ventas.
     * @return una lista de objetos con el nombre, código, cantidad vendida y subtotal de cada producto más vendido.
     */
    @Query("SELECT p.name, p.code, SUM(sd.quantity), SUM(sd.subtotal) " +
            "FROM SaleDetail sd " +
            "JOIN sd.product p " +
            "JOIN sd.sale s " +
            "WHERE s.saleDate BETWEEN :startDate AND :endDate AND s.user.id = :userId  " +
            "AND s.status = 'CONFIRMED' " +
            "GROUP BY p.name, p.code " +
            "ORDER BY SUM(sd.quantity) DESC " +
            "LIMIT 10") // Solo devuelve los 10 más vendidos
    List<Object[]> findTop10SellingProducts(@Param("startDate") LocalDate startDate,
                                            @Param("endDate") LocalDate endDate,
                                            @Param("userId") Long userId);

}
