package com.sgdc.core.reservas.domain.dto;

import com.sgdc.core.usuarios.domain.UsuarioDTO;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class InstalacionDTO {

    private Integer id;
    private String nombre;
    private String estado;

}
