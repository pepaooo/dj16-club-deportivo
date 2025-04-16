package com.sgdc.core.reservas.controller;

import com.sgdc.core.reservas.domain.EstadoReserva;
import com.sgdc.core.reservas.domain.Reserva;
import com.sgdc.core.reservas.service.ReservaService;
import com.sgdc.core.reservas.service.ReservaService;
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
@RequestMapping("reservas")
public class ReservaController {

    private static final Logger log = LoggerFactory.getLogger(ReservaController.class);

    private final ReservaService reservaService;

    public ReservaController(ReservaService reservaService) {
        this.reservaService = reservaService;
    }

    @GetMapping
    public String inicio(@RequestParam(value = "q", required = false) String keyword, Model model) {
        List<Reserva> reservas = reservaService.search(keyword);
        // Agregar al modelo los resultados y también el término de búsqueda para que el input lo retenga.
        model.addAttribute("reservas", reservas);
        model.addAttribute("q", keyword);

        // Otros atributos, por ejemplo para los resúmenes:
        //List<Reserva> allReservaes = reservaService.findAll();
        model.addAttribute("totalReservas", reservas.size());
        // Para calcular reservas activas/inactivas, podrías hacer filtrados o consultar en el servicio.
        long disponibles = reservas.stream().filter(m -> EstadoReserva.PENDIENTE.getLabel().equalsIgnoreCase(m.getEstadoReserva())).count();
        model.addAttribute("reservasPendientes", disponibles);
        long mantenimiento = reservas.stream().filter(m -> EstadoReserva.CONFIRMADA.getLabel().equalsIgnoreCase(m.getEstadoReserva())).count();
        model.addAttribute("reservasConfirmadas", mantenimiento);
        long cerradas = reservas.stream().filter(m -> EstadoReserva.CANCELADA.getLabel().equalsIgnoreCase(m.getEstadoReserva())).count();
        model.addAttribute("reservasCanceladas", cerradas);
        
        return "reservas/inicio";
    }

    @GetMapping("get")
    public String getReserva(@RequestParam(value = "id") Integer idReserva, Model model) {
        Optional<Reserva> reserva = reservaService.findById(idReserva);
        model.addAttribute("reserva", reserva.orElse(new Reserva()));
        return "reservas/ver-reserva";
    }

    @GetMapping("new")
    public String newReserva(Model model) {
        model.addAttribute("reserva", new Reserva());
        return "reservas/nueva-reserva";
    }

    @PostMapping("create-reserva")
    public String guardarReserva(@Valid Reserva reserva, BindingResult bindingResult, RedirectAttributes redirectAttributes, Model model) {
        if (bindingResult.hasErrors()) {
            for (ObjectError error : bindingResult.getAllErrors()) {
                log.error("Ocurrió un error: {}", error.getDefaultMessage());
            }
            return "reservas/nueva-reserva";
        }

        try {
            reserva.setEstadoReserva(EstadoReserva.PENDIENTE.getLabel());
            log.info("Reserva a guardar: {}", reserva);
            reservaService.save(reserva);
        } catch (DataIntegrityViolationException e) {
            log.error("Error de integridad de datos: {}", e.getMessage());
            bindingResult.rejectValue("global.error", "Esta reserva ya existe. Por favor, vuelva a intentarlo en otra fecha/hora.");
            return "reservas/nueva-reserva";
        }

        redirectAttributes.addFlashAttribute("exito", "La reserva se ha guardado correctamente");
        return "redirect:/reservas";
    }

    @GetMapping("change")
    public String changeReserva(@RequestParam(value = "id") Integer idReserva, Model model) {
        Optional<Reserva> reserva = reservaService.findById(idReserva);
        model.addAttribute("reserva", reserva.orElse(new Reserva()));
        // Agregar los posibles estados al modelo
        model.addAttribute("estados", EstadoReserva.values());
        return "reservas/editar-reserva";
    }

    @PostMapping("change-reserva")
    public String changeReserva(@Valid Reserva reserva, BindingResult bindingResult, RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            for (ObjectError error : bindingResult.getAllErrors()) {
                log.error("Ocurrió un error: {}", error.getDefaultMessage());
            }
            return "reservas/editar-reserva";
        }

        try {
            log.info("Membresía a guardar: {}", reserva);
            reservaService.save(reserva);
        } catch (DataIntegrityViolationException e) {
            log.error("Error de integridad de datos: {}", e.getMessage());
            bindingResult.rejectValue("global.error", "Esta reserva ya existe. Por favor, vuelva a intentarlo en otra fecha/hora.");
            return "reservas/editar-reserva";
        }

        redirectAttributes.addFlashAttribute("exito", "La reserva se ha guardado correctamente");
        return "redirect:/reservas";
    }

}
