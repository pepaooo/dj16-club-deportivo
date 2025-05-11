package com.sgdc.core.membresia.domain;

import com.sgdc.core.auditoria.jpa.AuditableBaseCreate;
import com.sgdc.core.miembro.domain.Miembro;
import jakarta.persistence.*;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

@Entity
@Table(name = "historial_membresia")
@Data
@EqualsAndHashCode(callSuper = true)
public class HistorialMembresia extends AuditableBaseCreate {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_historial")
    private Integer id;

    //@NotNull
    @ManyToOne
    @JoinColumn(name = "id_miembro", nullable = false)
    private Miembro miembro;

    //@NotNull
    @ManyToOne
    @JoinColumn(name = "id_membresia", nullable = false)
    private Membresia membresia;

    //@NotNull
    @Column(name = "fecha_cambio", nullable = false)
    private LocalDateTime fechaCambio;

    //@NotNull
    @Column(name = "descripcion", nullable = false, columnDefinition = "TEXT")
    private String descripcion;

}