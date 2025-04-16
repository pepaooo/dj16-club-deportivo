package com.sgdc.core.reservas.domain;

public enum EstadoReserva {
    PENDIENTE("Pendiente"),
    CONFIRMADA("Confirmada"),
    CANCELADA("Cancelada");

    private final String label;

    EstadoReserva(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}

