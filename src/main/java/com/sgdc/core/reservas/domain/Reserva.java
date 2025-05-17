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

package com.sgdc.core.reservas.domain;

import com.sgdc.core.auditoria.jpa.AuditableBase;
import com.sgdc.core.miembro.domain.Miembro;
import com.sgdc.core.usuarios.domain.Usuario;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

@Entity
@Table(name = "reserva")
@Data
@EqualsAndHashCode(callSuper = true)
public class Reserva extends AuditableBase {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_reserva")
    private Integer id;

    //@NotNull
    @ManyToOne
    @JoinColumn(name = "id_instalacion", nullable = false)
    private Instalacion instalacion;

    //@NotNull
    @ManyToOne
    @JoinColumn(name = "id_miembro", nullable = false)
    private Miembro miembro;

    @NotNull
    @Column(name = "fecha_hora_inicio", nullable = false)
    private LocalDateTime fechaHoraInicio;

    @NotNull
    @Column(name = "fecha_hora_fin", nullable = false)
    private LocalDateTime fechaHoraFin;

    //@NotNull
    @Pattern(regexp = "Pendiente|Confirmada|Cancelada")
    @Column(name = "estado_reserva", nullable = false, length = 20)
    private String estadoReserva;

    public boolean isCancelada() {
        return estadoReserva != null && estadoReserva.equals("Cancelada");
    }

//    @AssertTrue(message = "La fecha fin debe ser posterior a la de inicio")
//    public boolean isFechasCronologicas() {
//        if (fechaHoraInicio == null || fechaHoraFin == null) return true;
//        return fechaHoraInicio.isBefore(fechaHoraFin) && !fechaHoraInicio.isEqual(fechaHoraFin);
//    }

}
