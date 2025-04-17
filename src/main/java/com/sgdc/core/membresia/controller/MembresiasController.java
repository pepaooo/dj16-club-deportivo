package com.sgdc.core.membresia.controller;

import com.sgdc.core.membresia.domain.Membresia;
import com.sgdc.core.membresia.service.MembresiaService;
import com.sgdc.core.miembro.domain.Miembro;
import com.sgdc.core.pagos.domain.PagoMembresia;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@Controller
@RequestMapping("membresias")
public class MembresiasController {

    private static final Logger log = LoggerFactory.getLogger(MembresiasController.class);

    private final MembresiaService membresiaService;

    public MembresiasController(MembresiaService membresiaService) {
        this.membresiaService = membresiaService;
    }

    @GetMapping
    public String inicio(@RequestParam(value = "q", required = false) String keyword, Model model) {
        List<Membresia> membresias = membresiaService.search(keyword);
        // Agregar al modelo los resultados y también el término de búsqueda para que el input lo retenga.
        model.addAttribute("membresias", membresias);
        model.addAttribute("q", keyword);

        // Otros atributos, por ejemplo para los resúmenes:
        //List<Membresia> allMembresias = membresiaService.findAll();
        model.addAttribute("totalMembresias", membresias.size());
        // Para calcular membresías activas/inactivas, podrías hacer filtrados o consultar en el servicio.
        long activas = membresias.stream().filter(m -> "Activo".equalsIgnoreCase(m.getEstatus())).count();
        long inactivas = membresias.size() - activas;
        model.addAttribute("membresiasActivas", activas);
        model.addAttribute("membresiasInactivas", inactivas);

        return "membresias/inicio";
    }

    @GetMapping("get")
    public String getMembresia(@RequestParam(value = "id") Integer idMembresia, Model model) {
        Membresia membresia = membresiaService.findById(idMembresia);
        model.addAttribute("membresia", membresia);
        return "membresias/ver-membresia";
    }

    @GetMapping("new")
    public String newMembresia(Model model) {
        model.addAttribute("membresia", new Membresia());
        return "membresias/nueva-membresia";
    }

    @PostMapping("create-membresia")
    public String createMembresia(@Valid Membresia membresia, BindingResult bindingResult, RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            for (ObjectError error : bindingResult.getAllErrors()) {
                log.error("Ocurrió un error: {}", error.getDefaultMessage());
            }
            return "membresias/nueva-membresia";
        }

        try {
            membresia.setEstatus("Activo");
            log.info("Membresía a guardar: {}", membresia);
            membresiaService.save(membresia);
        } catch (DataIntegrityViolationException e) {
            log.error("Error de integridad de datos: {}", e.getMessage());
            bindingResult.rejectValue("nombre", "nombre", "El nombre de la membresía ya existe. Por favor, use otro.");
            return "membresias/nueva-membresia";
        }

        redirectAttributes.addFlashAttribute("exito", "La membresía se ha guardado correctamente");
        return "redirect:/membresias";
    }

    @GetMapping("change")
    public String changeMembresia(@RequestParam(value = "id") Integer idMembresia, Model model) {
        Membresia membresia = membresiaService.findById(idMembresia);
        model.addAttribute("membresia", membresia);
        return "membresias/editar-membresia";
    }

    @PostMapping("change-membresia")
    public String changeMembresia(@Valid Membresia membresia, BindingResult bindingResult, RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            for (ObjectError error : bindingResult.getAllErrors()) {
                log.error("Ocurrió un error: {}", error.getDefaultMessage());
            }
            return "membresias/editar-membresia";
        }

        try {
            log.info("Membresía a guardar: {}", membresia);
            membresiaService.save(membresia);
        } catch (DataIntegrityViolationException e) {
            log.error("Error de integridad de datos: {}", e.getMessage());
            bindingResult.rejectValue("nombre", "nombre", "El nombre de la membresía ya existe. Por favor, use otro.");
            return "membresias/editar-membresia";
        }

        redirectAttributes.addFlashAttribute("exito", "La membresía se ha guardado correctamente");
        return "redirect:/membresias";
    }

    @PostMapping("activate")
    public String activarMembresia(@RequestParam(value = "id") Integer idMembresia, RedirectAttributes redirectAttributes) {
        Optional<Membresia> membresia = membresiaService.activateMembresia(idMembresia);
        redirectAttributes.addFlashAttribute("exito", "La membresía se ha guardado correctamente");
        return "redirect:/membresias";
    }

    @PostMapping("deactivate")
    public String desactivarMembresia(@RequestParam(value = "id") Integer idMembresia, RedirectAttributes redirectAttributes) {
        Optional<Membresia> membresia = membresiaService.deactivateMembresia(idMembresia);
        redirectAttributes.addFlashAttribute("exito", "La membresía se ha guardado correctamente");
        return "redirect:/membresias";
    }

}
