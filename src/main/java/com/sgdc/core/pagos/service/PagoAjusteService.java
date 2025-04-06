package com.sgdc.core.pagos.service;

import com.sgdc.core.pagos.domain.PagoAjuste;

import java.util.List;
import java.util.Optional;

public interface PagoAjusteService {

    List<PagoAjuste> findAll();

    Optional<PagoAjuste> findById(Integer id);

    List<PagoAjuste> findByPagoMembresiaId(Integer idPagoMembresia);

    void save(PagoAjuste pagoAjuste);

}
