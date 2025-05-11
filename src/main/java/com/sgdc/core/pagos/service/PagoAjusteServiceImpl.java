package com.sgdc.core.pagos.service;

import com.sgdc.core.auditoria.aop.Auditable;
import com.sgdc.core.pagos.domain.PagoAjuste;
import com.sgdc.core.pagos.exception.PagoInactivoException;
import com.sgdc.core.pagos.repository.PagoAjusteRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class PagoAjusteServiceImpl implements PagoAjusteService {

    private static final Logger log = LoggerFactory.getLogger(PagoAjusteServiceImpl.class);

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

    @Auditable(
            tipoAccion = "CREATE",
            tabla = "pago_ajuste",
            entidadId = "#result.id",
            descripcion = "'Creación de ajuste al pago '+#result.pagoMembresia.id + ' de membresía con monto de '+#result.montoAjuste + '. Razón: '+#result.descripcion"
    )
    @Override
    public PagoAjuste save(PagoAjuste pagoAjuste) {
        // Validamos que el estatus del pago sea "Activo" o "Pendiente"
        pagoAjuste.setFechaAjuste(LocalDateTime.now());
        log.debug("save {}", pagoAjuste);
        if (!pagoAjuste.getPagoMembresia().getEstatus().equalsIgnoreCase("Activo")
                && !pagoAjuste.getPagoMembresia().getEstatus().equalsIgnoreCase("Pendiente")) {
            throw new PagoInactivoException("El pago no está activo y no se puede ajustar.");
        }
        return repository.save(pagoAjuste);
    }

    @Override
    public List<PagoAjuste> findByPagoMembresiaId(Integer idPagoMembresia) {
        return repository.findByPagoMembresia_Id(idPagoMembresia);
    }
}
