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

package com.sgdc.core.miembro.service;

import com.sgdc.core.miembro.domain.Miembro;
import com.sgdc.core.miembro.domain.dto.MiembroSearchDTO;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public interface MiembroService {

    List<Miembro> findAll();

    Miembro findById(Integer id);

    List<Miembro> search(String keyword);

    List<MiembroSearchDTO> searchActive(String keyword);

    Miembro save(Miembro miembro);

    Miembro update(Miembro miembro);

    List<Miembro> searchMiembros(Integer idMembresia, LocalDateTime fechaInicio, LocalDateTime fechaFin);

    byte[] generatePdfReport(Integer idMembresia, LocalDate fechaInicio, LocalDate fechaFin);


}
