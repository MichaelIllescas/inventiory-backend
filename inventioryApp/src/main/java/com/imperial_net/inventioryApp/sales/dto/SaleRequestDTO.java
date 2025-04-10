package com.imperial_net.inventioryApp.sales.dto;

import com.imperial_net.inventioryApp.products.dto.ProductQuantityDTO;
import lombok.Data;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.List;

@Data
public class SaleRequestDTO {

    /**
     * Lista de productos vendidos con su cantidad.
     * Esta lista contiene objetos de tipo ProductQuantityDTO que incluyen la ID del producto y la cantidad vendida.
     */
    @NotNull
    private List<ProductQuantityDTO> products;

    /**
     * Descuento total aplicado a la venta.
     * Este valor puede ser opcional y se inicializa en cero si no se aplica descuento.
     */
    private BigDecimal discountApplied = BigDecimal.ZERO;

    /**
     * Método de pago utilizado para la venta (por ejemplo, CASH, CREDIT_CARD, etc.).
     * Este campo es obligatorio.
     */
    @NotNull
    private String paymentMethod;

    /**
     * ID del cliente asociado con la venta (opcional).
     * Si no se asocia un cliente, este campo puede ser nulo.
     */
    private Long clientId;

    /**
     * Porcentaje de aumento que se aplicará a la venta.
     * Este valor puede ser opcional y se inicializa en cero si no se aplica aumento.
     */
    private BigDecimal extra_charge_percentage = BigDecimal.ZERO;
}
