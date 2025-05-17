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

package com.sgdc.core.sistema.domain;

import com.sgdc.core.auditoria.jpa.AuditableBase;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

@Entity
@Table(name = "configuracion_sistema")
@Data
@ToString
@EqualsAndHashCode(callSuper = true)
public class ConfiguracionSistema extends AuditableBase {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_config")
    private Integer id;

    @NotBlank(message = "El parámetro no puede estar vacío")
    @Size(max = 50)
    @Column(name = "parametro", unique = true, nullable = false)
    private String parametro;

    @NotBlank(message = "El valor no puede estar vacío")
    @Size(max = 100)
    @Column(name = "valor", nullable = false)
    private String valor;

    @Column(name = "descripcion", columnDefinition = "TEXT")
    private String descripcion;
}

