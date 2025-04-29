package com.sgdc.core.config.service;

import java.util.List;

public interface ConfiguracionSistemaService {

    boolean isNotificacionesActivadas();

    List<Integer> getNotificationPeriods();

}
