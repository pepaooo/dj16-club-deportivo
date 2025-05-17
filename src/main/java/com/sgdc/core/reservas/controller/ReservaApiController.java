/*
 * Copyright (C) 2025 the original author or authors.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

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
            e.put("title", r.getMiembro().getNombre() + " " + r.getMiembro().getApellidoPaterno() + " " + r.getMiembro().getApellidoMaterno() + " – " + r.getInstalacion().getNombre());
            e.put("start", r.getFechaHoraInicio().toString());
            e.put("end",   r.getFechaHoraFin().toString());
            eventos.add(e);
        }

        return eventos;
    }
}

