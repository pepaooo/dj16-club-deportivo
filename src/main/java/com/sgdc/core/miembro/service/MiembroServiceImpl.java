package com.sgdc.core.miembro.service;

import com.sgdc.core.miembro.domain.Miembro;
import com.sgdc.core.miembro.repository.MiembroRepository;
import com.sgdc.core.reportes.utils.PdfGenerator;
import jakarta.persistence.criteria.Expression;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Service
public class MiembroServiceImpl implements MiembroService {

    private final MiembroRepository repository;

    public MiembroServiceImpl(MiembroRepository repository) {
        this.repository = repository;
    }

    @Override
    public List<Miembro> findAll() {
        return repository.findAllByOrderByIdDesc();
    }

    @Override
    public Miembro findById(Integer id) {
        return repository.findById(id).orElseThrow(() -> new IllegalArgumentException("No se encontró el miembro con ID: " + id));
    }

    @Override
    public List<Miembro> search(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return repository.findAllByOrderByIdDesc();
        }
        String pattern = "%" + keyword.toLowerCase() + "%";

        Specification<Miembro> spec = (root, query, cb) -> {
            // Para atributos de tipo String se hace directamente.
            Expression<String> nombreExpr = cb.lower(root.get("nombre"));
            Expression<String> apellidoPaternoExpr = cb.lower(root.get("apellidoPaterno"));
            Expression<String> apellidoMaternoExpr = cb.lower(root.get("apellidoMaterno"));
            Expression<String> correoElectronicoExpr = cb.lower(root.get("correoElectronico"));

            return cb.or(
                    cb.like(nombreExpr, pattern),
                    cb.like(apellidoPaternoExpr, pattern),
                    cb.like(apellidoMaternoExpr, pattern),
                    cb.like(cb.lower(correoElectronicoExpr), pattern)
            );
        };

        return repository.findAll(spec, Sort.by(Sort.Direction.DESC, "id"));
    }

    @Override
    public List<Miembro> searchActive(String keyword) {
        return repository.searchActiveMembers(keyword);
    }

    @Override
    public void save(Miembro miembro) {
        repository.save(miembro);
    }

    @Override
    public List<Miembro> searchMiembros(Integer idMembresia, LocalDateTime fechaInicio, LocalDateTime fechaFin) {
        // Si todos los filtros son nulos, se devolverán todos los registros.
        if (idMembresia == null && fechaInicio == null && fechaFin == null) {
            return repository.findAllByOrderByIdDesc();
        }
        return repository.findByFilters(idMembresia, fechaInicio, fechaFin);
    }

    @Override
    public byte[] generatePdfReport(Integer idMembresia, LocalDate fechaInicio, LocalDate fechaFin) {
        // Convertir las fechas de LocalDate a LocalDateTime.
        // Para fechaInicio se usa el comienzo del día y para fechaFin se usa el final del día.
        LocalDateTime fechaInicioDT = (fechaInicio != null) ? fechaInicio.atStartOfDay() : null;
        LocalDateTime fechaFinDT = (fechaFin != null) ? fechaFin.atTime(23, 59, 59) : null;

        // Se obtienen los registros según los filtros ingresados.
        List<Miembro> miembros = searchMiembros(idMembresia, fechaInicioDT, fechaFinDT);
        // Aquí se utiliza una utilidad para generar el PDF.
        // Debes implementar la clase PdfGenerator con la lógica para crear el documento PDF.
        return PdfGenerator.generateMiembrosReport(miembros);
    }
}
