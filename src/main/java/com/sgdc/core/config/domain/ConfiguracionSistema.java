package com.sgdc.core.config.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.ToString;

@Entity
@Table(name = "configuracion_sistema")
@Data
@ToString
public class ConfiguracionSistema {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_config")
    private Integer idConfig;

    @NotBlank(message = "El parámetro no puede estar vacío")
    @Size(max = 50)
    @Column(name = "parametro", unique = true, nullable = false)
    private String parametro;

    @NotBlank(message = "El valor no puede estar vacío")
    @Size(max = 100)
    @Column(name = "valor", nullable = false)
    private String valor;

    @Column(name = "descripcion", columnDefinition = "TEXT")
    private String descripcion;
}

