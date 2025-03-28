package com.imperial_net.inventioryApp.models;

public enum PaymentMethod {
    CASH("Efectivo"),
    CREDIT_CARD("Tarjeta de crédito"),
    DEBIT_CARD("Tarjeta de débito"),
    BANK_TRANSFER("Transferencia bancaria"),
    OTHER("Otro");

    private final String descripcion;

    PaymentMethod(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getDescripcion() {
        return descripcion;
    }
}
