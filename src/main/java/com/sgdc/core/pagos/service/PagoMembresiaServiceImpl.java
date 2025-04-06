package com.sgdc.core.pagos.service;

import com.sgdc.core.miembro.domain.Miembro;
import com.sgdc.core.pagos.domain.PagoMembresia;
import com.sgdc.core.pagos.domain.dto.PagoMembresiaResumenDTO;
import com.sgdc.core.pagos.repository.PagoMembresiaRepository;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PagoMembresiaServiceImpl implements PagoMembresiaService {

    private final PagoMembresiaRepository repository;

    public PagoMembresiaServiceImpl(PagoMembresiaRepository repository) {
        this.repository = repository;
    }

    @Override
    public List<PagoMembresia> findAll() {
        return repository.findAll();
    }

    @Override
    public Optional<PagoMembresia> findById(Integer id) {
        return repository.findById(id);
    }

    @Override
    public List<PagoMembresia> findByMiembroId(Integer idMiembro) {
        return repository.findByMiembro_IdOrderByIdDesc(idMiembro);
    }

    @Override
    public List<PagoMembresia> search(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return repository.findAll();
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

        return repository.findAll(spec);
    }


    @Override
    public void save(PagoMembresia pagoMembresia) {
        repository.save(pagoMembresia);
    }

    @Override
    public List<PagoMembresiaResumenDTO> resumenPagos() {
        return repository.findResumenPagos();
    }

    @Override
    public List<PagoMembresiaResumenDTO> searchResumen(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return repository.findResumenPagos();
        }
        return repository.searchResumen(keyword);
    }
}
