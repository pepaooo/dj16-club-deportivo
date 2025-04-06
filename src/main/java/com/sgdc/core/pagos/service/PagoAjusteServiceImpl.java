package com.sgdc.core.pagos.service;

import com.sgdc.core.pagos.domain.PagoAjuste;
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
        // Validar el objeto PagoAjuste antes de guardarlo
        if (pagoAjuste != null) {
            repository.save(pagoAjuste);
        } else {
            throw new IllegalArgumentException("El objeto PagoAjuste no puede ser nulo");
        }
    }

    @Override
    public List<PagoAjuste> findByPagoMembresiaId(Integer idPagoMembresia) {
        return repository.findByPagoMembresia_Id(idPagoMembresia);
    }
}
