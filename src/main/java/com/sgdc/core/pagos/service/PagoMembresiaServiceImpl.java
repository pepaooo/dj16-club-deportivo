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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
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
    public List<PagoMembresia> search(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return pagoMembresiaRepository.findAll();
        }
        String pattern = "%" + keyword.toLowerCase() + "%";

        Specification<PagoMembresia> spec = (root, query, cb) -> {
            // Para buscar el nombre del miembro se realiza un join
            Join<PagoMembresia, Miembro> miembroJoin = root.join("miembro", JoinType.LEFT);
            Expression<String> miembroNombreExpr = cb.lower(miembroJoin.get("nombre"));

            // Para monto: convertirlo a String usando, por ejemplo, concat (compatible con MariaDB)
            Expression<String> montoExpr = cb.function("concat", String.class, root.get("monto"), cb.literal(""));

            // Para las fechas, podemos usar una función de formateo. Por ejemplo, en MariaDB se puede usar DATE_FORMAT.
            Expression<String> fechaPagoExpr = cb.function("DATE_FORMAT", String.class, root.get("fechaPago"), cb.literal("%Y-%m-%d"));
            Expression<String> fechaInicioExpr = cb.function("DATE_FORMAT", String.class, root.get("fechaInicio"), cb.literal("%Y-%m-%d"));
            Expression<String> fechaFinExpr = cb.function("DATE_FORMAT", String.class, root.get("fechaFin"), cb.literal("%Y-%m-%d"));

            return cb.or(
                    cb.like(miembroNombreExpr, pattern),
                    cb.like(cb.lower(montoExpr), pattern),
                    cb.like(cb.lower(fechaPagoExpr), pattern),
                    cb.like(cb.lower(fechaInicioExpr), pattern),
                    cb.like(cb.lower(fechaFinExpr), pattern)
            );
        };

        return pagoMembresiaRepository.findAll(spec);
    }


    @Transactional
    @Override
    public void save(PagoMembresiaDTO pagoMembresiaDTO) {
        Miembro miembro = miembroService.findById(pagoMembresiaDTO.getIdMiembro());
        boolean existeHistorial = pagoMembresiaRepository.existsByMiembro_Id(pagoMembresiaDTO.getIdMiembro());
        log.info("Existe historial: " + existeHistorial);

        // Cambio de membresía en miembro
        Membresia nuevaMembresia = null;
        if (pagoMembresiaDTO.getNuevoTipoMembresiaId() != null) {
            nuevaMembresia = membresiaService.findById(pagoMembresiaDTO.getNuevoTipoMembresiaId());
            miembro.setMembresia(nuevaMembresia);
            miembroService.save(miembro);
        }

        PagoMembresia pagoMembresia = PagoMembresia.builder()
                .id(pagoMembresiaDTO.getId())
                .miembro(miembro)
                .membresia(pagoMembresiaDTO.getNuevoTipoMembresiaId() != null ? nuevaMembresia : miembro.getMembresia())
                .monto(pagoMembresiaDTO.getMonto())
                .fechaPago(LocalDateTime.now())
                .fechaInicio(pagoMembresiaDTO.getFechaInicio())
                .fechaFin(pagoMembresiaDTO.getFechaInicio().plusDays(miembro.getMembresia().getDuracionDias()))
                .registradoPor(buildUsuario(pagoMembresiaDTO))
                .build();

        //log.info("Guardando pago de membresía: {}", pagoMembresia);
        pagoMembresiaRepository.save(pagoMembresia);



        // Creación de historial de membresía
        if (!existeHistorial) {
            log.info("No existe historial de membresía, creando uno nuevo.");
            HistorialMembresia historialMembresia = new HistorialMembresia();
            historialMembresia.setMiembro(miembro);
            historialMembresia.setMembresia(miembro.getMembresia());
            historialMembresia.setFechaCambio(LocalDateTime.now());
            historialMembresia.setDescripcion("Primera suscripción a " + miembro.getMembresia().getNombre());
            historialMembresia.setRegistradoPor(buildUsuario(pagoMembresiaDTO));
            log.info("Guardando historial de membresía: {}", historialMembresia);
            historialMembresiaService.save(historialMembresia);
        }

        if (pagoMembresiaDTO.getNuevoTipoMembresiaId() != null && nuevaMembresia != null) {
            log.info("Actualizando membresía del miembro a: {}", nuevaMembresia.getNombre());
            HistorialMembresia historialMembresia = new HistorialMembresia();
            historialMembresia.setMiembro(miembro);
            historialMembresia.setMembresia(nuevaMembresia);
            historialMembresia.setFechaCambio(LocalDateTime.now());
            historialMembresia.setDescripcion("Actualización a " + nuevaMembresia.getNombre());
            historialMembresia.setRegistradoPor(buildUsuario(pagoMembresiaDTO));
            historialMembresiaService.save(historialMembresia);
        }

    }

    private static Usuario buildUsuario(PagoMembresiaDTO pagoMembresiaDTO) {
        return Usuario.builder().id(pagoMembresiaDTO.getUsuarioDTO().getId()).build();
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
        return pagoMembresiaRepository.searchResumen(keyword);
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
    public List<PagoMembresiaResumenDTO> resumenPagosByMiembro(Integer idMiembro) {
        return pagoMembresiaRepository.findResumenPagosByMiembro(idMiembro);
    }

}
