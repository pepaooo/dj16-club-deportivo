package com.sgdc.core.miembro.domain.dto;

import com.sgdc.core.membresia.domain.Membresia;
import lombok.Data;

@Data
public class MiembroDTO {
    private Integer id;
    private String nombre;
    private String apellidoPaterno;
    private String apellidoMaterno;
    private String correoElectronico;
}
