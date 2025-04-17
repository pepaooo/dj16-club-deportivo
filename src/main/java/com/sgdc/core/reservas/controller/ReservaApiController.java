package com.sgdc.core.reservas.controller;

import com.sgdc.core.reservas.domain.Reserva;
import com.sgdc.core.reservas.service.ReservaService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/reservas")
public class ReservaApiController {

    private final ReservaService reservaService;

    public ReservaApiController(ReservaService reservaService) {
        this.reservaService = reservaService;
    }

    @GetMapping
    public List<Map<String,Object>> listaEventos() {
        List<Reserva> reservas = reservaService.findAll();
        List<Map<String,Object>> eventos = new ArrayList<>();

        for (Reserva r : reservas) {
            Map<String,Object> e = new HashMap<>();
            e.put("id",    r.getId());
            e.put("title", r.getMiembro().getNombre() + " – " + r.getInstalacion().getNombre());
            e.put("start", r.getFechaHoraInicio().toString());
            e.put("end",   r.getFechaHoraFin().toString());
            eventos.add(e);
        }

        return eventos;
    }
}

