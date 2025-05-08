package com.sgdc.core.pagos.domain.dto;

import com.sgdc.core.usuarios.domain.dto.UsuarioDTO;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PagoMembresiaDTO {

    private Integer id;

    @NotNull(message = "El id del miembro no puede estar vacío" )
    private Integer idMiembro;

    private String miembro;

    @NotNull(message = "Debe seleccionar un tipo de membresía")
    private Integer membresiaId;

    private String membresia;

    @NotNull(message = "El monto no puede estar vacío")
    @DecimalMin(value = "0.00", message = "El monto debe ser mayor o igual a $0.00")
    private BigDecimal monto;

    @NotNull(message = "La fecha de pago no puede estar vacía")
    private LocalDate fechaInicio;

    private UsuarioDTO usuarioDTO;

}
