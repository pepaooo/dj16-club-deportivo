package com.sgdc.core.miembro.domain.dto;

import com.sgdc.core.membresia.domain.Membresia;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class MiembroDetalleDTO {
    private Integer id;
    private String nombre;
    private String apellidoPaterno;
    private String apellidoMaterno;
    private String correoElectronico;
//    private String membresiaActual; // TODO Revisar si podemos tener alguin query para tener la membresía activa
//    private BigDecimal tarifa;
}
