package com.sgdc.core.membresia.service;

import com.sgdc.core.membresia.domain.HistorialMembresia;

import java.util.List;

public interface HistorialMembresiaService {

    List<HistorialMembresia> findAll();

    HistorialMembresia findById(Integer id);

    List<HistorialMembresia> findByMiembroId(Integer id);

    List<HistorialMembresia> search(Integer idMiembro, String keyword);

    void save(HistorialMembresia membresia);

}
