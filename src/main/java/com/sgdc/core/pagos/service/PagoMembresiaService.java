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

package com.sgdc.core.pagos.service;

import com.sgdc.core.pagos.domain.PagoAjuste;
import com.sgdc.core.pagos.domain.PagoMembresia;
import com.sgdc.core.pagos.domain.dto.PagoMembresiaDTO;
import com.sgdc.core.pagos.domain.dto.PagoMembresiaResumenDTO;
import com.sgdc.core.reportes.domain.dto.PagoReportDTO;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface PagoMembresiaService {

    List<PagoMembresia> findAll();

    PagoMembresia findById(Integer id);

    List<PagoMembresia> findByMiembroId(Integer idMiembro);

    Optional<PagoMembresia> findActiveByMiembro(Integer idMiembro);

    List<PagoMembresia> search(Integer idMiembro, String keyword);

    List<PagoMembresiaResumenDTO> resumenPagosByMiembro(Integer idMiembro, String keyword);

    List<PagoMembresiaResumenDTO> resumenAllPagosByMiembro(Integer idMiembro, int limite);

    List<PagoMembresiaResumenDTO> resumenPagos();

    List<PagoMembresiaResumenDTO> searchResumen(String keyword);

    BigDecimal calcularMontoFinal(PagoMembresia pago, List<PagoAjuste> ajustes);

    PagoMembresiaDTO save(PagoMembresiaDTO pagoMembresiaDTO);

    void cancelarPago(Integer idPago, String motivo);

    List<PagoReportDTO> searchPagosReport(Integer idMiembro,
                                    Integer idMembresia,
                                    LocalDateTime fechaInicio,
                                    LocalDateTime fechaFin);

    byte[] generatePdfReport(Integer idMiembro,
                             Integer idMembresia,
                             LocalDateTime fechaInicio,
                             LocalDateTime fechaFin);
}
