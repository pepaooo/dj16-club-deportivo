package com.sgdc.core.instalacion.repository;

import com.sgdc.core.instalacion.domain.Instalacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface InstalacionRepository extends JpaRepository<Instalacion, Integer>, JpaSpecificationExecutor<Instalacion> {
}
