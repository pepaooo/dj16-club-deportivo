package com.sgdc.core.membresia.repository;

import com.sgdc.core.membresia.domain.Membresia;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface MembresiaRepository extends JpaRepository<Membresia, Integer>, JpaSpecificationExecutor<Membresia> {
    List<Membresia> findAllByOrderByIdDesc();
    List<Membresia> findByEstatus(String estatus);
}
