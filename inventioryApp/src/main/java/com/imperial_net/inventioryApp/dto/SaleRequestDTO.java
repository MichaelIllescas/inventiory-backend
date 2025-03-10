package com.imperial_net.inventioryApp.dto;

import com.imperial_net.inventioryApp.dto.ProductQuantityDTO;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.util.List;

@Data
public class SaleRequestDTO {

    /**
     * Lista de productos vendidos con su cantidad.
     */
    @NotNull
    private List<ProductQuantityDTO> products;

    /**
     * Descuento total aplicado a la venta (puede ser opcional).
     */
    private BigDecimal discountApplied = BigDecimal.ZERO;

    /**
     * Método de pago (CASH, CREDIT_CARD, etc.).
     */
    @NotNull
    private String paymentMethod;

    /**
     * ID del cliente asociado (opcional).
     */
    private Long clientId;
}
