package com.sgdc.core.audit.model;

import com.sgdc.core.usuarios.domain.Usuario;
import jakarta.persistence.*;
import lombok.Data;
import lombok.ToString;

import java.time.LocalDateTime;


@Entity
@Table(name = "actividad")
@Data
@ToString
public class Actividad {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_actividad")
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "id_usuario", nullable = false)
    private Usuario usuario;

    @Column(name = "tipo_accion", length = 50, nullable = false)
    private String tipoAccion;

    @Column(name = "tabla_afectada", length = 50)
    private String tablaAfectada;

    @Column(name = "id_entidad_afectada")
    private Integer entidadId;

    @Column(name = "fecha_hora", nullable = false, updatable = false,
            columnDefinition = "DATETIME DEFAULT CURRENT_TIMESTAMP")
    private LocalDateTime fechaHora;

    @Column(name = "descripcion", columnDefinition = "TEXT")
    private String descripcion;

    @Column(name = "ip_origen", length = 45)
    private String ipOrigen;

    @Column(name = "user_agent")
    private String userAgent;

}

