package com.sgdc.core.reservas.domain;

import com.sgdc.core.miembro.domain.Miembro;
import com.sgdc.core.usuarios.domain.Usuario;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

import java.time.LocalDateTime;

@Entity
@Table(name = "reserva")
@Data
public class Reserva {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_reserva")
    private Integer id;

    //@NotNull
    @ManyToOne
    @JoinColumn(name = "id_instalacion", nullable = false)
    private Instalacion instalacion;

    //@NotNull
    @ManyToOne
    @JoinColumn(name = "id_miembro", nullable = false)
    private Miembro miembro;

    @NotNull
    @Column(name = "fecha_hora_inicio", nullable = false)
    private LocalDateTime fechaHoraInicio;

    @NotNull
    @Column(name = "fecha_hora_fin", nullable = false)
    private LocalDateTime fechaHoraFin;

    //@NotNull
    @Pattern(regexp = "Pendiente|Confirmada|Cancelada")
    @Column(name = "estado_reserva", nullable = false, length = 20)
    private String estadoReserva;

    //@NotNull
    @ManyToOne
    @JoinColumn(name = "registrado_por", nullable = false)
    private Usuario registradoPor;

//    @AssertTrue(message = "La fecha fin debe ser posterior a la de inicio")
//    public boolean isFechasCronologicas() {
//        if (fechaHoraInicio == null || fechaHoraFin == null) return true;
//        return fechaHoraInicio.isBefore(fechaHoraFin) && !fechaHoraInicio.isEqual(fechaHoraFin);
//    }

}
