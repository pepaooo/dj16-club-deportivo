package com.sgdc.core.miembro.repository;

import com.sgdc.core.miembro.domain.Miembro;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface MiembroRepository extends JpaRepository<Miembro, Integer>, JpaSpecificationExecutor<Miembro> {
    // Lista todos los miembros en orden descendente por ID
    List<Miembro> findAllByOrderByIdDesc();
}
