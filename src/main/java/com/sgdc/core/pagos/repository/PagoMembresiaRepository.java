package com.sgdc.core.pagos.repository;

import com.sgdc.core.pagos.domain.PagoMembresia;
import com.sgdc.core.pagos.domain.dto.PagoMembresiaResumenDTO;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface PagoMembresiaRepository extends JpaRepository<PagoMembresia, Integer>, JpaSpecificationExecutor<PagoMembresia> {

    List<PagoMembresia> findByMiembro_IdOrderByIdDesc(Integer idMiembro);

    boolean existsByMiembro_Id(Integer idMiembro);

    @Query("""
              SELECT p
                FROM PagoMembresia p
               WHERE p.miembro.id   = :miembroId
                 AND p.cancelado = false
                 AND p.fechaInicio <= CURRENT_DATE
                 AND p.fechaFin    >= CURRENT_DATE
            """)
    Optional<PagoMembresia> findActiveByMiembro(@Param("miembroId") Integer miembroId);

    @Query("""
              SELECT p 
                FROM PagoMembresia p
               WHERE p.miembro.id = :miembroId
                 AND p.cancelado = false
                 AND p.fechaFin   >= :inicioNuevo
                 AND p.fechaInicio <= :finNuevo
            """)
    List<PagoMembresia> findOverlapping(
            @Param("miembroId") Integer miembroId,
            @Param("inicioNuevo") LocalDate inicioNuevo,
            @Param("finNuevo") LocalDate finNuevo
    );

    @Query("""
              SELECT p
                FROM PagoMembresia p
               WHERE p.miembro.id    = :miembroId
                 AND p.cancelado = false
                 AND p.fechaInicio  > :finNuevo
               ORDER BY p.fechaInicio ASC
            """)
    List<PagoMembresia> findFutureByMiembroOrderByFechaInicio(
            @Param("miembroId") Integer miembroId,
            @Param("finNuevo") LocalDate finNuevo);

    @Query("""
              SELECT new com.sgdc.core.pagos.domain.dto.PagoMembresiaResumenDTO(
                p.id,
                p.miembro.id,
                p.miembro.nombre,
                p.miembro.apellidoPaterno,
                p.miembro.apellidoMaterno,
                p.membresia.id,
                p.membresia.nombre,
                p.monto,
                p.fechaInicio,
                p.fechaFin,
                CASE
                  WHEN p.cancelado = true THEN 'Cancelado'
                  WHEN p.fechaInicio > CURRENT_DATE THEN 'Pendiente'
                  WHEN p.fechaInicio <= CURRENT_DATE AND p.fechaFin >= CURRENT_DATE THEN 'Activo'
                  ELSE 'Vencido'
                END
              )
              FROM PagoMembresia p
              WHERE p.miembro.id = :idMiembro
                AND p.cancelado = false
              ORDER BY p.fechaInicio DESC
            """)
    List<PagoMembresiaResumenDTO> findAllResumenPagosByMiembro(@Param("idMiembro") Integer idMiembro,
                                                               Pageable pageable);

    @Query("SELECT new com.sgdc.core.pagos.domain.dto.PagoMembresiaResumenDTO(" +
            "p.id, " +
            "m.id, " +
            "m.nombre, " +
            "m.apellidoPaterno, " +
            "m.apellidoMaterno, " +
            "tm.id, " +
            "tm.nombre, " +
            "p.monto, " +
            "p.fechaInicio, " +
            "p.fechaFin, " +
            "CASE " +
            "   WHEN p.cancelado = true THEN 'Cancelado' " +
            "   WHEN p.fechaInicio > CURRENT_DATE THEN 'Pendiente' " +
            "   WHEN p.fechaInicio <= CURRENT_DATE AND p.fechaFin >= CURRENT_DATE THEN 'Activo' " +
            "   ELSE 'Vencido' END" +
            ") " +
            "FROM PagoMembresia p " +
            "JOIN p.miembro m " +
            "JOIN p.membresia tm " +
            "WHERE m.id = :idMiembro " +
            "AND p.cancelado = false " +
            "AND (p.fechaInicio > CURRENT_DATE OR " +
            "(p.fechaInicio <= CURRENT_DATE AND p.fechaFin >= CURRENT_DATE)) " +
            "ORDER BY p.id DESC")
    List<PagoMembresiaResumenDTO> findResumenPagosByMiembro(@Param("idMiembro") Integer idMiembro);

    @Query("SELECT new com.sgdc.core.pagos.domain.dto.PagoMembresiaResumenDTO(" +
            "p.id, m.id, m.nombre, m.apellidoPaterno, m.apellidoMaterno, tm.id, tm.nombre, p.monto , p.fechaInicio, p.fechaFin, " +
            "CASE " +
            "   WHEN p.cancelado = true THEN 'Cancelado' " +
            "   WHEN p.fechaInicio > CURRENT_DATE THEN 'Pendiente' " +
            "   WHEN p.fechaInicio <= CURRENT_DATE AND p.fechaFin >= CURRENT_DATE THEN 'Activo' " +
            "   ELSE 'Vencido' END" +
            ") " +
            "FROM PagoMembresia p " +
            "LEFT JOIN p.miembro m " +
            "LEFT JOIN p.membresia tm " +
            "WHERE m.id = :idMiembro " +
            "AND p.cancelado = false " +
            "AND (p.fechaInicio > CURRENT_DATE OR " +
            "(p.fechaInicio <= CURRENT_DATE AND p.fechaFin >= CURRENT_DATE)) " +
            "AND (LOWER(m.nombre) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
            "   OR LOWER(m.apellidoPaterno) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
            "   OR LOWER(m.apellidoMaterno) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
            "   OR LOWER(tm.nombre) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
            "   OR LOWER(FUNCTION('DATE_FORMAT', p.fechaInicio, '%Y-%m-%d')) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
            "   OR LOWER(FUNCTION('DATE_FORMAT', p.fechaFin, '%Y-%m-%d')) LIKE LOWER(CONCAT('%', :keyword, '%'))" +
            ") ORDER BY p.id DESC")
    List<PagoMembresiaResumenDTO> searchResumenPagosByMiembro(@Param("idMiembro") Integer idMiembro, @Param("keyword") String keyword);

    @Query("SELECT new com.sgdc.core.pagos.domain.dto.PagoMembresiaResumenDTO(" +
            "p.id, " +
            "m.id, " +
            "m.nombre, " +
            "m.apellidoPaterno, " +
            "m.apellidoMaterno, " +
            "tm.id, " +
            "tm.nombre, " +
            "p.monto, " +
            "p.fechaInicio, " +
            "p.fechaFin, " +
            "CASE " +
            "   WHEN p.cancelado = true THEN 'Cancelado' " +
            "   WHEN p.fechaInicio > CURRENT_DATE THEN 'Pendiente' " +
            "   WHEN p.fechaInicio <= CURRENT_DATE AND p.fechaFin >= CURRENT_DATE THEN 'Activo' " +
            "   ELSE 'Vencido' END" +
            ") " +
            "FROM PagoMembresia p " +
            "JOIN p.miembro m " +
            "JOIN p.membresia tm " +
            "WHERE " +
            "p.cancelado = false " +
            "AND (p.fechaInicio > CURRENT_DATE OR " +
            "(p.fechaInicio <= CURRENT_DATE AND p.fechaFin >= CURRENT_DATE)) " +
            "ORDER BY p.id DESC")
    List<PagoMembresiaResumenDTO> findResumenPagos();

    @Query("SELECT new com.sgdc.core.pagos.domain.dto.PagoMembresiaResumenDTO(" +
            "p.id, m.id, m.nombre, m.apellidoPaterno, m.apellidoMaterno, tm.id, tm.nombre, p.monto , p.fechaInicio, p.fechaFin, " +
            "CASE " +
            "   WHEN p.cancelado = true THEN 'Cancelado' " +
            "   WHEN p.fechaInicio > CURRENT_DATE THEN 'Pendiente' " +
            "   WHEN p.fechaInicio <= CURRENT_DATE AND p.fechaFin >= CURRENT_DATE THEN 'Activo' " +
            "   ELSE 'Vencido' END" +
            ") " +
            "FROM PagoMembresia p " +
            "LEFT JOIN p.miembro m " +
            "LEFT JOIN p.membresia tm " +
            "WHERE " +
            "p.cancelado = false " +
            "AND (p.fechaInicio > CURRENT_DATE OR " +
            "(p.fechaInicio <= CURRENT_DATE AND p.fechaFin >= CURRENT_DATE)) " +
            "AND (LOWER(m.nombre) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
            "   OR LOWER(m.apellidoPaterno) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
            "   OR LOWER(m.apellidoMaterno) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
            "   OR LOWER(tm.nombre) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
            "   OR LOWER(FUNCTION('DATE_FORMAT', p.fechaInicio, '%Y-%m-%d')) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
            "   OR LOWER(FUNCTION('DATE_FORMAT', p.fechaFin, '%Y-%m-%d')) LIKE LOWER(CONCAT('%', :keyword, '%'))" +
            ") ORDER BY p.id DESC")
    List<PagoMembresiaResumenDTO> searchResumenPagos(@Param("keyword") String keyword);

    List<PagoMembresia> findAllByOrderByIdDesc();

    @Query("""
              SELECT p FROM PagoMembresia p
               WHERE (:idMiembro   IS NULL OR p.miembro.id = :idMiembro)
                 AND (:idMembresia IS NULL OR p.membresia.id = :idMembresia)
                 AND (:fechaInicio IS NULL OR p.fechaCreacion >= :fechaInicio)
                 AND (:fechaFin    IS NULL OR p.fechaCreacion <= :fechaFin)
            """)
    List<PagoMembresia> findByFilters(
            @Param("idMiembro") Integer idMiembro,
            @Param("idMembresia") Integer idMembresia,
            @Param("fechaInicio") LocalDateTime fechaInicio,
            @Param("fechaFin") LocalDateTime fechaFin
    );

    // Busca pagos cuya fecha_fin sea exactamente la fecha dada y no estén cancelados
    List<PagoMembresia> findByFechaFinAndCanceladoFalse(LocalDate fechaFin);

}
