package com.sgdc.core.config.service;

import com.sgdc.core.config.repository.ConfiguracionSistemaRepository;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ConfiguracionSistemaServiceImpl implements ConfiguracionSistemaService {

    private final ConfiguracionSistemaRepository repository;

    public ConfiguracionSistemaServiceImpl(ConfiguracionSistemaRepository repository) {
        this.repository = repository;
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

    private List<Integer> parsePeriods(String csv) {
        return Arrays.stream(csv.split("\\s*,\\s*"))
                .map(Integer::valueOf)
                .distinct()
                .sorted()
                .collect(Collectors.toList());
    }
}
