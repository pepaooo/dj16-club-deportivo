package com.sgdc.core.miembro.domain;

public enum Genero {
    M("Masculino"),
    F("Femenino"),
    O("Otro");

    private final String label;

    Genero(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }
}