package com.sgdc.core.config;

import com.sgdc.core.membresia.domain.Beneficio;
import com.sgdc.core.membresia.domain.Membresia;
import com.sgdc.core.membresia.domain.dto.MembresiaDTO;
import com.sgdc.core.membresia.repository.BeneficioRepository;
import com.sgdc.core.reservas.domain.Instalacion;
import com.sgdc.core.reservas.repository.InstalacionRepository;
import org.modelmapper.ModelMapper;
import org.modelmapper.TypeMap;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Configuration
public class AppConfig {

    @Bean
    public ModelMapper modelMapper() {
        return new ModelMapper();
    }

}
