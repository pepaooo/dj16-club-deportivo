package com.sgdc.core.reservas.repository;

import com.sgdc.core.reservas.domain.Reserva;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface ReservaRepository extends JpaRepository<Reserva, Integer>, JpaSpecificationExecutor<Reserva> {
    // Listas de todas las reservas ordenadas de forma descendente por ID
    List<Reserva> findAllByOrderByIdDesc();

    List<Reserva> findByFechaHoraFinGreaterThanEqualOrderByFechaHoraInicioAsc(LocalDateTime ahora);

    @Query("SELECT COUNT(r) " +
            "FROM Reserva r " +
            "WHERE r.instalacion.id = :idInstalacion " +
            "  AND r.estadoReserva = 'Confirmada' " +
            "  AND r.fechaHoraFin > :fechaInicioDeseada " +
            "  AND r.fechaHoraInicio < :fechaFinDeseada")
    Long countReservasSolapadas(@Param("idInstalacion") Integer idInstalacion,
                                @Param("fechaInicioDeseada") LocalDateTime fechaInicioDeseada,
                                @Param("fechaFinDeseada") LocalDateTime fechaFinDeseada);

    @Query("""
              SELECT r 
                FROM Reserva r 
               WHERE r.instalacion.id = :instalacionId
                 AND r.estadoReserva = 'Pendiente'
                 AND r.fechaHoraFin   > :fechaInicio
                 AND r.fechaHoraInicio < :fechaFin
                 AND (:excludeId IS NULL OR r.id <> :excludeId)
            """)
    List<Reserva> findPendientesSolapadas(
            @Param("instalacionId") Integer instalacionId,
            @Param("fechaInicio") LocalDateTime fechaInicio,
            @Param("fechaFin") LocalDateTime fechaFin,
            @Param("excludeId") Integer excludeId
    );

}
