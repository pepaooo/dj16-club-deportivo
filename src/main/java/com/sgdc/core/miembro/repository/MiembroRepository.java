package com.sgdc.core.miembro.repository;

import com.sgdc.core.miembro.domain.Miembro;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface MiembroRepository extends JpaRepository<Miembro, Integer>, JpaSpecificationExecutor<Miembro> {
    // Lista todos los miembros en orden descendente por ID
    List<Miembro> findAllByOrderByIdDesc();

    @Query("SELECT m FROM Miembro m WHERE " +
            "(:idMembresia IS NULL OR m.membresia.id = :idMembresia) AND " +
            "(:fechaInicio IS NULL OR m.fechaInscripcion >= :fechaInicio) AND " +
            "(:fechaFin IS NULL OR m.fechaInscripcion <= :fechaFin) " +
            "ORDER BY m.id DESC")
    List<Miembro> findByFilters(@Param("idMembresia") Integer idMembresia,
                                @Param("fechaInicio") LocalDateTime fechaInicio,
                                @Param("fechaFin") LocalDateTime fechaFin);

    @Query("""
              SELECT m FROM Miembro m
                JOIN PagoMembresia p ON p.miembro.id = m.id
              WHERE p.fechaInicio = (
                SELECT MAX(p2.fechaInicio) FROM PagoMembresia p2
                 WHERE p2.miembro.id = m.id
                   AND p2.fechaInicio <= CURRENT_TIMESTAMP
              )
                AND p.cancelado = false
                AND p.fechaInicio <= CURRENT_TIMESTAMP
                AND p.fechaFin    >= CURRENT_TIMESTAMP
                AND ( :q IS NULL 
                      OR LOWER(m.nombre)  LIKE LOWER(CONCAT('%',:q,'%'))
                      OR LOWER(m.apellidoPaterno) LIKE LOWER(CONCAT('%',:q,'%'))
                      OR LOWER(m.apellidoMaterno) LIKE LOWER(CONCAT('%',:q,'%'))
                      OR LOWER(m.correoElectronico) LIKE LOWER(CONCAT('%',:q,'%')) )
            """)
    List<Miembro> searchActiveMembers(@Param("q") String keyword);
}
