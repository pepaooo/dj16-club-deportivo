package com.sgdc.core.reservas.repository;

import com.sgdc.core.reservas.domain.Instalacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface InstalacionRepository extends JpaRepository<Instalacion, Integer>, JpaSpecificationExecutor<Instalacion> {
}
