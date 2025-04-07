package com.sgdc.core.pagos.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class PagoMembresiaDTO {

    private Integer id;
    private String idMiembro;
    private String operacion;
    private Integer nuevoTipoMembresiaId;
    private BigDecimal monto;
    private LocalDate fechaInicio;

}
