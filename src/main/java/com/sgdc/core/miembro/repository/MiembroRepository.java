package com.sgdc.core.miembro.repository;

import com.sgdc.core.miembro.domain.Miembro;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface MiembroRepository extends JpaRepository<Miembro, Integer>, JpaSpecificationExecutor<Miembro> {
}
