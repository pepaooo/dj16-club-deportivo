package com.sgdc.core.config.service;

import com.sgdc.core.config.domain.ConfiguracionSistema;
import com.sgdc.core.config.repository.ConfiguracionSistemaRepository;
import com.sgdc.core.config.validator.ConfigValidationService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.criteria.Expression;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ConfiguracionSistemaServiceImpl implements ConfiguracionSistemaService {

    private static final Logger log = LoggerFactory.getLogger(ConfiguracionSistemaServiceImpl.class);

    private final ConfiguracionSistemaRepository repository;
    private final ConfigValidationService configValidationService;

    public ConfiguracionSistemaServiceImpl(ConfiguracionSistemaRepository repository, ConfigValidationService configValidationService) {
        this.repository = repository;
        this.configValidationService = configValidationService;
    }

    /**
     * ¿Las notificaciones están activadas?
     */
    @Override
    public boolean isNotificacionesActivadas() {
        return repository.findByParametro("notificaciones_activadas")
                .map(cfg -> Boolean.parseBoolean(cfg.getValor()))
                .orElse(false);
    }

    /**
     * Lista de días antes del vencimiento (p.ej. [5,3,1])
     */
    @Override
    public List<Integer> getNotificationPeriods() {
        return repository.findByParametro("notificacion_periodo")
                .map(cfg -> parsePeriods(cfg.getValor()))
                .orElse(List.of());
    }

    @Override
    public List<ConfiguracionSistema> search(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return repository.findAllByOrderByIdDesc();
        }
        String pattern = "%" + keyword.toLowerCase() + "%";

        Specification<ConfiguracionSistema> spec = (root, query, cb) -> {
            // Para atributos de tipo String se hace directamente.
            Expression<String> parametroExpr = cb.lower(root.get("parametro"));
            Expression<String> valorExpr = cb.lower(root.get("valor"));
            Expression<String> descripcionExpr = cb.lower(root.get("descripcion"));

            return cb.or(
                    cb.like(parametroExpr, pattern),
                    cb.like(valorExpr, pattern),
                    cb.like(descripcionExpr, pattern)
            );
        };

        return repository.findAll(spec, Sort.by(Sort.Direction.DESC, "id"));
    }

    @Override
    public ConfiguracionSistema save(ConfiguracionSistema configuracionSistema) {
        log.debug("Saving configuracionSistema: {}", configuracionSistema);
        return repository.save(configuracionSistema);
    }

    @Override
    public void update(Integer id, String valor) {
        ConfiguracionSistema configuracionSistema = repository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("ConfiguracionSistema not found with id: " + id));
        // Validar el valor según el tipo de configuración
        configValidationService.validate(configuracionSistema.getParametro(), valor);
        // Actualizar el valor
        configuracionSistema.setValor(valor);
        repository.save(configuracionSistema);
    }

    private List<Integer> parsePeriods(String csv) {
        return Arrays.stream(csv.split("\\s*,\\s*"))
                .map(Integer::valueOf)
                .distinct()
                .sorted()
                .collect(Collectors.toList());
    }
}
