package com.sgdc.core.pagos.domain.dto;

import com.sgdc.core.usuarios.domain.UsuarioDTO;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class PagoMembresiaDTO {

    private Integer id;
    @NotEmpty(message = "El id del miembro no puede estar vacío")
    private Integer idMiembro;
    private String operacion;
    private Integer nuevoTipoMembresiaId;
    private BigDecimal monto;
    private LocalDate fechaInicio;
    private UsuarioDTO usuarioDTO;

}
