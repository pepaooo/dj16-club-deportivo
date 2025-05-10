package com.sgdc.core.actividades.service;

import com.sgdc.core.actividades.model.Actividad;
import com.sgdc.core.actividades.repository.ActividadRepository;
import com.sgdc.core.reservas.domain.Reserva;
import com.sgdc.core.usuarios.domain.Usuario;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ActividadServiceImpl implements ActividadService {

    private final ActividadRepository repository;

    public ActividadServiceImpl(ActividadRepository repository) {
        this.repository = repository;
    }

    @Override
    public List<Actividad> findAll() {
        return repository.findAll();
    }

    @Override
    public Actividad findById(Integer id) {
        return repository.findById(id).orElseThrow(() -> new EntityNotFoundException("Actividad no encontrada con id: " + id));
    }

    @Override
    public List<Actividad> search(String keyword) {

        if (keyword == null || keyword.trim().isEmpty()) {
            return repository.findAllByOrderByIdDesc();
        }
        String pattern = "%" + keyword.toLowerCase() + "%";

        Specification<Actividad> spec = (root, query, cb) -> {
            // JOIN con usuario
            Join<Reserva, Usuario> usuarioJoin = root.join("usuario", JoinType.LEFT);
            Predicate pNombreUsuario = cb.like(cb.lower(usuarioJoin.get("nombre")), pattern);

            // Columnas de la tabla Actividad
            Predicate pTipoAccion = cb.like(cb.lower(root.get("tipoAccion")), pattern);
            Predicate pDescripcion = cb.like(cb.lower(root.get("descripcion")), pattern);

            Predicate pFechaHora = cb.like(
                    cb.function("DATE_FORMAT", String.class, root.get("fechaHora"), cb.literal("%Y-%m-%d %H:%i")),
                    pattern
            );

            // Unimos todos los OR
            return cb.or(
                    pNombreUsuario,
                    pTipoAccion,
                    pDescripcion,
                    pFechaHora
            );

        };
        return repository.findAll(spec, Sort.by(Sort.Direction.DESC, "fechaHora"));
    }
}
