package com.sgdc.core.reservas.repository;

import com.sgdc.core.reservas.domain.Reserva;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface ReservaRepository extends JpaRepository<Reserva, Integer>, JpaSpecificationExecutor<Reserva> {
    // Listas de todas las reservas ordenadas de forma descendente por ID
    List<Reserva> findAllByOrderByIdDesc();
}
