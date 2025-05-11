package com.sgdc.core.sistema.controller;

import com.sgdc.core.sistema.domain.ConfiguracionSistema;
import com.sgdc.core.sistema.service.ConfiguracionSistemaService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("configuracion")
public class ConfiguracionSistemaController {

    private static final Logger log = LoggerFactory.getLogger(ConfiguracionSistemaController.class);

    private final ConfiguracionSistemaService configuracionSistemaService;

    public ConfiguracionSistemaController(ConfiguracionSistemaService configuracionSistemaService) {
        this.configuracionSistemaService = configuracionSistemaService;
    }

    @GetMapping
    public String inicio(@RequestParam(value = "q", required = false) String keyword, Model model) {
        List<ConfiguracionSistema> configuraciones = configuracionSistemaService.search(keyword);
        // Agregar al modelo los resultados y también el término de búsqueda para que el input lo retenga.
        model.addAttribute("configuraciones", configuraciones);
        model.addAttribute("q", keyword);

        return "configuraciones/inicio";
    }

    @PostMapping("/actualizar/{id}")
    public String guardar(@PathVariable("id") Integer id,
                          @RequestParam("valor") String valor,
                          RedirectAttributes redirectAttributes) {
        try {
            log.info("Guardando configuracion por id {}", id);
            configuracionSistemaService.update(id, valor);
            redirectAttributes.addFlashAttribute("exito", "Configuración guardada correctamente del parámetro con el identificador " + id);
        } catch (IllegalStateException | IllegalArgumentException e) {
            redirectAttributes.addFlashAttribute("error", "Error al guardar la configuración del parámetro con el identificador " + id + ". " + e.getMessage());
        }
        return "redirect:/configuracion";
    }

}
