/*
 * Copyright (C) 2025 the original author or authors.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.sgdc.core.reservas.domain.dto;

import com.sgdc.core.usuarios.domain.dto.UsuarioDTO;
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
public class ReservaDTO {

    private Integer id;

    @NotNull(message = "El id del miembro no puede estar vacío" )
    private Integer idMiembro;

    private String miembro;

    @NotNull(message = "El id de la instalación no puede estar vacío")
    private Integer idInstalacion;

    private String instalacion;

    @NotNull
    private LocalDateTime fechaHoraInicio;

    @NotNull
    private LocalDateTime fechaHoraFin;

    @Pattern(regexp = "Pendiente|Confirmada|Cancelada")
    private String estadoReserva;

    private UsuarioDTO registradoPor;

}
