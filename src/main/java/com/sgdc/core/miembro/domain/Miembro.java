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

package com.sgdc.core.miembro.domain;

import com.sgdc.core.auditoria.jpa.AuditableBase;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Entity
@Table(name = "miembro")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class Miembro extends AuditableBase {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_miembro", nullable = false)
    private Integer id;

    @NotBlank(message = "El nombre del miembro no puede estar vacío")
    @Size(max = 50, message = "El nombre del miembro no puede tener más de 50 caracteres")
    @Column(name = "nombre", nullable = false, length = 50)
    private String nombre;

    @NotBlank(message = "El apellido paterno del miembro no puede estar vacío")
    @Size(max = 50, message = "El apellido paterno del miembro no pueden tener más de 50 caracteres")
    @Column(name = "apellido_paterno", nullable = false, length = 50)
    private String apellidoPaterno;

    @NotBlank(message = "El apellido materno del miembro no puede estar vacío")
    @Size(max = 50, message = "El apellido materno del miembro no pueden tener más de 50 caracteres")
    @Column(name = "apellido_materno", nullable = false, length = 50)
    private String apellidoMaterno;

    @NotBlank(message = "La dirección del miembro no puede estar vacía")
    @Size(max = 255, message = "La dirección del miembro no puede tener más de 255 caracteres")
    @Column(name = "direccion", nullable = false)
    private String direccion;

    @NotBlank(message = "El teléfono del miembro no puede estar vacío")
    @Size(max = 20, message = "El teléfono del miembro no puede tener más de 20 caracteres")
    @Column(name = "telefono", nullable = false, length = 20)
    private String telefono;

    @NotBlank(message = "El correo electrónico del miembro no puede estar vacío")
    @Email(message = "El correo electrónico del miembro no es válido")
    @Size(max = 100)
    @Column(name = "correo_electronico", nullable = false, unique = true, length = 100)
    private String correoElectronico;

    @NotNull(message = "La fecha de nacimiento del miembro no puede estar vacía")
    @Past(message = "La fecha de nacimiento del miembro no es válida")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @Column(name = "fecha_nacimiento", nullable = false)
    private LocalDate fechaNacimiento;

    //@NotBlank(message = "El género del miembro no puede estar vacío")
    //@Pattern(regexp = "M|F|O")
    @NotNull(message = "El género del miembro no puede estar vacío")
    @Column(name = "genero", nullable = false, length = 1)
    @Enumerated(EnumType.STRING)
    private Genero genero;

}
