package com.sgdc.core.miembro.service;

import com.sgdc.core.miembro.domain.Miembro;
import com.sgdc.core.miembro.repository.MiembroRepository;
import jakarta.persistence.criteria.Expression;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class MiembroServiceImpl implements MiembroService {

    private final MiembroRepository repository;

    public MiembroServiceImpl(MiembroRepository repository) {
        this.repository = repository;
    }

    @Override
    public List<Miembro> findAll() {
        return repository.findAll();
    }

    @Override
    public Optional<Miembro> findById(Integer id) {
        return repository.findById(id);
    }

    @Override
    public List<Miembro> search(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return repository.findAll();
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

        return repository.findAll(spec);
    }

    @Override
    public void save(Miembro miembro) {
        repository.save(miembro);
    }
}
