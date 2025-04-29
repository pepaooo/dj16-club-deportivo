package com.sgdc.core.config.repository;

import com.sgdc.core.config.domain.ConfiguracionSistema;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ConfiguracionSistemaRepository
        extends JpaRepository<ConfiguracionSistema, Integer> {
    Optional<ConfiguracionSistema> findByParametro(String parametro);
}
