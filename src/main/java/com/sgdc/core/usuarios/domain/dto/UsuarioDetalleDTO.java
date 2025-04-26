package com.sgdc.core.usuarios.domain.dto;

import com.sgdc.core.usuarios.domain.OnCreate;
import com.sgdc.core.usuarios.domain.OnUpdate;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class UsuarioDetalleDTO {
    private Integer id;

    @NotBlank(message = "El nombre de usuario es obligatorio")
    private String nombre;

    @NotBlank(message = "La contraseña es obligatoria", groups = OnCreate.class)
    @Size(min = 6, max = 16,
            message = "La contraseña debe tener entre {min} y {max} caracteres", groups = {OnCreate.class, OnUpdate.class})
    @Pattern(
            regexp = "^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d).+$",
            message = "La contraseña debe tener al menos una mayúscula, una minúscula y un dígito", groups = {OnCreate.class, OnUpdate.class}
    )
    private String contrasena;

    private String estatus;
    private LocalDateTime fechaCreacion;
    private LocalDateTime ultimoAcceso;

    private Integer idMiembro;
    private String miembro;
    private String correoElectronico;

    // Aquí almacenamos los IDs seleccionados
    @EqualsAndHashCode.Exclude  // <-- excluir de equals/hashCode
    @ToString.Exclude
    private Set<Integer> rolesIds = new HashSet<>();

    /**
     * Para detalle: lista de objetos con nombre+descripción
     */
    @EqualsAndHashCode.Exclude  // <-- excluir de equals/hashCode
    @ToString.Exclude
    private Set<RolInfo> roles = new HashSet<>();
}
