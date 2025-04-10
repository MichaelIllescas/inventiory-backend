package com.imperial_net.inventioryApp.suscriptions.model;

/**
 * Enum que representa los tipos de suscripciones disponibles en la aplicación.
 * Cada tipo de suscripción tiene un precio asociado.
 */
public enum Subscription {

    FREE(0),  // Suscripción gratuita, sin costo
    PRO(15000);  // Suscripción PRO, con un costo de 15000

    private final int price;  // Precio asociado a cada tipo de suscripción

    /**
     * Constructor del enum Subscription.
     * Asocia un precio a cada tipo de suscripción.
     *
     * @param price El precio de la suscripción.
     */
    Subscription(int price) {
        this.price = price;
    }

    /**
     * Obtiene el precio asociado a la suscripción.
     *
     * @return El precio de la suscripción.
     */
    public int getPrice() {
        return price;
    }
}
