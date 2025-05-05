package com.sgdc.core.config.service;

import com.sgdc.core.config.domain.ConfiguracionSistema;

import java.util.List;

public interface ConfiguracionSistemaService {

    boolean isNotificacionesActivadas();

    List<Integer> getNotificationPeriods();

    List<ConfiguracionSistema> search(String keyword);

    ConfiguracionSistema save(ConfiguracionSistema configuracionSistema);

    void update(Integer id, String valor);
}
