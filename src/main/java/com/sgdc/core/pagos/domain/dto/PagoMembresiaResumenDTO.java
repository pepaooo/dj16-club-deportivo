package com.sgdc.core.pagos.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@AllArgsConstructor
public class PagoMembresiaResumenDTO {

    private Integer id;
    private Integer idMiembro;
    private String nombreMiembro;
    private String apPaternoMiembro;
    private String apMaternoMiembro;
    private String membresiaActual;
    private BigDecimal monto;
    private LocalDate fechaInicio;
    private LocalDate fechaFin;
    private String estatusMembresia;

}
