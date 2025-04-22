package com.sgdc.core.membresia.service;

import com.sgdc.core.membresia.domain.Beneficio;
import com.sgdc.core.membresia.domain.Membresia;
import com.sgdc.core.membresia.domain.dto.BeneficioInfo;
import com.sgdc.core.membresia.domain.dto.InstalacionInfo;
import com.sgdc.core.membresia.domain.dto.MembresiaDTO;
import com.sgdc.core.membresia.repository.BeneficioRepository;
import com.sgdc.core.membresia.repository.MembresiaRepository;
import com.sgdc.core.reservas.domain.Instalacion;
import com.sgdc.core.reservas.repository.InstalacionRepository;
import jakarta.persistence.criteria.Expression;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class MembresiaServiceImpl implements MembresiaService {

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
                            .map(i -> new InstalacionInfo(i.getNombre(), i.getDescripcion()))
                            .collect(Collectors.toList()));
                    return dto;

                })
                .orElseThrow(() -> new RuntimeException("No se encontró la membresía con ID: " + id));

    }

    @Override
    public List<Membresia> search(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return repository.findAll();
        }
        String pattern = "%" + keyword.toLowerCase() + "%";

        Specification<Membresia> spec = (root, query, cb) -> {
            // Para atributos de tipo String se hace directamente.
            Expression<String> nombreExpr = cb.lower(root.get("nombre"));
            Expression<String> estatusExpr = cb.lower(root.get("estatus"));
            // Para atributos numéricos, se puede usar una función para convertir a cadena, si lo soporta el dialecto
            Expression<String> tarifaExpr = cb.function("str", String.class, root.get("tarifa"));
            Expression<String> duracionExpr = cb.function("str", String.class, root.get("duracionDias"));

            return cb.or(
                    cb.like(nombreExpr, pattern),
                    cb.like(estatusExpr, pattern),
                    cb.like(cb.lower(tarifaExpr), pattern),
                    cb.like(cb.lower(duracionExpr), pattern)
            );
        };

        return repository.findAll(spec);
    }

    @Override
    public void save(MembresiaDTO dto) {
        Membresia membresia = this.toEntity(dto);
        // Si no se especifica estatus, se asigna "Activo" por defecto
        if (membresia.getEstatus() == null || membresia.getEstatus().isEmpty()) {
            membresia.setEstatus("Activo");
        }
        repository.save(membresia);
    }

    @Override
    public Optional<Membresia> activateMembresia(Integer id) {
        Optional<Membresia> membresia = repository.findById(id);
        if (membresia.isPresent()) {
            Membresia m = membresia.get();
            m.setEstatus("Activo");
            return Optional.of(repository.save(m));
        }
        return Optional.empty();
    }

    @Override
    public Optional<Membresia> deactivateMembresia(Integer id) {
        Optional<Membresia> membresia = repository.findById(id);
        if (membresia.isPresent()) {
            Membresia m = membresia.get();
            m.setEstatus("Inactivo");
            return Optional.of(repository.save(m));
        }
        return Optional.empty();
    }

    public Membresia toEntity(MembresiaDTO dto) {
        Membresia m = new Membresia();
        m.setNombre(dto.getNombre());
        m.setDescripcion(dto.getDescripcion());
        m.setTarifa(dto.getTarifa());
        m.setDuracionDias(dto.getDuracionDias());
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
        return dto;
    }

}
