package com.sgdc.core.auditoria.service;


import com.sgdc.core.auditoria.model.Actividad;

import java.util.List;

public interface ActividadService {

    List<Actividad> findAll();

    Actividad findById(Integer id);

    List<Actividad> search(String keyword);

}
