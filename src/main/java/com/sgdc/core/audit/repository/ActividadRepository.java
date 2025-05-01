package com.sgdc.core.audit.repository;

import com.sgdc.core.audit.model.Actividad;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface ActividadRepository extends JpaRepository<Actividad, Integer>, JpaSpecificationExecutor<Actividad> {
}
