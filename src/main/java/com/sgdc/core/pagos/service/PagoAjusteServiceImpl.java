package com.sgdc.core.pagos.service;

import com.sgdc.core.pagos.domain.PagoAjuste;
import com.sgdc.core.pagos.exception.PagoInactivoException;
import com.sgdc.core.pagos.repository.PagoAjusteRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PagoAjusteServiceImpl implements PagoAjusteService {

    private final PagoAjusteRepository repository;

    public PagoAjusteServiceImpl(PagoAjusteRepository repository) {
        this.repository = repository;
    }

    @Override
    public List<PagoAjuste> findAll() {
        return repository.findAll();
    }

    @Override
    public Optional<PagoAjuste> findById(Integer id) {
        return repository.findById(id);
    }

    @Override
    public void save(PagoAjuste pagoAjuste) {
        // Validamos que el estatus del pago sea "Activo"
        if (!pagoAjuste.getPagoMembresia().getEstatus().equalsIgnoreCase("Activo")) {
            throw new PagoInactivoException("El pago no está activo y no se puede ajustar.");
        }
        repository.save(pagoAjuste);
    }

    @Override
    public List<PagoAjuste> findByPagoMembresiaId(Integer idPagoMembresia) {
        return repository.findByPagoMembresia_Id(idPagoMembresia);
    }
}
