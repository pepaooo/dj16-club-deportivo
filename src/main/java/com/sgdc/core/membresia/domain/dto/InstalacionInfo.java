package com.sgdc.core.membresia.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class InstalacionInfo {
    private String nombre;
    private String descripcion;
    private String estado;
}
