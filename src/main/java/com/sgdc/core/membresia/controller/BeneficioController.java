package com.sgdc.core.membresia.controller;

import com.sgdc.core.membresia.domain.Beneficio;
import com.sgdc.core.membresia.service.BeneficioService;
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
@RequestMapping("beneficios")
public class BeneficioController {

    private static final Logger log = LoggerFactory.getLogger(BeneficioController.class);

    private final BeneficioService beneficioService;

    public BeneficioController(BeneficioService beneficioService) {
        this.beneficioService = beneficioService;
    }

    @GetMapping
    public String inicio(@RequestParam(value = "q", required = false) String keyword, Model model) {
        List<Beneficio> beneficios = beneficioService.search(keyword);
        // Agregar al modelo los resultados y también el término de búsqueda para que el input lo retenga.
        model.addAttribute("beneficios", beneficios);
        model.addAttribute("q", keyword);

        return "beneficios/inicio";
    }

    @GetMapping("get")
    public String getBeneficio(@RequestParam(value = "id") Integer idBeneficio, Model model) {
        Optional<Beneficio> beneficio = beneficioService.findById(idBeneficio);
        model.addAttribute("beneficio", beneficio.orElse(new Beneficio()));
        return "beneficios/ver-beneficio";
    }

    @GetMapping("new")
    public String newBeneficio(Model model) {
        model.addAttribute("beneficio", new Beneficio());
        return "beneficios/nuevo-beneficio";
    }

    @PostMapping("create-beneficio")
    public String createBeneficio(@Valid Beneficio beneficio, BindingResult bindingResult, RedirectAttributes redirectAttributes, Model model) {
        if (bindingResult.hasErrors()) {
            for (ObjectError error : bindingResult.getAllErrors()) {
                log.error("Ocurrió un error: {}", error.getDefaultMessage());
            }
            return "beneficios/nuevo-beneficio";
        }

        try {
            log.info("Beneficio a guardar: {}", beneficio);
            beneficioService.save(beneficio);
        } catch (DataIntegrityViolationException e) {
            log.error("Error de integridad de datos: {}", e.getMessage());
            bindingResult.rejectValue("nombre", "nombre", "El nombre del beneficio ya existe. Por favor, use otro.");
            return "beneficios/nuevo-beneficio";
        }

        redirectAttributes.addFlashAttribute("exito", "El beneficio se ha guardado correctamente");
        return "redirect:/beneficios";
    }

    @GetMapping("change")
    public String changeBeneficio(@RequestParam(value = "id") Integer idBeneficio, Model model) {
        Optional<Beneficio> beneficio = beneficioService.findById(idBeneficio);
        model.addAttribute("beneficio", beneficio.orElse(new Beneficio()));
        return "beneficios/editar-beneficio";
    }


    @PostMapping("change-beneficio")
    public String changeBeneficio(@Valid Beneficio beneficio, BindingResult bindingResult, RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            for (ObjectError error : bindingResult.getAllErrors()) {
                log.error("Ocurrió un error: {}", error.getDefaultMessage());
            }
            return "beneficios/editar-beneficio";
        }

        try {
            log.info("Membresía a guardar: {}", beneficio);
            beneficioService.save(beneficio);
        } catch (DataIntegrityViolationException e) {
            log.error("Error de integridad de datos: {}", e.getMessage());
            bindingResult.rejectValue("nombre", "nombre", "El nombre del beneficio ya existe. Por favor, use otro.");
            return "beneficios/editar-beneficio";
        }

        redirectAttributes.addFlashAttribute("exito", "El beneficio se ha guardado correctamente");
        return "redirect:/beneficios";
    }

    @PostMapping("delete")
    public String deleteBeneficio(@RequestParam(value = "id") Integer idBeneficio, RedirectAttributes redirectAttributes) {
        // Eliminar el beneficio
        beneficioService.delete(idBeneficio);
        redirectAttributes.addFlashAttribute("exito", "La beneficio se ha eliminado correctamente");
        return "redirect:/beneficios";
    }

}
