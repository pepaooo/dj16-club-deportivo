package com.sgdc.core.pagos.service;

import com.sgdc.core.miembro.domain.Miembro;
import com.sgdc.core.pagos.domain.PagoMembresia;
import com.sgdc.core.pagos.domain.dto.PagoMembresiaResumenDTO;

import java.util.List;
import java.util.Optional;

public interface PagoMembresiaService {

    List<PagoMembresia> findAll();

    Optional<PagoMembresia> findById(Integer id);

    List<PagoMembresia> findByMiembroId(Integer idMiembro);

    List<PagoMembresia> search(String keyword);

    void save(PagoMembresia pagoMembresia);

    List<PagoMembresiaResumenDTO> resumenPagos();

    List<PagoMembresiaResumenDTO> searchResumen(String keyword);

}
