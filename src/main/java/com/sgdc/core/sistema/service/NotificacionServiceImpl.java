package com.sgdc.core.sistema.service;

import com.sgdc.core.sistema.domain.Notificacion;
import com.sgdc.core.sistema.domain.dto.NotificacionSearchDTO;
import com.sgdc.core.sistema.repository.NotificacionRepository;
import com.sgdc.core.pagos.domain.PagoMembresia;
import com.sgdc.core.pagos.repository.PagoMembresiaRepository;
import jakarta.persistence.EntityNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
public class NotificacionServiceImpl implements NotificacionService {

    private static final Logger log = LoggerFactory.getLogger(NotificacionServiceImpl.class);

    private final ConfiguracionSistemaService configService;
    private final PagoMembresiaRepository pagoRepo;
    private final NotificacionRepository notiRepo;

    public NotificacionServiceImpl(ConfiguracionSistemaService configService, PagoMembresiaRepository pagoRepo, NotificacionRepository notiRepo) {
        this.configService = configService;
        this.pagoRepo = pagoRepo;
        this.notiRepo = notiRepo;
    }

    @Transactional
    @Override
    public void generarNotificacionesBatch() {
        if (!configService.isNotificacionesActivadas()) {
            return;
        }

        LocalDate hoy = LocalDate.now();
        for (Integer diasAntes : configService.getNotificationPeriods()) {
            LocalDate fechaObjetivo = hoy.plusDays(diasAntes);
            List<PagoMembresia> pagos
                    = pagoRepo.findByFechaFinAndCanceladoFalse(fechaObjetivo);

            pagos.forEach(pago -> {
                Integer idPagoMembresia = pago.getId();
                // si ya existe, saltamos
                if (notiRepo.existsByPagoMembresia_IdAndFechaVencimiento(idPagoMembresia, fechaObjetivo)) {
                    log.debug("Notificación ya existe para el pago {} y fecha {}", idPagoMembresia, fechaObjetivo);
                    return;
                }
                Notificacion n = new Notificacion();
                n.setPagoMembresia(pago);
                n.setFechaVencimiento(fechaObjetivo);
                n.setEstado("Pendiente");
                n.setMensaje("Su membresía vence en "
                        + diasAntes + " día(s).");
                notiRepo.save(n);
            });
        }
    }

    @Override
    public Notificacion findById(Integer idNotificacion) {
        return notiRepo.findById(idNotificacion)
                .orElseThrow(() -> new EntityNotFoundException(
                        "Notificación no encontrada: " + idNotificacion));
    }

    @Override
    public List<NotificacionSearchDTO> search(String keyword) {
        return notiRepo.search(keyword);
    }
}
