package com.sgdc.core.reservas.controller;

import com.sgdc.core.reservas.domain.EstadoInstalacion;
import com.sgdc.core.reservas.domain.Instalacion;
import com.sgdc.core.reservas.service.InstalacionService;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("instalaciones")
public class InstalacionController {

    private static final Logger log = LoggerFactory.getLogger(InstalacionController.class);

    private final InstalacionService instalacionService;

    public InstalacionController(InstalacionService instalacionService) {
        this.instalacionService = instalacionService;
    }

    @GetMapping
    public String inicio(@RequestParam(value = "q", required = false) String keyword, Model model) {
        List<Instalacion> instalaciones = instalacionService.search(keyword);
        // Agregar al modelo los resultados y también el término de búsqueda para que el input lo retenga.
        model.addAttribute("instalaciones", instalaciones);
        model.addAttribute("q", keyword);

        // Otros atributos, por ejemplo para los resúmenes:
        //List<Instalacion> allInstalaciones = instalacionService.findAll();
        model.addAttribute("totalInstalaciones", instalaciones.size());
        // Para calcular instalaciones activas/inactivas, podrías hacer filtrados o consultar en el servicio.
        long disponibles = instalaciones.stream().filter(m -> EstadoInstalacion.DISPONIBLE.getLabel().equalsIgnoreCase(m.getEstado())).count();
        model.addAttribute("instalacionesDisponibles", disponibles);
        long mantenimiento = instalaciones.stream().filter(m -> EstadoInstalacion.EN_MANTENIMIENTO.getLabel().equalsIgnoreCase(m.getEstado())).count();
        model.addAttribute("instalacionesEnMantenimiento", mantenimiento);
        long cerradas = instalaciones.stream().filter(m -> EstadoInstalacion.CERRADA.getLabel().equalsIgnoreCase(m.getEstado())).count();
        model.addAttribute("instalacionesCerradas", cerradas);
        
        return "instalaciones/inicio";
    }

    @GetMapping("get")
    public String getInstalacion(@RequestParam(value = "id") Integer idInstalacion, Model model) {
        Optional<Instalacion> instalacion = instalacionService.findById(idInstalacion);
        model.addAttribute("instalacion", instalacion.orElse(new Instalacion()));
        return "instalaciones/ver-instalacion";
    }

    @GetMapping("new")
    public String newInstalacion(Model model) {
        model.addAttribute("instalacion", new Instalacion());
        return "instalaciones/nueva-instalacion";
    }

    @PostMapping("create-instalacion")
    public String guardarInstalacion(@Valid Instalacion instalacion, BindingResult bindingResult, RedirectAttributes redirectAttributes, Model model) {
        if (bindingResult.hasErrors()) {
            for (ObjectError error : bindingResult.getAllErrors()) {
                log.error("Ocurrió un error: {}", error.getDefaultMessage());
            }
            return "instalaciones/nueva-instalacion";
        }

        try {
            instalacion.setEstado(EstadoInstalacion.DISPONIBLE.getLabel());
            log.info("Instalacion a guardar: {}", instalacion);
            instalacionService.save(instalacion);
        } catch (DataIntegrityViolationException e) {
            log.error("Error de integridad de datos: {}", e.getMessage());
            bindingResult.rejectValue("nombre", "nombre", "El nombre de la instalación ya existe. Por favor, use otro.");
            return "instalaciones/nueva-instalacion";
        }

        redirectAttributes.addFlashAttribute("exito", "La instalación se ha guardado correctamente");
        return "redirect:/instalaciones";
    }

    @GetMapping("change")
    public String changeInstalacion(@RequestParam(value = "id") Integer idInstalacion, Model model) {
        Optional<Instalacion> instalacion = instalacionService.findById(idInstalacion);
        model.addAttribute("instalacion", instalacion.orElse(new Instalacion()));
        // Agregar los posibles estados al modelo
        model.addAttribute("estados", EstadoInstalacion.values());
        return "instalaciones/editar-instalacion";
    }

    @PostMapping("change-instalacion")
    public String changeInstalacion(@Valid Instalacion instalacion, BindingResult bindingResult, RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            for (ObjectError error : bindingResult.getAllErrors()) {
                log.error("Ocurrió un error: {}", error.getDefaultMessage());
            }
            return "instalaciones/editar-instalacion";
        }

        try {
            log.info("Membresía a guardar: {}", instalacion);
            instalacionService.save(instalacion);
        } catch (DataIntegrityViolationException e) {
            log.error("Error de integridad de datos: {}", e.getMessage());
            bindingResult.rejectValue("nombre", "nombre", "El nombre de la membresía ya existe. Por favor, use otro.");
            return "instalaciones/editar-instalacion";
        }

        redirectAttributes.addFlashAttribute("exito", "La membresía se ha guardado correctamente");
        return "redirect:/instalaciones";
    }

}
