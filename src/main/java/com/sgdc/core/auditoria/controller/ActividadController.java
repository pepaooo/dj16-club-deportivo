package com.sgdc.core.auditoria.controller;

import com.sgdc.core.auditoria.model.Actividad;
import com.sgdc.core.auditoria.service.ActividadService;
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
