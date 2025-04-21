package com.sgdc.core.reservas.domain.dto;

import com.sgdc.core.usuarios.domain.UsuarioDTO;
import jakarta.persistence.Column;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ReservaDTO {

    private Integer id;

    @NotNull(message = "El id del miembro no puede estar vacío" )
    private Integer idMiembro;

    @NotNull(message = "El id de la instalación no puede estar vacío")
    private Integer idInstalacion;

    @NotNull
    private LocalDateTime fechaHoraInicio;

    @NotNull
    private LocalDateTime fechaHoraFin;

    @Pattern(regexp = "Pendiente|Confirmada|Cancelada")
    private String estadoReserva;

    private UsuarioDTO registradoPor;

}
