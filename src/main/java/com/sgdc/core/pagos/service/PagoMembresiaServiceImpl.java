package com.sgdc.core.pagos.service;

import com.sgdc.core.membresia.domain.HistorialMembresia;
import com.sgdc.core.membresia.domain.Membresia;
import com.sgdc.core.membresia.service.HistorialMembresiaService;
import com.sgdc.core.membresia.service.MembresiaService;
import com.sgdc.core.miembro.domain.Miembro;
import com.sgdc.core.miembro.service.MiembroService;
import com.sgdc.core.pagos.domain.PagoAjuste;
import com.sgdc.core.pagos.domain.PagoMembresia;
import com.sgdc.core.pagos.domain.dto.PagoMembresiaDTO;
import com.sgdc.core.pagos.domain.dto.PagoMembresiaResumenDTO;
import com.sgdc.core.pagos.repository.PagoMembresiaRepository;
import com.sgdc.core.usuarios.domain.Usuario;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class PagoMembresiaServiceImpl implements PagoMembresiaService {

    private static final Logger log = LoggerFactory.getLogger(PagoMembresiaServiceImpl.class);

    private final PagoMembresiaRepository pagoMembresiaRepository;
    private final MiembroService miembroService;
    private final MembresiaService membresiaService;
    private final HistorialMembresiaService historialMembresiaService;

    public PagoMembresiaServiceImpl(PagoMembresiaRepository pagoMembresiaRepository, MiembroService miembroService, MembresiaService membresiaService, HistorialMembresiaService historialMembresiaService) {
        this.pagoMembresiaRepository = pagoMembresiaRepository;
        this.miembroService = miembroService;
        this.membresiaService = membresiaService;
        this.historialMembresiaService = historialMembresiaService;
    }

    @Override
    public List<PagoMembresia> findAll() {
        return pagoMembresiaRepository.findAll();
    }

    @Override
    public PagoMembresia findById(Integer id) {
        return pagoMembresiaRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("No se encontró el pago de membresía con ID: " + id));
    }

    @Override
    public List<PagoMembresia> findByMiembroId(Integer idMiembro) {
        return pagoMembresiaRepository.findByMiembro_IdOrderByIdDesc(idMiembro);
    }

    @Override
    public List<PagoMembresia> search(Integer idMiembro, String keyword) {
        if (idMiembro == null) {
            throw new IllegalArgumentException("El id del miembro no puede ser nulo");
        }

        Specification<PagoMembresia> spec = (root, query, cb) -> {
            // Filtro obligatorio por id del miembro
            Predicate predicateBase = cb.equal(root.get("miembro").get("id"), idMiembro);

            if (keyword == null || keyword.trim().isEmpty()) {
                return predicateBase;
            }

            String pattern = "%" + keyword.toLowerCase() + "%";

            // JOIN con la membresía
            Join<PagoMembresia, Membresia> membresiaJoin = root.join("membresia", JoinType.LEFT);
            Expression<String> nombreMembresiaExpr = cb.lower(membresiaJoin.get("nombre"));

            // Para monto: convertirlo a String usando, por ejemplo, concat (compatible con MariaDB)
            Expression<String> montoExpr = cb.function("concat", String.class, root.get("monto"), cb.literal(""));

            // Para las fechas, podemos usar una función de formateo. Por ejemplo, en MariaDB se puede usar DATE_FORMAT.
            Expression<String> fechaPagoExpr = cb.function("DATE_FORMAT", String.class, root.get("fechaPago"), cb.literal("%Y-%m-%d"));
            Expression<String> fechaInicioExpr = cb.function("DATE_FORMAT", String.class, root.get("fechaInicio"), cb.literal("%Y-%m-%d"));
            Expression<String> fechaFinExpr = cb.function("DATE_FORMAT", String.class, root.get("fechaFin"), cb.literal("%Y-%m-%d"));

            Predicate keywordPredicate = cb.or(
                    cb.like(nombreMembresiaExpr, pattern),
                    cb.like(cb.lower(montoExpr), pattern),
                    cb.like(cb.lower(fechaPagoExpr), pattern),
                    cb.like(cb.lower(fechaInicioExpr), pattern),
                    cb.like(cb.lower(fechaFinExpr), pattern)
            );
            return cb.and(predicateBase, keywordPredicate);
        };

        return pagoMembresiaRepository.findAll(spec, Sort.by(Sort.Direction.DESC, "id")
        );
    }


    @Transactional
    @Override
    public void save(PagoMembresiaDTO dto) {
        // 0) guardamos el pago nuevo y calculamos iniN, finN
        PagoMembresia nuevo = buildAndSaveNuevo(dto);
        LocalDate iniN = nuevo.getFechaInicio();
        LocalDate finN = nuevo.getFechaFin();

        // 1) ajustamos sólo los que realmente solapan contra [iniN, finN]
        List<PagoMembresia> solapados = pagoMembresiaRepository.findOverlapping(dto.getIdMiembro(), iniN, finN);
        List<PagoMembresia> cola = new ArrayList<>();

        for (PagoMembresia ex : solapados) {
            LocalDate ini = ex.getFechaInicio(), fin = ex.getFechaFin();
            // 1.0) si el pago es el nuevo, lo ignoramos
            if (ex.getId().equals(nuevo.getId())) {
                log.info("Pago {} es el nuevo. Ignorando solapamiento.", ex.getId());
                continue;
            }

            // 1.1) totalmente dentro → cancelar
            if (!ini.isBefore(iniN) && !fin.isAfter(finN)) {
                ex.setCancelado(true);
                ex.setFechaCancelacion(LocalDateTime.now());
                ex.setMotivoCancelacion("Pago solapado por otro nuevo. Id de pago: " + nuevo.getId());
                pagoMembresiaRepository.save(ex);
                log.info("Cancelación de Pago {}. Pago solapado por otro nuevo. Id del nuevo pago: {}", ex.getId(), nuevo.getId());
                continue;
            }
            // 1.2) envuelto por completo → fragmentar en dos
            if (ini.isBefore(iniN) && fin.isAfter(finN)) {
                log.info("Fragmentación de Pago {}. Pago solapado por otro nuevo. Id del nuevo pago: {}", ex.getId(), nuevo.getId());
                ex.setFechaFin(iniN.minusDays(1));
                pagoMembresiaRepository.save(ex);
                PagoMembresia frag = cloneSinId(ex);
                frag.setFechaInicio(finN.plusDays(1));
                frag.setFechaFin(frag.getFechaInicio()
                        .plusDays(ex.getMembresia().getDuracionDias()));
//                frag.setEstatus("Pendiente");
                cola.add(frag);
                continue;
            }
            // 1.3) solapa al principio → recortar fin
            if (ini.isBefore(iniN) && fin.isAfter(iniN)) {
                log.info("Recorte de Pago {}. Pago solapado por otro nuevo. Id del nuevo pago: {}", ex.getId(), nuevo.getId());
                ex.setFechaFin(iniN.minusDays(1));
                pagoMembresiaRepository.save(ex);
                continue;
            }
            // 1.4) solapa al final → encolar entero
            if (ini.isBefore(finN) && fin.isAfter(finN)) {
                log.info("Reubicación 1 de Pago {}. Pago solapado por otro nuevo. Id del nuevo pago: {}", ex.getId(), nuevo.getId());
                LocalDate nuevoInicio = finN.plusDays(1);
                ex.setFechaInicio(nuevoInicio);
                ex.setFechaFin(nuevoInicio.plusDays(ex.getMembresia().getDuracionDias()));
                pagoMembresiaRepository.save(ex);
                continue;
            }
        }

        // 2) ahora añadimos **todos** los pagos futuros que queden:
        //    los que no solaparon y los fragmentos nuevos
        List<PagoMembresia> futuros = pagoMembresiaRepository
                .findFutureByMiembroOrderByFechaInicio(dto.getIdMiembro(), iniN);
        for (PagoMembresia f : futuros) {
            if (!cola.contains(f)) cola.add(f);
        }

        // 3) reubicamos en cadena, empezando justo al final de finN
        LocalDate prevEnd = finN;
        for (PagoMembresia ex : cola) {
            long dur = ex.getMembresia().getDuracionDias();
            LocalDate start = prevEnd.plusDays(1);
            ex.setFechaInicio(start);
            ex.setFechaFin(start.plusDays(dur));
//            ex.setEstatus("Pendiente");
            log.info("Reubicación 2 de Pago {}. Pago solapado por otro nuevo. Id del nuevo pago: {}", ex.getId(), nuevo.getId());
            pagoMembresiaRepository.save(ex);
            prevEnd = ex.getFechaFin();
        }
    }

    private PagoMembresia cloneSinId(PagoMembresia ex) {
        return PagoMembresia.builder()
                .miembro(ex.getMiembro())
                .membresia(ex.getMembresia())
                .monto(ex.getMonto())
                .fechaPago(ex.getFechaPago())
                .fechaInicio(ex.getFechaInicio())
                .fechaFin(ex.getFechaFin())
                .registradoPor(ex.getRegistradoPor())
                .cancelado(ex.isCancelado())
                .build();
    }

    private PagoMembresia buildAndSaveNuevo(PagoMembresiaDTO dto) {
        Miembro miembro = miembroService.findById(dto.getIdMiembro());
        // -------------------------------------------------------
        // 1) Calculamos el rango del nuevo pago
        LocalDate inicioNuevo = dto.getFechaInicio();
        Membresia planNuevo = membresiaService.findById(dto.getMembresiaId());
        LocalDate finNuevo = inicioNuevo.plusDays(planNuevo.getDuracionDias());
        // -------------------------------------------------------
        // 2) Insertamos el nuevo pago
        PagoMembresia nuevo = PagoMembresia.builder()
                .miembro(miembro)
                .membresia(planNuevo)
                .monto(dto.getMonto())
                .fechaPago(LocalDateTime.now())
                .fechaInicio(inicioNuevo)
                .fechaFin(finNuevo)
                .registradoPor(buildUsuario(dto))
                .build();
        pagoMembresiaRepository.save(nuevo);
        // -------------------------------------------------------
        // 3) Histórico de cambio de plan
        boolean primerPago = !pagoMembresiaRepository.existsByMiembro_Id(miembro.getId());
        HistorialMembresia hist = new HistorialMembresia();
        hist.setMiembro(miembro);
        hist.setMembresia(planNuevo);
        hist.setFechaCambio(LocalDateTime.now());
        hist.setDescripcion((primerPago ? "Primera suscripción a " : "Renovación a ")
                + planNuevo.getNombre());
        hist.setRegistradoPor(buildUsuario(dto));
        historialMembresiaService.save(hist);

        return nuevo;
    }

    private static Usuario buildUsuario(PagoMembresiaDTO dto) {
        return Usuario.builder().id(dto.getUsuarioDTO().getId()).build();
    }

    @Override
    public List<PagoMembresiaResumenDTO> resumenPagos() {
        return pagoMembresiaRepository.findResumenPagos();
    }

    @Override
    public List<PagoMembresiaResumenDTO> searchResumen(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return pagoMembresiaRepository.findResumenPagos();
        }
        return pagoMembresiaRepository.searchResumenPagos(keyword);
    }

    @Override
    public BigDecimal calcularMontoFinal(PagoMembresia pago, List<PagoAjuste> ajustes) {
        BigDecimal montoFinal = pago.getMonto() != null ? pago.getMonto() : BigDecimal.ZERO;
        if (ajustes != null) {
            for (PagoAjuste ajuste : ajustes) {
                montoFinal = montoFinal.add(ajuste.getMontoAjuste());
            }
        }
        return montoFinal;
    }

    @Override
    public List<PagoMembresiaResumenDTO> resumenPagosByMiembro(Integer idMiembro, String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return pagoMembresiaRepository.findResumenPagosByMiembro(idMiembro);
        }
        return pagoMembresiaRepository.searchResumenPagosByMiembro(idMiembro, keyword);
    }

    @Override
    public List<PagoMembresiaResumenDTO> resumenAllPagosByMiembro(Integer idMiembro, int limite) {
        return pagoMembresiaRepository.findAllResumenPagosByMiembro(idMiembro, limite != -1 ? PageRequest.of(0, limite) : Pageable.unpaged());
    }
}
