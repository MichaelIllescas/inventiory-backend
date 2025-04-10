package com.imperial_net.inventioryApp.sales.model;

/**
 * Enum que representa los posibles estados de una venta.
 * Los estados de venta pueden ser: Confirmada, Cancelada o Pendiente.
 */
public enum SaleStatus {

    /**
     * Estado de la venta: Confirmada.
     * Indica que la venta ha sido procesada y confirmada, y la transacción es válida.
     */
    CONFIRMED("Confirmada"),

    /**
     * Estado de la venta: Cancelada.
     * Indica que la venta ha sido cancelada y no es válida. No se procesarán más acciones sobre esta venta.
     */
    CANCELED("Cancelada"),

    /**
     * Estado de la venta: Pendiente.
     * Indica que la venta está en proceso y aún no ha sido confirmada ni cancelada. Se espera que se complete o se cancele.
     */
    PENDING("Pendiente");

    // Descripción del estado
    private final String descripcion;

    /**
     * Constructor del enum para asignar una descripción al estado de la venta.
     *
     * @param descripcion Descripción del estado de la venta.
     */
    SaleStatus(String descripcion) {
        this.descripcion = descripcion;
    }

    /**
     * Método para obtener la descripción del estado.
     *
     * @return Descripción del estado.
     */
    public String getDescripcion() {
        return descripcion;
    }
}
