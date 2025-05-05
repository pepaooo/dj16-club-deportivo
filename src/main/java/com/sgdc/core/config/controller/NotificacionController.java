package com.sgdc.core.config.controller;

import com.sgdc.core.config.domain.Notificacion;
import com.sgdc.core.config.domain.dto.NotificacionSearchDTO;
import com.sgdc.core.config.service.NotificacionEnvioService;
import com.sgdc.core.config.service.NotificacionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("notificaciones")
public class NotificacionController {

    private static final Logger log = LoggerFactory.getLogger(NotificacionController.class);

    private final NotificacionService notificacionService;

    private final NotificacionEnvioService notificacionEnvioService;

    public NotificacionController(NotificacionService notificacionService,NotificacionEnvioService notificacionEnvioService) {
        this.notificacionService = notificacionService;
        this.notificacionEnvioService = notificacionEnvioService;
    }

    @GetMapping
    public String inicio(@RequestParam(value = "q", required = false) String keyword, Model model) {
        List<NotificacionSearchDTO> notificaciones = notificacionService.search(keyword);
        // Agregar al modelo los resultados y también el término de búsqueda para que el input lo retenga.
        model.addAttribute("notificaciones", notificaciones);
        model.addAttribute("q", keyword);

        // Otros atributos, por ejemplo para los resúmenes:
        model.addAttribute("totalNotificaciones", notificaciones.size());
        // Para calcular notificaciones con estatus Pendiente|Enviada|Error
        long pendientes = notificaciones.stream().filter(n -> n.getEstatus().equals("Pendiente")).count();
        long enviadas = notificaciones.stream().filter(n -> n.getEstatus().equals("Enviada")).count();
        long error = notificaciones.stream().filter(n -> n.getEstatus().equals("Error")).count();
        model.addAttribute("notificacionesPendientes", pendientes);
        model.addAttribute("notificacionesEnviadas", enviadas);
        model.addAttribute("notificacionesError", error);

        return "notificaciones/inicio";
    }

    @GetMapping("get")
    public String getNotificacion(@RequestParam(value = "id") Integer idNotificacion, Model model) {
        Notificacion notificacion = notificacionService.findById(idNotificacion);
        model.addAttribute("notificacion", notificacion);
        return "notificaciones/ver-notificacion";
    }

    @PostMapping("reenviar")
    public String reenviarNotificacion(@RequestParam(value = "id") Integer idNotificacion, RedirectAttributes redirectAttributes) {
        notificacionEnvioService.enviarNotificacion(idNotificacion);
        redirectAttributes.addFlashAttribute("exito", "La notificación se ha reenviado correctamente");
        return "redirect:/notificaciones";
    }

}
