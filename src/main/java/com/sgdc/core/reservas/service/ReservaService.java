package com.sgdc.core.reservas.service;

import com.sgdc.core.reservas.domain.Reserva;

import java.util.List;
import java.util.Optional;

public interface ReservaService {

    List<Reserva> findAll();

    Optional<Reserva> findById(Integer id);

    List<Reserva> search(String keyword);

    void save(Reserva reserva);

}
