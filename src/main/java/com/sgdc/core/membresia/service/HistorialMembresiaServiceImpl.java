package com.sgdc.core.membresia.service;

import com.sgdc.core.membresia.domain.HistorialMembresia;
import com.sgdc.core.membresia.domain.Membresia;
import com.sgdc.core.membresia.repository.HistorialMembresiaRepository;
import com.sgdc.core.pagos.domain.PagoMembresia;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class HistorialMembresiaServiceImpl implements HistorialMembresiaService {

    private final HistorialMembresiaRepository repository;

    public HistorialMembresiaServiceImpl(HistorialMembresiaRepository repository) {
        this.repository = repository;
    }

    @Override
    public List<HistorialMembresia> findAll() {
        return repository.findAll();
    }

    @Override
    public HistorialMembresia findById(Integer id) {
        return repository.findById(id).orElseThrow(() -> new RuntimeException("Historial membresia no encontrado"));
    }

    @Override
    public List<HistorialMembresia> findByMiembroId(Integer id) {
        return repository.findByMiembro_IdOrderByIdDesc(id);
    }

    @Override
    public List<HistorialMembresia> search(Integer idMiembro, String keyword) {
        if (idMiembro == null) {
            throw new IllegalArgumentException("El id del miembro no puede ser nulo");
        }

        Specification<HistorialMembresia> spec = (root, query, cb) -> {
            // Filtro obligatorio por id del miembro
            Predicate predicateBase = cb.equal(root.get("miembro").get("id"), idMiembro);

            if (keyword == null || keyword.trim().isEmpty()) {
                return predicateBase;
            }

            String pattern = "%" + keyword.toLowerCase() + "%";

            // JOIN con la membresía
            Join<HistorialMembresia, Membresia> membresiaJoin = root.join("membresia", JoinType.LEFT);
            Expression<String> nombreMembresiaExpr = cb.lower(membresiaJoin.get("nombre"));

            // Para atributos de tipo String se hace directamente.
            Expression<String> descripcionExpr = cb.lower(root.get("descripcion"));
            // Para las fechas, podemos usar una función de formateo. Por ejemplo, en MariaDB se puede usar DATE_FORMAT.
            Expression<String> fechaCambioExpr = cb.function("DATE_FORMAT", String.class, root.get("fechaCambio"), cb.literal("%Y-%m-%d"));

            Predicate keywordPredicate = cb.or(
                    cb.like(nombreMembresiaExpr, pattern),
                    cb.like(descripcionExpr, pattern),
                    cb.like(fechaCambioExpr, pattern)
            );
            return cb.and(predicateBase, keywordPredicate);
        };

        return repository.findAll(spec, Sort.by(Sort.Direction.DESC, "id"));
    }

    @Override
    public void save(HistorialMembresia historialMembresia) {
        repository.save(historialMembresia);
    }
}
