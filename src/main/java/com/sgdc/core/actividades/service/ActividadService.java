package com.sgdc.core.actividades.service;


import com.sgdc.core.actividades.model.Actividad;

import java.util.List;

public interface ActividadService {

    List<Actividad> findAll();

    Actividad findById(Integer id);

    List<Actividad> search(String keyword);

}
