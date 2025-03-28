package com.imperial_net.inventioryApp.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import java.math.BigDecimal;

@Data
@AllArgsConstructor
public class TopCustomerResponse {
    private String customerName;    // Nombre del cliente
    private String documentNumber;  // Número de documento del cliente
    private BigDecimal totalSpent;  // Total gastado en compras dentro del período seleccionado
    private Long totalPurchases;    // Cantidad total de compras realizadas
}
