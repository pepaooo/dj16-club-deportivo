package com.sgdc.core.membresia.repository;

import com.sgdc.core.membresia.domain.Beneficio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface BeneficioRepository extends JpaRepository<Beneficio, Integer>, JpaSpecificationExecutor<Beneficio> {

    @Query("""
              SELECT b
                FROM Beneficio b
                JOIN b.membresias m
               WHERE m.id = :idMembresia
            """)
    List<Beneficio> findAllByMembresiaId(@Param("idMembresia") Integer idMembresia);
}
