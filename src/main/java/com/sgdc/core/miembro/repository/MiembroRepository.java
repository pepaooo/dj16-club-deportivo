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

package com.sgdc.core.miembro.repository;

import com.sgdc.core.miembro.domain.Miembro;
import com.sgdc.core.miembro.domain.dto.MiembroSearchDTO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;

public interface MiembroRepository extends JpaRepository<Miembro, Integer>, JpaSpecificationExecutor<Miembro> {
    // Lista todos los miembros en orden descendente por ID
    List<Miembro> findAllByOrderByIdDesc();

    @Query("""
            SELECT DISTINCT m
              FROM Miembro m
             WHERE (:idMembresia IS NULL
                    OR m.id = :idMembresia)
               AND (:fechaInicio IS NULL OR m.fechaCreacion >= :fechaInicio)
               AND (:fechaFin    IS NULL OR m.fechaCreacion <= :fechaFin)
             ORDER BY m.id DESC
            """)
    List<Miembro> findByFilters(
            @Param("idMembresia") Integer idMembresia,
            @Param("fechaInicio") LocalDateTime fechaInicio,
            @Param("fechaFin") LocalDateTime fechaFin
    );


    @Query("""
      SELECT new com.sgdc.core.miembro.domain.dto.MiembroSearchDTO(
        m.id,
        m.nombre,
        m.apellidoPaterno,
        m.apellidoMaterno,
        m.correoElectronico,
        p.membresia.id,
        p.membresia.nombre
      )
        FROM Miembro m
        JOIN PagoMembresia p
          ON p.miembro.id = m.id
       WHERE p.fechaInicio = (
               SELECT MAX(p2.fechaInicio)
                 FROM PagoMembresia p2
                WHERE p2.miembro.id = m.id
                  AND p2.fechaInicio <= CURRENT_DATE
             )
         AND p.cancelado = false
         AND p.fechaInicio <= CURRENT_DATE
         AND p.fechaFin    >= CURRENT_DATE
         AND ( :q IS NULL
               OR LOWER(m.nombre)             LIKE LOWER(CONCAT('%', :q, '%'))
               OR LOWER(m.apellidoPaterno)    LIKE LOWER(CONCAT('%', :q, '%'))
               OR LOWER(m.apellidoMaterno)    LIKE LOWER(CONCAT('%', :q, '%'))
               OR LOWER(m.correoElectronico)  LIKE LOWER(CONCAT('%', :q, '%')) )
    """)
    List<MiembroSearchDTO> searchActiveMembers(@Param("q") String keyword);
}
