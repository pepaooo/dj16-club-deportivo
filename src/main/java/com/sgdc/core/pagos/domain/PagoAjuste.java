package com.sgdc.core.pagos.domain;

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
public class PagoAjuste {
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

    //@NotNull
    @ManyToOne
    @JoinColumn(name = "registrado_por", nullable = false)
    private Usuario registradoPor;

//    @AssertTrue(message = "El monto de ajuste debe ser distinto de 0")
//    public boolean isMontoAjusteValido() {
//        return montoAjuste != null && montoAjuste.compareTo(BigDecimal.ZERO) != 0;
//    }

}