package com.sgdc.core.reservas.domain.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class InstalacionDTO {

    private Integer id;
    private String nombre;
    private String estado;

}
