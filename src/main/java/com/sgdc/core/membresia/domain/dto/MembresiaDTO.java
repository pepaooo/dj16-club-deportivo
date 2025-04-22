package com.sgdc.core.membresia.domain.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Data
@ToString
public class MembresiaDTO {

    private Integer id;

    @NotBlank(message = "El nombre de la membresía no puede estar vacío")
    private String nombre;

    @NotBlank(message = "La descripción de la membresía no puede estar vacía")
    private String descripcion;

    @NotNull(message = "El estatus de la membresía no puede estar vacío")
    @Min(value = 0, message = "La tarifa no puede ser negativa")
    private BigDecimal tarifa;

    @NotNull(message = "La duración en días no puede estar vacía")
    @Min(value = 0, message = "La duración en días no puede ser negativa")
    private Integer duracionDias;

    private String estatus;

    // Aquí almacenamos los IDs seleccionados
    @EqualsAndHashCode.Exclude  // <-- excluir de equals/hashCode
    @ToString.Exclude
    private Set<Integer> beneficiosIds = new HashSet<>();
    @EqualsAndHashCode.Exclude  // <-- excluir de equals/hashCode
    @ToString.Exclude
    private Set<Integer> instalacionesIds = new HashSet<>();

    /** Para detalle: lista de objetos con nombre+descripción */
    private List<BeneficioInfo> beneficios = new ArrayList<>();
    private List<InstalacionInfo> instalaciones = new ArrayList<>();

}

