/*
 * Copyright (C) 2025 the original author or authors.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package com.sgdc.core.reservas.service;

import com.sgdc.core.auditoria.aop.Auditable;
import com.sgdc.core.reservas.domain.EstadoInstalacion;
import com.sgdc.core.reservas.domain.Instalacion;
import com.sgdc.core.reservas.domain.dto.InstalacionDTO;
import com.sgdc.core.reservas.repository.InstalacionRepository;
import jakarta.persistence.criteria.Expression;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class InstalacionServiceImpl implements InstalacionService {

    private static final Logger log = LoggerFactory.getLogger(InstalacionServiceImpl.class);

    private final InstalacionRepository repository;

    public InstalacionServiceImpl(InstalacionRepository repository) {
        this.repository = repository;
    }

    @Override
    public List<Instalacion> findAll() {
        return repository.findAllByOrderByIdDesc();
    }

    @Override
    public Optional<Instalacion> findById(Integer id) {
        return repository.findById(id);
    }

    @Override
    public List<Instalacion> search(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return repository.findAllByOrderByIdDesc();
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
        // Ordenar por ID de forma descendente.
        return repository.findAll(spec, Sort.by(Sort.Direction.DESC, "id"));
    }

    @Override
    public List<InstalacionDTO> findByMembresiaId(Integer mid) {
        return repository.findByMembresiaId(mid)
                .stream()
                .map(i -> new InstalacionDTO(i.getId(), i.getNombre(), i.getEstado()))
                .toList();
    }

    @Auditable(
            tipoAccion = "CREATE",
            tabla = "instalacion",
            entidadId = "#result.id",
            descripcion = "'Creación de instalación '+#result.nombre + ' con descripción: '+#result.descripcion"
    )
    @Override
    public Instalacion save(Instalacion instalacion) {
        instalacion.setEstado(EstadoInstalacion.DISPONIBLE.getLabel());
        log.info("Agregando nueva instalación: {}", instalacion);
        return repository.save(instalacion);
    }

    @Auditable(
            tipoAccion = "UPDATE",
            tabla = "instalacion",
            entidadId = "#result.id",
            descripcion = "'Actualización de instalación '+#result.nombre + ' con descripción: '+#result.descripcion"
    )
    @Override
    public Instalacion update(Instalacion instalacion) {
        log.info("Actualizando instalación: {}", instalacion);
        return repository.save(instalacion);
    }
}
