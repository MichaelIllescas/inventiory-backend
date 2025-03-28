package com.imperial_net.inventioryApp.dto;


import lombok.Data;
import java.math.BigDecimal;


@Data
public class SaleDetailDTO {
    private Long id;
    private Long saleId;
    private Long productId;
    private String productCode;
    private String productName;
    private String productDescription;
    private String productCategory;
    private String productBrandName;
    private BigDecimal productQuantity;
    private BigDecimal productSalePrice;

    /**
     * Monto total pagado por este producto en la venta.
     * Calculado como: salePrice * quantity.
     */
    private BigDecimal subtotal;

    private BigDecimal costPrice;
}
