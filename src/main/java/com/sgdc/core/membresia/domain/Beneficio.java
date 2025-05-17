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

import com.sgdc.core.auditoria.jpa.AuditableBase;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "beneficio")
@Data
@ToString
@EqualsAndHashCode(callSuper = true)
public class Beneficio extends AuditableBase {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_beneficio", nullable = false)
    private Integer id;

    @NotBlank(message = "El nombre del beneficio no puede estar vacío")
    @Size(max = 100, message = "El nombre del beneficio no puede tener más de 100 caracteres")
    @Column(name = "nombre", nullable = false, length = 100)
    private String nombre;

    @NotBlank(message = "La descripción del beneficio no puede estar vacío")
    @Column(name = "descripcion", nullable = false)
    private String descripcion;

    // Relación ManyToMany con Membresia (lado inverso)
    @ManyToMany(mappedBy = "beneficios")
    @EqualsAndHashCode.Exclude  // <-- excluir de equals/hashCode
    @ToString.Exclude
    private Set<Membresia> membresias = new HashSet<>();
}
