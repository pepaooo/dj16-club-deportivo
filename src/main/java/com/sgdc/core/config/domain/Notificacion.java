package com.sgdc.core.config.domain;

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
