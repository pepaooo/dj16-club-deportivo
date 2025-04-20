package com.sgdc.core.membresia.domain.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class MembresiaDetalleDTO {
    private BigDecimal tarifa;
    private int duracionDias;
    private String nombre;
}
