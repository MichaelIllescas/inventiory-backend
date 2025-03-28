package com.imperial_net.inventioryApp.dto;
import lombok.Data;
import lombok.Getter;

import java.util.List;

@Data
public class UpdatePriceDTO {
    private List<Long> productIds; // Lista de IDs de productos a actualizar
    private String priceType;      // "purchasePrice" o "salePrice"
    private Double percentage;     // Porcentaje de modificación
    private String action;         // "increase" o "discount"

}