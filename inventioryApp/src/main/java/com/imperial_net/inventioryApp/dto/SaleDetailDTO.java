package com.imperial_net.inventioryApp.dto;


import lombok.Data;
import java.math.BigDecimal;


@Data
public class SaleDetailDTO {
    private Long id;
    private Long saleId;
    private Long productId;
    private BigDecimal quantity;
    private BigDecimal salePrice;

    /**
     * Monto total pagado por este producto en la venta.
     * Calculado como: salePrice * quantity.
     */
    private BigDecimal subtotal;

    private BigDecimal costPrice;
}
