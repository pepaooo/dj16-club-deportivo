package com.sgdc.core.reservas.service;

import com.sgdc.core.miembro.domain.Miembro;
import com.sgdc.core.reportes.utils.PdfGenerator;
import com.sgdc.core.reservas.domain.EstadoReserva;
import com.sgdc.core.reservas.domain.Instalacion;
import com.sgdc.core.reservas.domain.Reserva;
import com.sgdc.core.reservas.domain.dto.ReservaDTO;
import com.sgdc.core.reservas.exception.ReservaInvalidaException;
import com.sgdc.core.reservas.exception.ReservaSolapadaException;
import com.sgdc.core.reservas.repository.ReservaRepository;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

@Service
public class ReservaServiceImpl implements ReservaService {

    private static final Logger log = LoggerFactory.getLogger(ReservaServiceImpl.class);

    private final ReservaRepository repository;

    private final ModelMapper modelMapper;

    public ReservaServiceImpl(ReservaRepository repository, ModelMapper modelMapper) {
        this.repository = repository;
        this.modelMapper = modelMapper;
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
        LocalDateTime ahora = LocalDateTime.now();

        if (keyword == null || keyword.trim().isEmpty()) {
            return repository.findByFechaHoraFinGreaterThanEqualOrderByFechaHoraInicioAsc(ahora);
        }
        String pattern = "%" + keyword.toLowerCase() + "%";

        Specification<Reserva> spec = (root, query, cb) -> {
            // 1) Sólo reservas aún vigentes
            Predicate vigentes = cb.greaterThanOrEqualTo(root.get("fechaHoraFin"), ahora);


            // JOIN con miembro
            Join<Reserva, Miembro> miembroJoin = root.join("miembro", JoinType.LEFT);
            Predicate pNombreMiembro = cb.like(cb.lower(miembroJoin.get("nombre")), pattern);
            Predicate pApellidoMiembro = cb.like(cb.lower(miembroJoin.get("apellidoPaterno")), pattern);

            // 3) JOIN con Instalación
            Join<Reserva, Instalacion> instalacionJoin = root.join("instalacion", JoinType.LEFT);
            Predicate pNombreInstalacion = cb.like(cb.lower(instalacionJoin.get("nombre")), pattern);

            // 4) Estado y fechas
            Predicate pEstado = cb.like(cb.lower(root.get("estadoReserva")), pattern);
            Predicate pInicio = cb.like(
                    cb.function("DATE_FORMAT", String.class, root.get("fechaHoraInicio"), cb.literal("%Y-%m-%d %H:%i")),
                    pattern
            );
            Predicate pFin = cb.like(
                    cb.function("DATE_FORMAT", String.class, root.get("fechaHoraFin"), cb.literal("%Y-%m-%d %H:%i")),
                    pattern
            );

            // 5) Unimos todos los OR de texto
            Predicate texto = cb.or(
                    pNombreMiembro,
                    pApellidoMiembro,
                    pNombreInstalacion,
                    pEstado,
                    pInicio,
                    pFin
            );

            // 6) Combinamos con el filtro de vigentes
            return cb.and(vigentes, texto);
        };

        return repository.findAll(spec, Sort.by(Sort.Direction.ASC, "fechaHoraInicio"));
    }

    @Override
    public void save(ReservaDTO dto) {
        // 1. Convertir DTO a entidad
        Reserva reserva = modelMapper.map(dto, Reserva.class);

        // 1. Validar que las fechas de inicio y fin sean correctas
        // Traza el now hasta minutos para ignorar segundos/nanos
        LocalDateTime ahora = LocalDateTime.now().truncatedTo(ChronoUnit.MINUTES);
        log.info("Reserva Ahora: {}, Inicio: {}, Fin:  {}", ahora, reserva.getFechaHoraInicio(), reserva.getFechaHoraFin());
        if (reserva.getFechaHoraInicio().isBefore(ahora)) {
            throw new ReservaInvalidaException("La fecha de inicio no puede ser en el pasado");
        }
        if (reserva.getFechaHoraFin().isBefore(reserva.getFechaHoraInicio()) ||
                reserva.getFechaHoraFin().isEqual(reserva.getFechaHoraInicio())) {
            throw new ReservaInvalidaException("La fecha fin debe ser posterior a la de inicio");
        }

        // 2. Verificar que no haya reservas solapadas
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

    @Override
    public List<Reserva> searchReservas(Integer idInstalacion, Integer idMiembro, LocalDateTime fechaInicio, LocalDateTime fechaFin) {
        // Si todos los filtros son nulos, se devolverán todos los registros.
        if (idInstalacion == null && idMiembro == null && fechaInicio == null && fechaFin == null) {
            return repository.findAllByOrderByIdDesc();
        }
        return repository.findByFilters(idInstalacion, idMiembro, fechaInicio, fechaFin);
    }

    @Override
    public byte[] generatePdfReport(Integer idInstalacion, Integer idMiembro, LocalDateTime fechaInicio, LocalDateTime fechaFin) {
        // Obtener las reservas filtradas
        List<Reserva> reservas = searchReservas(idInstalacion, idMiembro, fechaInicio, fechaFin);
        // Generar el informe PDF
        return PdfGenerator.generateReservasReport(reservas);
    }
}
