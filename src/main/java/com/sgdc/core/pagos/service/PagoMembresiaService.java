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
