package com.sgdc.core.reportes.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PagoReportDTO {
    private Integer id;
    private String miembro;
    private String membresia;
    private BigDecimal montoOriginal;
    private BigDecimal montoReal;
    private LocalDateTime fechaCreacion;
    private LocalDate fechaInicio;
    private LocalDate fechaFin;
    private boolean cancelado;
}

