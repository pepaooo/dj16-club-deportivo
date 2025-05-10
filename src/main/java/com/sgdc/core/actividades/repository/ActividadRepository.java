package com.sgdc.core.actividades.repository;

import com.sgdc.core.actividades.model.Actividad;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface ActividadRepository extends JpaRepository<Actividad, Integer>, JpaSpecificationExecutor<Actividad> {
    // Listas de todas las actividades ordenadas de forma descendente por ID
    List<Actividad> findAllByOrderByIdDesc();
}
