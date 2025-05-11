package com.sgdc.core.sistema.repository;

import com.sgdc.core.sistema.domain.Notificacion;
import com.sgdc.core.sistema.domain.dto.NotificacionSearchDTO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;

public interface NotificacionRepository extends JpaRepository<Notificacion, Integer> {
    // Evita duplicar notificaciones para el mismo pago y fecha
    boolean existsByPagoMembresia_IdAndFechaVencimiento(
            Integer idPagoMembresia, LocalDate fechaVencimiento);

    // Busca notificaciones por estado
    List<Notificacion> findByEstado(String estado);

    @Query("""
              SELECT new com.sgdc.core.sistema.domain.dto.NotificacionSearchDTO(
                n.id,
                CONCAT(m.nombre, ' ', m.apellidoPaterno, ' ', m.apellidoMaterno),
                mb.nombre,
                n.fechaVencimiento,
                n.estado
              )
                FROM Notificacion n
                JOIN n.pagoMembresia p
                JOIN p.miembro m
                JOIN p.membresia mb
               WHERE n.fechaVencimiento >= CURRENT_DATE
                  AND (
                      :q IS NULL
                      OR LOWER(m.nombre)           LIKE LOWER(CONCAT('%', :q, '%'))
                      OR LOWER(m.apellidoPaterno)  LIKE LOWER(CONCAT('%', :q, '%'))
                      OR LOWER(m.apellidoMaterno)  LIKE LOWER(CONCAT('%', :q, '%'))
                      OR LOWER(mb.nombre)          LIKE LOWER(CONCAT('%', :q, '%'))
                      OR LOWER(FUNCTION('DATE_FORMAT', n.fechaVencimiento, '%Y-%m-%d'))
                           LIKE LOWER(CONCAT('%', :q, '%'))
                      OR LOWER(n.estado)           LIKE LOWER(CONCAT('%', :q, '%'))
                  )
               ORDER BY n.fechaVencimiento ASC
            """)
    List<NotificacionSearchDTO> search(@Param("q") String q);
}

