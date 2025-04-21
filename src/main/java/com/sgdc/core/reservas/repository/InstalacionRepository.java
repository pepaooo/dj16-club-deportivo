package com.sgdc.core.reservas.repository;

import com.sgdc.core.reservas.domain.Instalacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface InstalacionRepository extends JpaRepository<Instalacion, Integer>, JpaSpecificationExecutor<Instalacion> {

    @Query("""
              SELECT i 
                FROM Instalacion i 
                JOIN i.membresias m 
               WHERE m.id = :mid
            """)
    List<Instalacion> findByMembresiaId(@Param("mid") Integer mid);

}
