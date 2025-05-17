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

package com.sgdc.core.actividades.controller;

import com.sgdc.core.actividades.model.Actividad;
import com.sgdc.core.actividades.service.ActividadService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@RequestMapping("auditoria/actividades")
public class ActividadController {

    private static final Logger log = LoggerFactory.getLogger(ActividadController.class);

    private final ActividadService actividadService;

    public ActividadController(ActividadService actividadService) {
        this.actividadService = actividadService;
    }

    @GetMapping
    public String inicio(@RequestParam(value = "q", required = false) String keyword, Model model) {
        List<Actividad> actividades = actividadService.search(keyword);
        // Agregar al modelo los resultados y también el término de búsqueda para que el input lo retenga.
        model.addAttribute("actividades", actividades);
        model.addAttribute("q", keyword);

        return "actividades/inicio";
    }

    @GetMapping("get")
    public String getActividad(@RequestParam(value = "id") Integer idActividad, Model model) {
        Actividad actividad = actividadService.findById(idActividad);
        model.addAttribute("actividad", actividad);
        return "actividades/ver-actividad";
    }

}
