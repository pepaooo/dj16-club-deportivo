package com.sgdc.core.usuarios.repository;

import com.sgdc.core.usuarios.domain.Rol;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface RolRepository extends JpaRepository<Rol, Integer>, JpaSpecificationExecutor<Rol> {
}
