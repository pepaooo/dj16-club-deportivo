package com.sgdc.core.sistema.domain.dto;

import lombok.Data;

import java.time.LocalDate;
import java.time.temporal.ChronoUnit;

@Data
public class NotificacionSearchDTO {
    private final Integer id;
    private final String miembroNombre;
    private final String membresiaNombre;
    private final LocalDate fechaVencimiento;
    private final String estatus;

    /**
     * Obtiene el número de días restantes hasta la fecha de vencimiento.
     * @return
     */
    public long getDiasRestantes() {
        return ChronoUnit.DAYS.between(LocalDate.now(), fechaVencimiento);
    }
}
