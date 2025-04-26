package com.sgdc.core.usuarios.domain.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UsuarioDTO {
    private Integer id;
    private String nombre;
}
