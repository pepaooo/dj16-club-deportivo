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

package com.sgdc.core.membresia.domain;

import com.sgdc.core.auditoria.jpa.AuditableBaseCreate;
import com.sgdc.core.miembro.domain.Miembro;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

@Entity
@Table(name = "historial_membresia")
@Data
@EqualsAndHashCode(callSuper = true)
public class HistorialMembresia extends AuditableBaseCreate {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_historial")
    private Integer id;

    //@NotNull
    @ManyToOne
    @JoinColumn(name = "id_miembro", nullable = false)
    private Miembro miembro;

    //@NotNull
    @ManyToOne
    @JoinColumn(name = "id_membresia", nullable = false)
    private Membresia membresia;

    //@NotNull
    @Column(name = "fecha_cambio", nullable = false)
    private LocalDateTime fechaCambio;

    //@NotNull
    @Column(name = "descripcion", nullable = false, columnDefinition = "TEXT")
    private String descripcion;

}