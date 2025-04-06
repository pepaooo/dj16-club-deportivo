package com.sgdc.core.pagos.domain;

import com.sgdc.core.membresia.domain.Membresia;
import com.sgdc.core.miembro.domain.Miembro;
import com.sgdc.core.usuarios.domain.Usuario;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "pago_membresia", uniqueConstraints = {@UniqueConstraint(columnNames = {"id_miembro", "fecha_pago"})})
@Data
public class PagoMembresia {
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

    //@NotNull
    @Column(name = "fecha_pago", nullable = false)
    private LocalDateTime fechaPago;

    @NotNull(message = "La fecha de inicio no puede estar vacía")
    @FutureOrPresent(message = "La fecha de inicio debe ser hoy o en el futuro")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @Column(name = "fecha_inicio", nullable = false)
    private LocalDate fechaInicio;

    //@NotNull
    @Column(name = "fecha_fin", nullable = false)
    private LocalDate fechaFin;

    //@NotNull
    @ManyToOne
    @JoinColumn(name = "registrado_por", nullable = false)
    private Usuario registradoPor;

    @ManyToOne
    @JoinColumn(name = "id_membresia", nullable = false)
    private Membresia membresia;

    /**
     * Metodo para obtener el estatus de la membresía.
     * @return
     */
    public String getEstatus() {
        if (this.fechaInicio != null && this.fechaFin != null) {
            LocalDate now = LocalDate.now();
            return (this.fechaInicio.isBefore(now) && this.fechaFin.isAfter(now)) ? "Activo" : "Vencido";
        }
        return "Desconocido";
    }

}
