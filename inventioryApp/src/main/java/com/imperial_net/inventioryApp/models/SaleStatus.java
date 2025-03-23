package com.imperial_net.inventioryApp.models;

public enum SaleStatus {
    CONFIRMED("Confirmada"),
    CANCELED("Cancelada"),
    PENDING("Pendiente");

    private final String descripcion;

    SaleStatus(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getDescripcion() {
        return descripcion;
    }
}
