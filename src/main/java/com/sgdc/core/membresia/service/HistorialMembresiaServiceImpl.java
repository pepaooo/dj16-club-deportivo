package com.sgdc.core.membresia.service;

import com.sgdc.core.membresia.domain.HistorialMembresia;
import com.sgdc.core.membresia.repository.HistorialMembresiaRepository;
import jakarta.persistence.criteria.Expression;
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
    public List<HistorialMembresia> search(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return repository.findAll();
        }
        String pattern = "%" + keyword.toLowerCase() + "%";

        Specification<HistorialMembresia> spec = (root, query, cb) -> {
            // Para atributos de tipo String se hace directamente.
            Expression<String> descripcionExpr = cb.lower(root.get("descripcion"));
//            Expression<String> estatusExpr = cb.lower(root.get("estatus"));
//            // Para atributos numéricos, se puede usar una función para convertir a cadena, si lo soporta el dialecto
//            Expression<String> tarifaExpr = cb.function("str", String.class, root.get("tarifa"));
//            Expression<String> duracionExpr = cb.function("str", String.class, root.get("duracionDias"));

            return cb.or(
                    cb.like(descripcionExpr, pattern)
//                    cb.like(estatusExpr, pattern),
//                    cb.like(cb.lower(tarifaExpr), pattern),
//                    cb.like(cb.lower(duracionExpr), pattern)
            );
        };

        return repository.findAll(spec);
    }

    @Override
    public void save(HistorialMembresia historialMembresia) {
        repository.save(historialMembresia);
    }
}
