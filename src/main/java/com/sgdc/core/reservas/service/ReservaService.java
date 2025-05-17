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

package com.sgdc.core.reservas.service;

import com.sgdc.core.reservas.domain.Reserva;
import com.sgdc.core.reservas.domain.dto.ReservaDTO;

import java.time.LocalDateTime;
import java.util.List;

public interface ReservaService {

    List<Reserva> findAll();

    Reserva findById(Integer id);

    List<Reserva> search(String keyword);

    ReservaDTO save(ReservaDTO reserva);

//    ReservaDTO update(ReservaDTO reserva);

    void confirmarReserva(Integer id);

    void cancelarReserva(Integer id);

    public List<Reserva> buscarPendientesSolapadas(Integer instalacionId,
                                                   LocalDateTime inicio,
                                                   LocalDateTime fin,
                                                   Integer excludeId);

    List<Reserva> searchReservas(Integer idInstalacion,
                                 Integer idMiembro,
                                 LocalDateTime fechaInicio,
                                 LocalDateTime fechaFin);

    byte[] generatePdfReport(Integer idInstalacion,
                             Integer idMiembro,
                             LocalDateTime fechaInicio,
                             LocalDateTime fechaFin);

}
