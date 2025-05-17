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

package com.sgdc.core.usuarios.domain;

import com.sgdc.core.auditoria.jpa.AuditableBase;
import com.sgdc.core.miembro.domain.Miembro;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDateTime;
import java.util.Set;

@Entity
@Table(name = "usuario")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class Usuario extends AuditableBase {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_usuario")
    private Integer id;

    @NotBlank(message = "El nombre de usuario no puede estar vacío")
    @Size(max = 50)
    @Column(name = "nombre_usuario", nullable = false, unique = true, length = 50)
    private String nombre;

    //@NotBlank(message = "La contraseña no puede estar vacía")
    @Size(max = 255)
    @Column(name = "contrasena", nullable = false, length = 255)
    @ToString.Exclude
    private String contrasena;

    @NotBlank(message = "El estatus no puede estar vacío")
    @Pattern(regexp = "Activo|Inactivo")
    @Column(name = "estatus", nullable = false, length = 20)
    private String estatus;

    @Column(name = "ultimo_acceso")
    private LocalDateTime ultimoAcceso;

    @Column(name = "intentos_fallidos", nullable = false)
    private Integer failedAttempt;

    @Column(name = "fecha_bloqueo")
    private LocalDateTime lockTime;

    @ManyToOne
    @JoinColumn(name = "id_miembro")
    private Miembro miembro;

    // Relación ManyToMany con Rol
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "usuario_rol",
            joinColumns = @JoinColumn(name = "id_usuario"),
            inverseJoinColumns = @JoinColumn(name = "id_rol"))
    @EqualsAndHashCode.Exclude  // <-- excluir de equals/hashCode
    @ToString.Exclude
    private Set<Rol> roles;

    public void incrementFailedAttempt() {
        this.failedAttempt++;
    }

    public void resetFailedAttempts() {
        this.failedAttempt = 0;
    }

    public void lock() {
        this.lockTime = LocalDateTime.now();
    }

    public boolean isLockTimeExpired(long lockDurationMinutes) {
        return lockTime != null
                && lockTime.plusMinutes(lockDurationMinutes).isBefore(LocalDateTime.now());
    }

}
