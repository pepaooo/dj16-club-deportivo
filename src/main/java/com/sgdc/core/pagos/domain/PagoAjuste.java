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

package com.sgdc.core.pagos.domain;

import com.sgdc.core.auditoria.jpa.AuditableBaseCreate;
import com.sgdc.core.usuarios.domain.Usuario;
import com.sgdc.core.validation.NotZero;
import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "pago_ajuste")
@Data
@ToString
@EqualsAndHashCode(callSuper = true)
public class PagoAjuste extends AuditableBaseCreate {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_pago_ajuste")
    private Integer id;

    //@NotNull
    @ManyToOne
    @JoinColumn(name = "id_pago", nullable = false)
    @ToString.Exclude
    private PagoMembresia pagoMembresia;

    @NotNull(message = "El monto no puede estar vacío")
    @NotZero(message = "El monto de ajuste no puede ser cero")
    @Column(name = "monto_ajuste", nullable = false, precision = 10, scale = 2)
    private BigDecimal montoAjuste;

    //@NotNull
    @Column(name = "fecha_ajuste", nullable = false)
    private LocalDateTime fechaAjuste;

    @NotNull
    @Column(name = "descripcion", nullable = false, columnDefinition = "TEXT")
    private String descripcion;

//    @AssertTrue(message = "El monto de ajuste debe ser distinto de 0")
//    public boolean isMontoAjusteValido() {
//        return montoAjuste != null && montoAjuste.compareTo(BigDecimal.ZERO) != 0;
//    }

}