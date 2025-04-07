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

    @NotNull(message = "El id del miembro no puede estar vacío")
    private Integer idMiembro;

    @NotEmpty(message = "El tipo de operación no puede estar vacío")
    private String operacion;

    private Integer nuevoTipoMembresiaId;

    @NotNull(message = "El monto no puede estar vacío")
    private BigDecimal monto;

    @NotNull(message = "La fecha de pago no puede estar vacía")
    private LocalDate fechaInicio;

    private UsuarioDTO usuarioDTO;

}
