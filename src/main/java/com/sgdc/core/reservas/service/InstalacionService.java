package com.sgdc.core.reservas.service;

import com.sgdc.core.reservas.domain.Instalacion;
import com.sgdc.core.reservas.domain.dto.InstalacionDTO;

import java.util.List;
import java.util.Optional;

public interface InstalacionService {

    List<Instalacion> findAll();

    Optional<Instalacion> findById(Integer id);

    List<Instalacion> search(String keyword);

    List<InstalacionDTO> findByMembresiaId(Integer membresiaId);

    void save(Instalacion instalacion);

}
