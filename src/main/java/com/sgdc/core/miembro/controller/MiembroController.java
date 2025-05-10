package com.sgdc.core.miembro.controller;

import com.sgdc.core.membresia.domain.Beneficio;
import com.sgdc.core.membresia.domain.HistorialMembresia;
import com.sgdc.core.membresia.service.BeneficioService;
import com.sgdc.core.membresia.service.HistorialMembresiaService;
import com.sgdc.core.miembro.domain.Genero;
import com.sgdc.core.miembro.domain.Miembro;
import com.sgdc.core.miembro.service.MiembroService;
import com.sgdc.core.pagos.domain.dto.PagoMembresiaResumenDTO;
import com.sgdc.core.pagos.service.PagoMembresiaService;
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

import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("miembros")
public class MiembroController {

    private static final Logger log = LoggerFactory.getLogger(MiembroController.class);

    private final MiembroService miembroService;

    private final HistorialMembresiaService historialMembresiaService;

    private final PagoMembresiaService pagoMembresiaService;

    private final BeneficioService beneficioService;


    public MiembroController(MiembroService miembroService, HistorialMembresiaService historialMembresiaService, PagoMembresiaService pagoMembresiaService, BeneficioService beneficioService) {
        this.miembroService = miembroService;
        this.historialMembresiaService = historialMembresiaService;
        this.pagoMembresiaService = pagoMembresiaService;
        this.beneficioService = beneficioService;
    }

    @GetMapping
    public String inicio(@RequestParam(value = "q", required = false) String keyword, Model model) {
        List<Miembro> miembros = miembroService.search(keyword);
        // Agregar al modelo los resultados y también el término de búsqueda para que el input lo retenga.
        model.addAttribute("miembros", miembros);
        model.addAttribute("q", keyword);
        return "miembros/inicio";
    }

    @GetMapping("get")
    public String getMiembro(@RequestParam(value = "id") Integer idMiembro, Model model) {
        Miembro miembro = miembroService.findById(idMiembro);
        log.info("getMiembro: {}", miembro);
        List<PagoMembresiaResumenDTO> pagos = pagoMembresiaService.resumenPagosByMiembro(idMiembro, null);
        log.info("pagos del id miembro {} : {}", idMiembro, pagos);
        model.addAttribute("miembro", miembro);
        model.addAttribute("pagosMembresiaDTO", pagos);
        List<Beneficio> beneficios = new ArrayList<>();
        // Obtenemos los beneficios vigentes (Estatus = Activo)
        PagoMembresiaResumenDTO pagoActivo = pagos.stream()
                .filter(p -> p.getEstatusMembresia().equals("Activo"))
                .findFirst()
                .orElse(null);
        if (pagoActivo != null && pagoActivo.getIdMembresiaActual() != null) {
            beneficios = beneficioService.findByMembresia(pagoActivo.getIdMembresiaActual());
        }
        model.addAttribute("beneficios", beneficios);
        return "miembros/ver-miembro";
    }

    @GetMapping("new")
    public String newMiembro(Model model) {
        model.addAttribute("miembro", new Miembro());
        // Agregamos los atributos adicionales al bean necesarios para la vista.
        model.addAttribute("generos", Genero.values());
        return "miembros/nuevo-miembro";
    }

    @PostMapping("create-miembro")
    public String createMiembro(@Valid Miembro miembro, BindingResult bindingResult, RedirectAttributes redirectAttributes, Model model) {
        if (bindingResult.hasErrors()) {
            for (ObjectError error : bindingResult.getAllErrors()) {
                log.error("Ocurrió un error: {}", error.getDefaultMessage());
            }
            // Agregamos los atributos necesarios para la vista.
            model.addAttribute("generos", Genero.values());
            return "miembros/nuevo-miembro";
        }

        try {
            log.info("Miembro a guardar: {}", miembro);
            miembroService.save(miembro);
        } catch (DataIntegrityViolationException e) {
            String errorMessage = e.getMessage();
            log.error("Error de integridad de datos: {}", errorMessage);
            if (errorMessage.contains("uq_miembro")) {
                // Agregamos un error global que no se asocia a un campo en particular
                bindingResult.reject("global.error", "El miembro ya existe. Verifique los datos ingresados.");
            } else if (errorMessage.contains("correo_electronico")) {
                bindingResult.rejectValue("correoElectronico", "correoElectronico", "El correo electrónico ya existe. Por favor, use otro.");
            }
            // Agregamos los atributos necesarios para la vista.
            model.addAttribute("generos", Genero.values());
            return "miembros/nuevo-miembro";
        }

        redirectAttributes.addFlashAttribute("exito", "La membresía se ha guardado correctamente");
        return "redirect:/miembros";
    }

    @GetMapping("change")
    public String changeMiembro(@RequestParam(value = "id") Integer idMiembro, Model model) {
        Miembro miembro = miembroService.findById(idMiembro);
        model.addAttribute("miembro", miembro);
        // Agregamos los atributos adicionales al bean necesarios para la vista.
        model.addAttribute("generos", Genero.values());
        return "miembros/editar-miembro";
    }

    @PostMapping("change-miembro")
    public String changeMiembro(@Valid Miembro miembro, BindingResult bindingResult, RedirectAttributes redirectAttributes, Model model) {
        if (bindingResult.hasErrors()) {
            for (ObjectError error : bindingResult.getAllErrors()) {
                log.error("Ocurrió un error: {}", error.getDefaultMessage());
            }
            // Agregamos los atributos necesarios para la vista.
            model.addAttribute("generos", Genero.values());
            return "miembros/editar-miembro";
        }

        try {
            log.info("Membresía a guardar: {}", miembro);
            miembroService.update(miembro);
        } catch (DataIntegrityViolationException e) {
            String errorMessage = e.getMessage();
            log.error("Error de integridad de datos: {}", errorMessage);
            if (errorMessage.contains("uq_miembro")) {
                // Agregamos un error global que no se asocia a un campo en particular
                bindingResult.reject("global.error", "El miembro ya existe. Verifique los datos ingresados.");
            } else if (errorMessage.contains("correo_electronico")) {
                bindingResult.rejectValue("correoElectronico", "correoElectronico", "El correo electrónico ya existe. Por favor, use otro.");
            }
            // Agregamos los atributos necesarios para la vista.
            model.addAttribute("generos", Genero.values());
            return "miembros/editar-miembro";
        }

        redirectAttributes.addFlashAttribute("exito", "La membresía se ha guardado correctamente");
        return "redirect:/miembros";
    }


    @GetMapping("historial-membresias")
    public String viewHistorialMembresias(@RequestParam(value = "id") Integer idMiembro,
                                          @RequestParam(value = "q", required = false) String keyword,
                                          Model model) {
        Miembro miembro = miembroService.findById(idMiembro);
        List<HistorialMembresia> historialMembresias = historialMembresiaService.search(idMiembro, keyword);
        model.addAttribute("historialMembresias", historialMembresias);
        model.addAttribute("miembro", miembro);
        model.addAttribute("q", keyword);
        return "miembros/historial-membresias";
    }

}
