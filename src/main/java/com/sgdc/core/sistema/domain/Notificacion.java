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
import com.sgdc.core.pagos.domain.PagoMembresia;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@Entity
@Table(name = "notificacion")
@Data
@ToString
@EqualsAndHashCode(callSuper = true)
public class Notificacion extends AuditableBase {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_notificacion")
    private Integer id;

    @NotNull
    @ManyToOne
    @JoinColumn(name = "id_pago", nullable = false)
    @ToString.Exclude
    private PagoMembresia pagoMembresia;

    @NotNull
    @Column(name = "fecha_vencimiento", nullable = false)
    private LocalDate fechaVencimiento;

    @NotNull
    @Pattern(regexp = "Pendiente|Enviada|Error")
    @Column(name = "estado", nullable = false, length = 20)
    private String estado;

    @Column(name = "fecha_envio")
    private LocalDateTime fechaEnvio;

    @Column(name = "mensaje", columnDefinition = "TEXT")
    private String mensaje;

    /**
     * Obtiene el número de días restantes hasta la fecha de vencimiento.
     * @return
     */
    public long getDiasRestantes() {
        return ChronoUnit.DAYS.between(LocalDate.now(), fechaVencimiento);
    }

}
