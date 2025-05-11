package com.sgdc.core.sistema.service;

import com.sgdc.core.sistema.domain.ConfiguracionSistema;

import java.util.List;

public interface ConfiguracionSistemaService {

    boolean isNotificacionesActivadas();

    List<Integer> getNotificationPeriods();

    List<ConfiguracionSistema> search(String keyword);

    ConfiguracionSistema save(ConfiguracionSistema configuracionSistema);

    ConfiguracionSistema update(Integer id, String valor);
}
