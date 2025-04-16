package com.sgdc.core.reservas.domain;

public enum EstadoInstalacion {
    DISPONIBLE("Disponible"),
    EN_MANTENIMIENTO("En Mantenimiento"),
    CERRADA("Cerrada");

    private final String label;

    EstadoInstalacion(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}

