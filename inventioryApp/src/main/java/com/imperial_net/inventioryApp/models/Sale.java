package com.imperial_net.inventioryApp.models;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Data
@Entity
public class Sale {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Fecha y hora en la que se realizó la venta.
     * Se asigna automáticamente antes de persistir en la base de datos.
     */
    @NotNull
    private LocalDate saleDate;

    /**
     * Usuario que registró la venta.
     */
    @ManyToOne
    @NotNull
    private User user;

    /**
     * Cliente que realizó la compra (opcional).
     * Puede ser `null` si la venta no tiene un cliente asignado.
     */
    @ManyToOne
    @JoinColumn(name = "customer_id", nullable = true)
    private Client customer;

    /**
     * Lista de productos vendidos en esta venta.
     */
    @OneToMany(mappedBy = "sale", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<SaleDetail> saleDetails;

    /**
     * Monto total pagado por el cliente.
     * Se calcula como: Σ (precioVenta * cantidad) de cada producto.
     */
    @NotNull
    @Positive
    private BigDecimal totalSale;

    /**
     * Costo total de los productos vendidos.
     * Se calcula como: Σ (costoUnitario * cantidad) de cada producto.
     */
    @NotNull
    @Positive
    private BigDecimal totalCost;

    /**
     * Ganancia bruta antes de aplicar descuentos.
     * Se calcula como: totalSale - totalCost.
     */
    @NotNull
    private BigDecimal grossProfit;

    /**
     * Descuento total aplicado a la venta.
     */
    @NotNull
    private BigDecimal discountApplied = BigDecimal.ZERO;

    /**
     * Ganancia neta después de aplicar descuentos.
     * Se calcula como: grossProfit - discountApplied.
     */
    @NotNull
    private BigDecimal netProfit;

    /**
     * Método de pago utilizado (Efectivo, Tarjeta de Crédito, Transferencia, etc.).
     */
    @Enumerated(EnumType.STRING)
    @NotNull
    private PaymentMethod paymentMethod;

    /**
     * Estado de la venta (Confirmada, Cancelada, Pendiente).
     */
    @Enumerated(EnumType.STRING)
    @NotNull
    private SaleStatus status;

    /**
     * Antes de persistir la venta, se asigna automáticamente la fecha y hora actual.
     */
    @PrePersist
    protected void onCreate() {
        this.saleDate = LocalDate.now();
    }
}
