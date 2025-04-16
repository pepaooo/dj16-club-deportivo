package com.sgdc.core.reservas.service;

import com.sgdc.core.reservas.domain.Instalacion;

import java.util.List;
import java.util.Optional;

public interface InstalacionService {

    List<Instalacion> findAll();

    Optional<Instalacion> findById(Integer id);

    List<Instalacion> search(String keyword);

    void save(Instalacion instalacion);

}
