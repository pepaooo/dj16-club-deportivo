/*
 * Copyright (C) 2025 the original author or authors.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

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
                 AND r.fechaHoraFin > CURRENT_TIMESTAMP
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

    @Query("""
              SELECT r 
                FROM Reserva r 
               WHERE (:idInstalacion IS NULL OR r.instalacion.id = :idInstalacion)
                 AND (:idMiembro IS NULL OR r.miembro.id = :idMiembro)
                 AND (:fechaInicio IS NULL OR r.fechaHoraInicio >= :fechaInicio)
                 AND (:fechaFin IS NULL OR r.fechaHoraFin <= :fechaFin)
            """)
    List<Reserva> findByFilters(@Param("idInstalacion") Integer idInstalacion, @Param("idMiembro") Integer idMiembro, @Param("fechaInicio")  LocalDateTime fechaInicio, @Param("fechaFin")  LocalDateTime fechaFin);
}
