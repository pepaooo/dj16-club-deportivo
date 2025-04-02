package com.sgdc.core.miembro.domain;

import com.sgdc.core.membresia.domain.Membresia;
import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Data;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Entity
@Table(name = "miembro")
@Data
public class Miembro {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id_miembro", nullable = false)
    private Integer id;

    @NotBlank(message = "El nombre del miembro no puede estar vacío")
    @Size(max = 50, message = "El nombre del miembro no puede tener más de 50 caracteres")
    @Column(name = "nombre", nullable = false, length = 50)
    private String nombre;

    @NotBlank(message = "El apoellido paterno del miembro no puede estar vacío")
    @Size(max = 50, message = "El apellido paterno del miembro no pueden tener más de 50 caracteres")
    @Column(name = "apellido_paterno", nullable = false, length = 50)
    private String apellidoPaterno;

    @NotBlank(message = "El apoellido materno del miembro no puede estar vacío")
    @Size(max = 50, message = "El apellido materno del miembro no pueden tener más de 50 caracteres")
    @Column(name = "apellido_materno", nullable = false, length = 50)
    private String apellidoMaterno;

    @NotBlank(message = "La dirección del miembro no puede estar vacía")
    @Size(max = 255, message = "La dirección del miembro no puede tener más de 255 caracteres")
    @Column(name = "direccion", nullable = false)
    private String direccion;

    @NotBlank(message = "El teléfono del miembro no puede estar vacío")
    @Size(max = 20, message = "El teléfono del miembro no puede tener más de 20 caracteres")
    @Column(name = "telefono", nullable = false, length = 20)
    private String telefono;

    @NotBlank(message = "El correo electrónico del miembro no puede estar vacío")
    @Email(message = "El correo electrónico del miembro no es válido")
    @Size(max = 100)
    @Column(name = "correo_electronico", nullable = false, unique = true, length = 100)
    private String correoElectronico;

    @NotNull(message = "La fecha de nacimiento del miembro no puede estar vacía")
    @Past(message = "La fecha de nacimiento del miembro no es válida")
    @DateTimeFormat(pattern = "yyyy-MM-dd")
    @Column(name = "fecha_nacimiento", nullable = false)
    private LocalDate fechaNacimiento;

    //@NotBlank(message = "El género del miembro no puede estar vacío")
    //@Pattern(regexp = "M|F|O")
    @NotNull(message = "El género del miembro no puede estar vacío")
    @Column(name = "genero", nullable = false, length = 1)
    @Enumerated(EnumType.STRING)
    private Genero genero;

    //@NotBlank(message = "La fecha de inscripción del miembro no puede estar vacía")
    @Column(name = "fecha_inscripcion", nullable = false)
    private LocalDateTime fechaInscripcion;

    //@NotNull
    @ManyToOne
    @JoinColumn(name = "id_membresia", nullable = false)
    private Membresia membresia;
}
