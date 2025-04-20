package com.sgdc.core.pagos.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PagoMembresiaResumenDTO {

    private Integer id;
    private Integer idMiembro;
    private String nombreMiembro;
    private String apPaternoMiembro;
    private String apMaternoMiembro;
    private Integer idMembresiaActual;
    private String membresiaActual;
    private BigDecimal monto;
    private LocalDate fechaInicio;
    private LocalDate fechaFin;
    private String estatusMembresia;

}
