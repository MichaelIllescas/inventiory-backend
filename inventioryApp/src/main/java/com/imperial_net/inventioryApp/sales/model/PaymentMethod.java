package com.imperial_net.inventioryApp.sales.model;

public enum PaymentMethod {

    /**
     * Método de pago en efectivo.
     * Representa el pago realizado con dinero en efectivo.
     */
    CASH("Efectivo"),

    /**
     * Método de pago con tarjeta de crédito.
     * Representa el pago realizado mediante una tarjeta de crédito.
     */
    CREDIT_CARD("Tarjeta de crédito"),

    /**
     * Método de pago con tarjeta de débito.
     * Representa el pago realizado mediante una tarjeta de débito.
     */
    DEBIT_CARD("Tarjeta de débito"),

    /**
     * Método de pago mediante transferencia bancaria.
     * Representa el pago realizado mediante una transferencia de fondos entre bancos.
     */
    BANK_TRANSFER("Transferencia bancaria"),

    /**
     * Método de pago no especificado.
     * Representa un pago realizado por un método no especificado.
     */
    OTHER("Otro");

    /**
     * Descripción del método de pago.
     * Guarda la descripción en español del método de pago.
     */
    private final String descripcion;

    /**
     * Constructor del enum PaymentMethod.
     * Asigna la descripción al método de pago.
     *
     * @param descripcion La descripción del método de pago.
     */
    PaymentMethod(String descripcion) {
        this.descripcion = descripcion;
    }

    /**
     * Obtiene la descripción del método de pago.
     *
     * @return La descripción del método de pago.
     */
    public String getDescripcion() {
        return descripcion;
    }
}
