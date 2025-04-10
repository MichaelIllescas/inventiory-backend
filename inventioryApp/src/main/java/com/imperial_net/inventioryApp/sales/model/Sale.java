package com.imperial_net.inventioryApp.sales.model;

import com.imperial_net.inventioryApp.clients.models.Client;
import com.imperial_net.inventioryApp.users.model.User;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * Entidad que representa una venta realizada en el sistema.
 * Cada instancia de esta clase corresponde a una venta individual.
 */
@Data
@Entity
public class Sale {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    /**
     * Fecha en la que se realizó la venta.
     * Esta fecha se asigna automáticamente antes de persistir en la base de datos.
     */
    @NotNull
    private LocalDate saleDate;

    /**
     * Relación con el usuario que registró la venta.
     * Un usuario puede registrar múltiples ventas.
     */
    @ManyToOne
    @NotNull
    private User user;

    /**
     * Relación con el cliente que realizó la compra (opcional).
     * Si no hay cliente, puede ser `null`.
     */
    @ManyToOne
    @JoinColumn(name = "customer_id", nullable = true)
    private Client customer;

    /**
     * Lista de productos vendidos en esta venta.
     * Cada producto en esta lista es representado por un `SaleDetail`.
     */
    @OneToMany(mappedBy = "sale", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<SaleDetail> saleDetails;

    /**
     * Monto total de la venta pagado por el cliente.
     * Este valor se calcula sumando los subtotales de cada producto vendido.
     */
    @NotNull
    @Positive
    private BigDecimal totalSale;

    /**
     * Costo total de los productos vendidos.
     * Este valor se calcula sumando los costos de cada producto vendido.
     */
    @NotNull
    @Positive
    private BigDecimal totalCost;

    /**
     * Ganancia bruta antes de aplicar cualquier descuento.
     * Se calcula como: `totalSale - totalCost`.
     */
    @NotNull
    private BigDecimal grossProfit;

    /**
     * Descuento total aplicado a la venta.
     * El valor por defecto es 0 (sin descuento).
     */
    @NotNull
    private BigDecimal discountApplied = BigDecimal.ZERO;

    /**
     * Porcentaje adicional aplicado a la venta, como cargos extra (por ejemplo, tasas adicionales).
     * El valor por defecto es 0.
     */
    @NotNull
    private BigDecimal extra_charge_percentage = BigDecimal.ZERO;

    /**
     * Ganancia neta después de aplicar el descuento.
     * Se calcula como: `grossProfit - discountApplied`.
     */
    @NotNull
    private BigDecimal netProfit;

    /**
     * Método de pago utilizado en la venta (Efectivo, Tarjeta de Crédito, Transferencia, etc.).
     * El valor se guarda como un `Enum` para los diferentes tipos de pago.
     */
    @Enumerated(EnumType.STRING)
    @NotNull
    private PaymentMethod paymentMethod;

    /**
     * Estado de la venta, que puede ser `CONFIRMED` (Confirmada), `CANCELED` (Cancelada), o `PENDING` (Pendiente).
     * Representa el estado actual de la venta.
     */
    @Enumerated(EnumType.STRING)
    @NotNull
    private SaleStatus status;

    /**
     * Método que asigna automáticamente la fecha y hora actual antes de persistir la venta.
     * Se ejecuta solo una vez, al crear la entidad en la base de datos.
     */
    @PrePersist
    protected void onCreate() {
        this.saleDate = LocalDate.now(); // Asigna la fecha actual de la venta
    }
}
