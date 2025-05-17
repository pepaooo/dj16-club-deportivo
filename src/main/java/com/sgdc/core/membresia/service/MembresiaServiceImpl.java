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
import com.sgdc.core.membresia.domain.Membresia;
import com.sgdc.core.membresia.domain.dto.BeneficioInfo;
import com.sgdc.core.membresia.domain.dto.InstalacionInfo;
import com.sgdc.core.membresia.domain.dto.MembresiaDTO;
import com.sgdc.core.membresia.repository.BeneficioRepository;
import com.sgdc.core.membresia.repository.MembresiaRepository;
import com.sgdc.core.reservas.domain.Instalacion;
import com.sgdc.core.reservas.repository.InstalacionRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.criteria.Expression;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class MembresiaServiceImpl implements MembresiaService {

    private static final Logger log = LoggerFactory.getLogger(MembresiaServiceImpl.class);

    private final MembresiaRepository repository;

    private final BeneficioRepository beneficioRepo;

    private final InstalacionRepository instalacionRepo;

    public MembresiaServiceImpl(MembresiaRepository repository, BeneficioRepository beneficioRepo, InstalacionRepository instalacionRepo) {
        this.repository = repository;
        this.beneficioRepo = beneficioRepo;
        this.instalacionRepo = instalacionRepo;
    }

    @Override
    public List<Membresia> findAll() {
        return repository.findAll();
    }

    @Override
    public List<Membresia> findActive() {
        return repository.findByEstatus("Activo");
    }

    @Override
    public MembresiaDTO findById(Integer id) {
        return repository.findById(id)
                .map(memb -> {
                    MembresiaDTO dto = this.toDTO(memb);
                    // Asignar beneficios
                    // mapeo manual de las colecciones
                    dto.setBeneficios(memb.getBeneficios()
                            .stream()
                            .map(b -> new BeneficioInfo(b.getNombre(), b.getDescripcion()))
                            .collect(Collectors.toList()));
                    dto.setInstalaciones(memb.getInstalaciones()
                            .stream()
                            .map(i -> new InstalacionInfo(i.getNombre(), i.getDescripcion(), i.getEstado()))
                            .collect(Collectors.toList()));
                    return dto;

                })
                .orElseThrow(() -> new RuntimeException("No se encontró la membresía con ID: " + id));

    }

    @Override
    public List<Membresia> search(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return repository.findAllByOrderByIdDesc();
        }
        String pattern = "%" + keyword.toLowerCase() + "%";

        Specification<Membresia> spec = (root, query, cb) -> {
            // Para atributos de tipo String se hace directamente.
            Expression<String> nombreExpr = cb.lower(root.get("nombre"));
            Expression<String> descripcionExpr = cb.lower(root.get("descripcion"));
            Expression<String> estatusExpr = cb.lower(root.get("estatus"));
            // Para atributos numéricos, se puede usar una función para convertir a cadena, si lo soporta el dialecto
            Expression<String> tarifaExpr = cb.function("str", String.class, root.get("tarifa"));
            Expression<String> duracionExpr = cb.function("str", String.class, root.get("duracionDias"));

            return cb.or(
                    cb.like(nombreExpr, pattern),
                    cb.like(descripcionExpr, pattern),
                    cb.like(estatusExpr, pattern),
                    cb.like(cb.lower(tarifaExpr), pattern),
                    cb.like(cb.lower(duracionExpr), pattern)
            );
        };

        return repository.findAll(spec, Sort.by(Sort.Direction.DESC, "id"));
    }

    @Auditable(
            tipoAccion = "CREATE",
            tabla = "membresia",
            entidadId = "#result.id",
            descripcion = "'Creación de membresía '+#result.nombre + ' con tarifa de '+#result.tarifa + ' y duración de '+#result.duracionDias"
    )
    @Override
    public MembresiaDTO save(MembresiaDTO dto) {
        Membresia membresia = this.toEntity(dto);
        log.info("Agregando nueva membresía: {} {} {} {}",
                membresia.getNombre(), membresia.getTarifa(), membresia.getDuracionDias(), membresia.getEstatus());
        // Si no se especifica estatus, se asigna "Activo" por defecto
        if (membresia.getEstatus() == null || membresia.getEstatus().isEmpty()) {
            membresia.setEstatus("Activo");
        }
        Membresia newMembresia = repository.save(membresia);
        return this.toDTO(newMembresia);
    }

    @Auditable(
            tipoAccion = "UPDATE",
            tabla = "membresia",
            entidadId = "#result.id",
            descripcion = "'Actualización de membresía '+#result.nombre + ' con tarifa de '+#result.tarifa + ' y duración de '+#result.duracionDias"
    )
    @Override
    public MembresiaDTO update(MembresiaDTO dto) {
        Membresia m = this.toEntity(dto);
        log.info("Actualizando membresía: {} {} {} {}",
                m.getId(), m.getNombre(), m.getTarifa(), m.getDuracionDias());
        Membresia updatedMembresia = repository.save(m);
        return this.toDTO(updatedMembresia);
    }

    @Auditable(
            tipoAccion = "UPDATE",
            tabla = "membresia",
            entidadId = "#id",
            descripcion = "'Activación de membresía'"
    )
    @Override
    public void activateMembresia(Integer id) {
        Membresia m = repository.findById(id).orElseThrow(() -> new EntityNotFoundException("No se encontró la membresía con ID: " + id));
        m.setEstatus("Activo");
        repository.save(m);
    }

    @Auditable(
            tipoAccion = "UPDATE",
            tabla = "membresia",
            entidadId = "#id",
            descripcion = "'Inactivación de membresía'"
    )
    @Override
    public void deactivateMembresia(Integer id) {
        Membresia m = repository.findById(id).orElseThrow(() -> new EntityNotFoundException("No se encontró la membresía con ID: " + id));
        m.setEstatus("Inactivo");
        repository.save(m);
    }

    public Membresia toEntity(MembresiaDTO dto) {
        Membresia m = new Membresia();
        m.setId(dto.getId());
        m.setNombre(dto.getNombre());
        m.setTarifa(dto.getTarifa());
        m.setDuracionDias(dto.getDuracionDias());
        m.setEstatus(dto.getEstatus());
        m.setDescripcion(dto.getDescripcion());
        // buscar y asignar beneficios
        List<Beneficio> bens = beneficioRepo.findAllById(dto.getBeneficiosIds());
        m.setBeneficios(new HashSet<>(bens));
        // buscar y asignar instalaciones
        List<Instalacion> ins = instalacionRepo.findAllById(dto.getInstalacionesIds());
        m.setInstalaciones(new HashSet<>(ins));
        return m;
    }

    public MembresiaDTO toDTO(Membresia m) {
        MembresiaDTO dto = new MembresiaDTO();
        dto.setId(m.getId());
        dto.setNombre(m.getNombre());
        dto.setDescripcion(m.getDescripcion());
        dto.setTarifa(m.getTarifa());
        dto.setDuracionDias(m.getDuracionDias());
        dto.setEstatus(m.getEstatus());
        // Asignar IDs de beneficios
        Set<Integer> beneficiosIds = m.getBeneficios().stream()
                .map(Beneficio::getId).collect(Collectors.toSet());
        dto.setBeneficiosIds(beneficiosIds);
        // Asignar IDs de instalaciones
        Set<Integer> instalacionesIds = m.getInstalaciones().stream()
                .map(Instalacion::getId).collect(Collectors.toSet());
        dto.setInstalacionesIds(instalacionesIds);
        // Asignar datos de auditoría
        dto.setCreadoPor(m.getCreadoPor());
        dto.setModificadoPor(m.getModificadoPor());
        dto.setFechaCreacion(m.getFechaCreacion());
        dto.setFechaModificacion(m.getFechaModificacion());
        return dto;
    }

}
