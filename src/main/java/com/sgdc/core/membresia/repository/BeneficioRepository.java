package com.sgdc.core.membresia.repository;

import com.sgdc.core.membresia.domain.Beneficio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface BeneficioRepository extends JpaRepository<Beneficio, Integer>, JpaSpecificationExecutor<Beneficio> {
}
