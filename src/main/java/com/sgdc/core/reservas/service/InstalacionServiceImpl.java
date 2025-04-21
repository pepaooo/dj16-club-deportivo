package com.sgdc.core.reservas.service;

import com.sgdc.core.reservas.domain.Instalacion;
import com.sgdc.core.reservas.domain.dto.InstalacionDTO;
import com.sgdc.core.reservas.repository.InstalacionRepository;
import jakarta.persistence.criteria.Expression;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class InstalacionServiceImpl implements InstalacionService {

    private final InstalacionRepository repository;

    public InstalacionServiceImpl(InstalacionRepository repository) {
        this.repository = repository;
    }

    @Override
    public List<Instalacion> findAll() {
        return repository.findAll();
    }

    @Override
    public Optional<Instalacion> findById(Integer id) {
        return repository.findById(id);
    }

    @Override
    public List<Instalacion> search(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return repository.findAll();
        }
        String pattern = "%" + keyword.toLowerCase() + "%";

        Specification<Instalacion> spec = (root, query, cb) -> {
            // Para atributos de tipo String se hace directamente.
            Expression<String> nombreExpr = cb.lower(root.get("nombre"));
            //Expression<String> descripcionExpr = cb.lower(root.get("descripcion"));
            // Para campos tipo TEXT se deberá usar la siguiente expresión, la expresión de arriba no es funcional.
            Expression<String> descripcionExpr = cb.function("concat", String.class, root.get("descripcion"), cb.literal(""));
            Expression<String> estadoExpr = cb.lower(root.get("estado"));

            return cb.or(
                    cb.like(nombreExpr, pattern),
                    cb.like(descripcionExpr, pattern),
                    cb.like(estadoExpr, pattern)
            );
        };

        return repository.findAll(spec);
    }

    @Override
    public List<InstalacionDTO> findByMembresiaId(Integer mid) {
        return repository.findByMembresiaId(mid)
                .stream()
                .map(i -> new InstalacionDTO(i.getId(), i.getNombre()))
                .toList();
    }

    @Override
    public void save(Instalacion instalacion) {
        repository.save(instalacion);
    }
}
