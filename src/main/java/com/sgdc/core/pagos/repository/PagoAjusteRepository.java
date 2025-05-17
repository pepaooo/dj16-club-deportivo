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

package com.sgdc.core.pagos.repository;

import com.sgdc.core.pagos.domain.PagoAjuste;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.time.LocalDateTime;
import java.util.List;

public interface PagoAjusteRepository extends JpaRepository<PagoAjuste, Integer>, JpaSpecificationExecutor<PagoAjuste> {

    /**
     * Finds all PagoAjuste records within a specified date range.
     *
     * @param startDate the start date of the range (inclusive)
     * @param endDate   the end date of the range (inclusive)
     * @return a list of PagoAjuste records within the specified date range
     */
    List<PagoAjuste> findByFechaAjusteBetween(LocalDateTime startDate, LocalDateTime endDate);

    /**
     * Finds all PagoAjuste records associated with a specific PagoMembresia ID.
     *
     * @param idPagoMembresia the ID of the PagoMembresia
     * @return a list of PagoAjuste records associated with the specified PagoMembresia ID
     */
    List<PagoAjuste> findByPagoMembresia_Id(Integer idPagoMembresia);
}
