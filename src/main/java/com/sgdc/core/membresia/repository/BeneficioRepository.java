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

package com.sgdc.core.membresia.repository;

import com.sgdc.core.membresia.domain.Beneficio;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface BeneficioRepository extends JpaRepository<Beneficio, Integer>, JpaSpecificationExecutor<Beneficio> {
    // Lista todos los beneficios en orden descendente por ID
    List<Beneficio> findAllByOrderByIdDesc();

    @Query("""
              SELECT b
                FROM Beneficio b
                JOIN b.membresias m
               WHERE m.id = :idMembresia
            """)
    List<Beneficio> findAllByMembresiaId(@Param("idMembresia") Integer idMembresia);
}
