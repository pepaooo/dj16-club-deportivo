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
