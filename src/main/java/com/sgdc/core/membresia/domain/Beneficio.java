package com.sgdc.core.membresia.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "beneficio")
@Data
@ToString
public class Beneficio {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_beneficio", nullable = false)
    private Integer id;

    @NotBlank(message = "El nombre del beneficio no puede estar vacío")
    @Size(max = 100, message = "El nombre del beneficio no puede tener más de 100 caracteres")
    @Column(name = "nombre", nullable = false, length = 100)
    private String nombre;

    @NotBlank(message = "La descripción del beneficio no puede estar vacío")
    @Column(name = "descripcion", nullable = false)
    private String descripcion;

    // Relación ManyToMany con Membresia (lado inverso)
    @ManyToMany(mappedBy = "beneficios")
    @EqualsAndHashCode.Exclude  // <-- excluir de equals/hashCode
    @ToString.Exclude
    private Set<Membresia> membresias = new HashSet<>();
}
