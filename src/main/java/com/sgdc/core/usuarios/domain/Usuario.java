package com.sgdc.core.usuarios.domain;

import com.sgdc.core.miembro.domain.Miembro;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDateTime;
import java.util.Set;

@Entity
@Table(name = "usuario")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Usuario {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_usuario")
    private Integer id;

    @NotBlank(message = "El nombre de usuario no puede estar vacío")
    @Size(max = 50)
    @Column(name = "nombre_usuario", nullable = false, unique = true, length = 50)
    private String nombre;

    //@NotBlank(message = "La contraseña no puede estar vacía")
    @Size(max = 255)
    @Column(name = "contrasena", nullable = false, length = 255)
    @ToString.Exclude
    private String contrasena;

    @NotBlank(message = "El estatus no puede estar vacío")
    @Pattern(regexp = "Activo|Inactivo")
    @Column(name = "estatus", nullable = false, length = 20)
    private String estatus;

    @NotNull
    @Column(name = "fecha_creacion", nullable = false)
    private LocalDateTime fechaCreacion;

    @Column(name = "ultimo_acceso")
    private LocalDateTime ultimoAcceso;

    @ManyToOne
    @JoinColumn(name = "id_miembro")
    private Miembro miembro;

    // Relación ManyToMany con Rol
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "usuario_rol",
            joinColumns = @JoinColumn(name = "id_usuario"),
            inverseJoinColumns = @JoinColumn(name = "id_rol"))
    @EqualsAndHashCode.Exclude  // <-- excluir de equals/hashCode
    @ToString.Exclude
    private Set<Rol> roles;

}
