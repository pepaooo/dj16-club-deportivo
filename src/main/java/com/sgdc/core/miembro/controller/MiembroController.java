package com.sgdc.core.miembro.controller;

import com.sgdc.core.miembro.domain.Miembro;
import com.sgdc.core.miembro.service.MiembroService;
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
@RequestMapping("miembros")
public class MiembroController {

    private static final Logger log = LoggerFactory.getLogger(MiembroController.class);

    private final MiembroService miembroService;

    public MiembroController(MiembroService miembroService) {
        this.miembroService = miembroService;
    }

    @GetMapping
    public String inicio(@RequestParam(value = "q", required = false) String keyword, Model model) {
        List<Miembro> miembros = miembroService.search(keyword);
        // Agregar al modelo los resultados y también el término de búsqueda para que el input lo retenga.
        model.addAttribute("miembros", miembros);
        model.addAttribute("q", keyword);

        // Otros atributos, por ejemplo para los resúmenes:
        List<Miembro> allMiembros = miembroService.findAll();
        model.addAttribute("totalMiembros", allMiembros.size());
        // Para calcular miembros activos/inactivos.
        // TODO calcular miembros activos/inactivos.
//        long activas = allMiembros.stream().filter(m -> "Activo".equalsIgnoreCase(m.getEstatus())).count();
//        long inactivas = allMiembros.size() - activas;
        model.addAttribute("miembrosActivos", 0);
        model.addAttribute("miembrosInactivos", 0);

        return "miembros/inicio";
    }

    @GetMapping("get")
    public String getMiembro(@RequestParam(value = "id") Integer idMiembro, Model model) {
        Optional<Miembro> miembro = miembroService.findById(idMiembro);
        model.addAttribute("miembro", miembro.orElse(new Miembro()));
        return "miembros/ver-miembro";
    }

    @GetMapping("new")
    public String newMiembro(Model model) {
        model.addAttribute("miembro", new Miembro());
        return "miembros/nueva-miembro";
    }

    @PostMapping("create-miembro")
    public String createMiembro(@Valid Miembro miembro, BindingResult bindingResult, RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            for (ObjectError error : bindingResult.getAllErrors()) {
                log.error("Ocurrió un error: {}", error.getDefaultMessage());
            }
            return "miembros/nueva-miembro";
        }

        try {
            log.info("Membresía a guardar: {}", miembro);
            miembroService.save(miembro);
        } catch (DataIntegrityViolationException e) {
            log.error("Error de integridad de datos: {}", e.getMessage());
            bindingResult.rejectValue("nombre", "nombre", "El nombre de la membresía ya existe. Por favor, use otro.");
            return "miembros/nueva-miembro";
        }

        redirectAttributes.addFlashAttribute("exito", "La membresía se ha guardado correctamente");
        return "redirect:/miembros";
    }

    @GetMapping("change")
    public String changeMiembro(@RequestParam(value = "id") Integer idMiembro, Model model) {
        Optional<Miembro> miembro = miembroService.findById(idMiembro);
        model.addAttribute("miembro", miembro.orElse(new Miembro()));
        return "miembros/editar-miembro";
    }

    @PostMapping("change-miembro")
    public String changeMiembro(@Valid Miembro miembro, BindingResult bindingResult, RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            for (ObjectError error : bindingResult.getAllErrors()) {
                log.error("Ocurrió un error: {}", error.getDefaultMessage());
            }
            return "miembros/editar-miembro";
        }

        try {
            log.info("Membresía a guardar: {}", miembro);
            miembroService.save(miembro);
        } catch (DataIntegrityViolationException e) {
            log.error("Error de integridad de datos: {}", e.getMessage());
            bindingResult.rejectValue("nombre", "nombre", "El nombre de la membresía ya existe. Por favor, use otro.");
            return "miembros/editar-miembro";
        }

        redirectAttributes.addFlashAttribute("exito", "La membresía se ha guardado correctamente");
        return "redirect:/miembros";
    }

}
