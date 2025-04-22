package com.sgdc.core.membresia.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class MembresiaDetalleApiDTO {
    private String nombre;
    private BigDecimal tarifa;
    private int duracionDias;
}
