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

package com.sgdc.core.usuarios.domain.dto;

import com.sgdc.core.usuarios.domain.OnCreate;
import com.sgdc.core.usuarios.domain.OnUpdate;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class UsuarioDetalleDTO {
    private Integer id;

    @NotBlank(message = "El nombre de usuario es obligatorio")
    private String nombre;

    @NotBlank(message = "La contraseña es obligatoria", groups = OnCreate.class)
    @Size(min = 6, max = 16,
            message = "La contraseña debe tener entre {min} y {max} caracteres", groups = {OnCreate.class, OnUpdate.class})
    @Pattern(
            regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).+$",
            message = "La contraseña debe tener al menos una mayúscula, una minúscula y un dígito", groups = {OnCreate.class, OnUpdate.class}
    )
    private String contrasena;

    private String estatus;
    private LocalDateTime ultimoAcceso;

    private Integer idMiembro;
    private String miembro;
    private String correoElectronico;

    // Aquí almacenamos los IDs seleccionados
    @EqualsAndHashCode.Exclude  // <-- excluir de equals/hashCode
    @ToString.Exclude
    private Set<Integer> rolesIds = new HashSet<>();

    /**
     * Para detalle: lista de objetos con nombre+descripción
     */
    @EqualsAndHashCode.Exclude  // <-- excluir de equals/hashCode
    @ToString.Exclude
    private Set<RolInfo> roles = new HashSet<>();

    // Campos de auditoría
    private String creadoPor;
    private String modificadoPor;
    private LocalDateTime fechaCreacion;
    private LocalDateTime fechaModificacion;

}
