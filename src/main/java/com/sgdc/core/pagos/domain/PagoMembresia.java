package com.sgdc.core.pagos.domain;

import com.sgdc.core.auditoria.jpa.AuditableBase;
import com.sgdc.core.membresia.domain.Membresia;
import com.sgdc.core.miembro.domain.Miembro;
import jakarta.persistence.*;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Entity
@Table(name = "pago_membresia", uniqueConstraints = {@UniqueConstraint(columnNames = {"id_miembro", "fecha_pago"})})
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ToString(exclude = {"ajustes"})
@EqualsAndHashCode(callSuper = true)
public class PagoMembresia extends AuditableBase {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_pago")
    private Integer id;

    //@NotNull
    @ManyToOne
    @JoinColumn(name = "id_miembro", nullable = false)
    private Miembro miembro;

    @NotNull(message = "El monto no puede estar vacío")
    @DecimalMin(value = "0.00", message = "El monto debe ser mayor o igual a 0.00")
    @Column(name = "monto", nullable = false, precision = 10, scale = 2)
    private BigDecimal monto;

    @NotNull(message = "La fecha de inicio no puede estar vacía")
    //@FutureOrPresent(message = "La fecha de inicio debe ser hoy o en el futuro")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @Column(name = "fecha_inicio", nullable = false)
    private LocalDate fechaInicio;

    //@NotNull
    @Column(name = "fecha_fin", nullable = false)
    private LocalDate fechaFin;

    @ManyToOne
    @JoinColumn(name = "id_membresia", nullable = false)
    private Membresia membresia;

    @Column(name = "cancelado")
    private boolean cancelado;

    @Column(name = "fecha_cancelacion")
    private LocalDateTime fechaCancelacion;

    @Column(name = "motivo_cancelacion", length = 255)
    private String motivoCancelacion;

    @OneToMany(mappedBy = "pagoMembresia", fetch = FetchType.LAZY)
    private List<PagoAjuste> ajustes;

    /**
     * Metodo para obtener el estatus de la membresía.
     * @return
     */
    public String getEstatus() {
        if (cancelado) return "Cancelado";
        if (this.fechaInicio != null && this.fechaFin != null) {
            LocalDate now = LocalDate.now();
            if (this.fechaInicio.isAfter(now)) {
                return "Pendiente";
            } else if (this.fechaFin.isBefore(now)) {
                return "Vencido";
            } else {
                return "Activo";
            }
        }
        return "Desconocido";
    }

}
