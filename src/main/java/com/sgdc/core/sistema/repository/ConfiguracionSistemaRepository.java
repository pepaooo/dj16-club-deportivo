package com.sgdc.core.sistema.repository;

import com.sgdc.core.sistema.domain.ConfiguracionSistema;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;
import java.util.Optional;

public interface ConfiguracionSistemaRepository
        extends JpaRepository<ConfiguracionSistema, Integer>, JpaSpecificationExecutor<ConfiguracionSistema> {
    // Listas de todas las configuraciones ordenadas de forma descendente por ID
    List<ConfiguracionSistema> findAllByOrderByIdDesc();

    // Este metodo se usa para buscar por el parámetro.
    Optional<ConfiguracionSistema> findByParametro(String parametro);
}
