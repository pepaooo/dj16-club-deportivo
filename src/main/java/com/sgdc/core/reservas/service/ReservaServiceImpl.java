package com.sgdc.core.reservas.service;

import com.sgdc.core.miembro.domain.Miembro;
import com.sgdc.core.reservas.domain.EstadoReserva;
import com.sgdc.core.reservas.domain.Reserva;
import com.sgdc.core.reservas.exception.ReservaSolapadaException;
import com.sgdc.core.reservas.repository.ReservaRepository;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Service
public class ReservaServiceImpl implements ReservaService {

    private final ReservaRepository repository;

    public ReservaServiceImpl(ReservaRepository repository) {
        this.repository = repository;
    }

    @Override
    public List<Reserva> findAll() {
        return repository.findAll();
    }

    @Override
    public Reserva findById(Integer id) {
        return repository.findById(id).orElseThrow(() ->
                new IllegalArgumentException("No se encontró la reserva con ID: " + id)
        );
    }

    @Override
    public List<Reserva> search(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return repository.findAllByOrderByIdDesc();
        }
        String pattern = "%" + keyword.toLowerCase() + "%";

        Specification<Reserva> spec = (root, query, cb) -> {

            // JOIN con miembro
            Join<Reserva, Miembro> miembroJoin = root.join("miembro", JoinType.LEFT);
            Expression<String> nombreMiembroExpr = cb.lower(miembroJoin.get("nombre"));

            // Para atributos de tipo String se hace directamente.
            Expression<String> estadoReservaExpr = cb.lower(root.get("estadoReserva"));

            // Para las fechas, podemos usar una función de formateo. Por ejemplo, en MariaDB se puede usar DATE_FORMAT.
            Expression<String> fechaHoraInicioExpr = cb.function("DATE_FORMAT", String.class, root.get("fechaHoraInicio"), cb.literal("%Y-%m-%d"));
            Expression<String> fechaHoraFinExpr = cb.function("DATE_FORMAT", String.class, root.get("fechaHoraFin"), cb.literal("%Y-%m-%d"));

            return cb.or(
                    cb.like(nombreMiembroExpr, pattern),
                    cb.like(estadoReservaExpr, pattern),
                    cb.like(fechaHoraInicioExpr, pattern),
                    cb.like(fechaHoraFinExpr, pattern)
            );
        };

        return repository.findAll(spec, Sort.by(Sort.Direction.DESC, "id"));
    }

    @Override
    public void save(Reserva reserva) {
        Long solapadas = repository.countReservasSolapadas(
                reserva.getInstalacion().getId(),
                reserva.getFechaHoraInicio(),
                reserva.getFechaHoraFin()
        );

        if (solapadas != null && solapadas > 0) {
            throw new ReservaSolapadaException("Ya existe una reserva confirmada en esta franja horaria para la instalación.");
        }
        repository.save(reserva);
    }

    @Transactional
    @Override
    public Optional<Reserva> confirmarReserva(Integer id) {
        Optional<Reserva> reserva = repository.findById(id);
        if (reserva.isPresent()) {
            Reserva seleccionada = reserva.get();
            seleccionada.setEstadoReserva(EstadoReserva.CONFIRMADA.getLabel());
            repository.save(seleccionada);

            // 2. Buscar y cancelar todas las “Pendiente” que solapen
            List<Reserva> solapadas = repository.findPendientesSolapadas(
                    seleccionada.getInstalacion().getId(),
                    seleccionada.getFechaHoraInicio(),
                    seleccionada.getFechaHoraFin(),
                    seleccionada.getId()
            );
            for (Reserva r : solapadas) {
                r.setEstadoReserva(EstadoReserva.CANCELADA.getLabel());
            }
            repository.saveAll(solapadas);

            // 3. (En un futuro se podría implementar) Enviar notificaciones a los usuarios de las reservas canceladas
            // notificationService.notifyCancellations(solapadas);

            return Optional.of(seleccionada);
        }
        return Optional.empty();
    }

    @Override
    public Optional<Reserva> cancelarReserva(Integer id) {
        Optional<Reserva> reserva = repository.findById(id);
        if (reserva.isPresent()) {
            Reserva r = reserva.get();
            r.setEstadoReserva("Cancelada");
            repository.save(r);
            return Optional.of(r);
        }
        return Optional.empty();
    }

    @Override
    public List<Reserva> buscarPendientesSolapadas(Integer instalacionId, LocalDateTime inicio, LocalDateTime fin, Integer excludeId) {
        return repository.findPendientesSolapadas(instalacionId, inicio, fin, excludeId);
    }
}
