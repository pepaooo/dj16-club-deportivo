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

package com.sgdc.core.membresia.service;

import com.sgdc.core.auditoria.aop.Auditable;
import com.sgdc.core.membresia.domain.Beneficio;
import com.sgdc.core.membresia.repository.BeneficioRepository;
import jakarta.persistence.criteria.Expression;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class BeneficioServiceImpl implements BeneficioService {

    private static final Logger log = LoggerFactory.getLogger(BeneficioServiceImpl.class);

    private final BeneficioRepository repository;

    public BeneficioServiceImpl(BeneficioRepository repository) {
        this.repository = repository;
    }

    @Override
    public List<Beneficio> findAll() {
        return repository.findAll();
    }

    @Override
    public List<Beneficio> findByMembresia(Integer idMembresia) {
        return repository.findAllByMembresiaId(idMembresia);
    }

    @Override
    public Optional<Beneficio> findById(Integer id) {
        return repository.findById(id);
    }

    @Override
    public List<Beneficio> search(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return repository.findAllByOrderByIdDesc();
        }
        String pattern = "%" + keyword.toLowerCase() + "%";

        Specification<Beneficio> spec = (root, query, cb) -> {
            // Para atributos de tipo String se hace directamente.
            Expression<String> nombreExpr = cb.lower(root.get("nombre"));
            Expression<String> estatusExpr = cb.lower(root.get("descripcion"));

            return cb.or(
                    cb.like(nombreExpr, pattern),
                    cb.like(estatusExpr, pattern)
            );
        };

        return repository.findAll(spec, Sort.by("id").descending());
    }

    @Auditable(
            tipoAccion = "CREATE",
            tabla = "beneficio",
            entidadId = "#result.id",
            descripcion = "'Creación de beneficio '+#result.nombre + ' con descripción: '+#result.descripcion"
    )
    @Override
    public Beneficio save(Beneficio beneficio) {
        log.info("Agregando nuevo beneficio: {}", beneficio);
        return repository.save(beneficio);
    }

    @Auditable(
            tipoAccion = "UPDATE",
            tabla = "beneficio",
            entidadId = "#result.id",
            descripcion = "'Actualización de beneficio '+#result.nombre + ' con descripción: '+#result.descripcion"
    )
    @Override
    public Beneficio update(Beneficio beneficio) {
        log.info("Actualizando beneficio: {}", beneficio);
        return repository.save(beneficio);
    }

    @Auditable(
            tipoAccion = "DELETE",
            tabla = "beneficio",
            entidadId = "#id",
            descripcion = "'Eliminación de beneficio con ID: '+#id"
    )
    @Override
    public void delete(Integer id) {
        log.info("Eliminando beneficio con ID: {}", id);
        repository.deleteById(id);
    }
}
