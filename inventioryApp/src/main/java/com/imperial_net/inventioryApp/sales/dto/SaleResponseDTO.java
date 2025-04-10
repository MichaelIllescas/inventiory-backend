package com.imperial_net.inventioryApp.sales.dto;

import com.imperial_net.inventioryApp.clients.dto.ClientResponseDTO;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class SaleResponseDTO {

    /**
     * ID de la venta.
     * Identificador único de la transacción de venta.
     */
    private Long id;

    /**
     * Fecha de la venta.
     * Representa la fecha en que se realizó la venta.
     */
    private String saleDate;

    /**
     * Detalles de los productos vendidos.
     * Lista de detalles de cada producto asociado a esta venta.
     */
    private List<SaleDetailDTO> saleDetails;

    /**
     * Total de la venta.
     * Monto total de la venta (sin descuentos ni costos).
     */
    private BigDecimal totalSale;

    /**
     * Costo total de los productos vendidos.
     * Suma de los costos de todos los productos vendidos en esta venta.
     */
    private BigDecimal totalCost;

    /**
     * Ganancia bruta de la venta.
     * Calculada como totalSale - totalCost.
     */
    private BigDecimal grossProfit;

    /**
     * Descuento total aplicado a la venta.
     * Monto total de descuento que se ha aplicado a la venta.
     */
    private BigDecimal discountApplied;

    /**
     * Ganancia neta de la venta.
     * Calculada como grossProfit - cualquier otro gasto adicional o costos.
     */
    private BigDecimal netProfit;

    /**
     * Método de pago utilizado en la venta.
     * Puede ser "CASH", "CREDIT_CARD", "DEBIT_CARD", etc.
     */
    private String paymentMethod;

    /**
     * Estado de la venta.
     * Puede ser "PENDING", "COMPLETED", "CANCELLED", etc.
     */
    private String status;

    /**
     * Cliente asociado a la venta.
     * Si la venta está vinculada a un cliente, se devuelve un objeto con la información del cliente.
     */
    private ClientResponseDTO client;

    /**
     * Porcentaje de cargos adicionales aplicados a la venta.
     * Este cargo es adicional al precio de venta original.
     */
    private BigDecimal extra_charge_percentage = BigDecimal.ZERO;
}
