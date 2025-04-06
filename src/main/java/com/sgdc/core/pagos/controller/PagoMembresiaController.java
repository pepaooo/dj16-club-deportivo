package com.sgdc.core.pagos.controller;

import com.sgdc.core.membresia.repository.MembresiaRepository;
import com.sgdc.core.miembro.domain.Genero;
import com.sgdc.core.miembro.domain.Miembro;
import com.sgdc.core.miembro.service.MiembroService;
import com.sgdc.core.pagos.domain.PagoAjuste;
import com.sgdc.core.pagos.domain.PagoMembresia;
import com.sgdc.core.pagos.domain.dto.PagoMembresiaResumenDTO;
import com.sgdc.core.pagos.service.PagoAjusteService;
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

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("pagos")
public class PagoMembresiaController {

    private static final Logger log = LoggerFactory.getLogger(PagoMembresiaController.class);

    private final PagoMembresiaService pagoMembresiaService;

    private final MiembroService miembroService;

    private final PagoAjusteService pagoAjusteService;

    public PagoMembresiaController(PagoMembresiaService pagoMembresiaService, MiembroService miembroService, PagoAjusteService pagoAjusteService) {
        this.pagoMembresiaService = pagoMembresiaService;
        this.miembroService = miembroService;
        this.pagoAjusteService = pagoAjusteService;
    }

    @GetMapping
    public String inicio(@RequestParam(value = "q", required = false) String keyword, Model model) {
        List<PagoMembresiaResumenDTO> pagosMembresiaDTO = pagoMembresiaService.searchResumen(keyword);
        // Agregar al modelo los resultados y también el término de búsqueda para que el input lo retenga.
        model.addAttribute("pagosMembresiaDTO", pagosMembresiaDTO);
        model.addAttribute("q", keyword);

        // Otros atributos, por ejemplo para los resúmenes:
        //List<PagoMembresia> allPagosMembresia = pagoMembresiaService.findAll();
        model.addAttribute("totalDePagos", pagosMembresiaDTO.size());
        // Para calcular pagos activos/vencidos
        long activos = pagosMembresiaDTO.stream().filter(m -> "Activo".equalsIgnoreCase(m.getEstatusMembresia())).count();
        long vencidos = pagosMembresiaDTO.size() - activos;
        model.addAttribute("pagosActivos", activos);
        model.addAttribute("pagosVencidos", vencidos);

        return "pagos/inicio";
    }

    @GetMapping("get")
    public String getPagoMembresia(@RequestParam(value = "id") Integer idPagoMembresia,
                                   @RequestParam(value = "origen", required = false) String origen,
                                   Model model) {
        Optional<PagoMembresia> pagoMembresia = pagoMembresiaService.findById(idPagoMembresia);
        List<PagoAjuste> ajustes = pagoAjusteService.findByPagoMembresiaId(idPagoMembresia);
        model.addAttribute("pago", pagoMembresia.orElse(new PagoMembresia()));
        model.addAttribute("ajustes", ajustes);
        model.addAttribute("origen", origen); // 'miembro' o 'pagos'
        return "pagos/ver-pago";
    }

    @GetMapping("new")
    public String newPagoMembresia(Model model) {
        model.addAttribute("pagoMembresia", new PagoMembresia());
        // Agregamos los atributos adicionales al bean necesarios para la vista.
        return "pagos/nuevo-pago";
    }

    @PostMapping("create-pago")
    public String createPagoMembresia(@Valid PagoMembresia pagoMembresia, BindingResult bindingResult, RedirectAttributes redirectAttributes, Model model) {
        if (bindingResult.hasErrors()) {
            for (ObjectError error : bindingResult.getAllErrors()) {
                log.error("Ocurrió un error: {}", error.getDefaultMessage());
            }
            return "pagos/nuevo-pago";
        }

        try {
            pagoMembresia.setFechaPago(LocalDateTime.now());
            pagoMembresia.setFechaFin(LocalDate.now().plusDays(pagoMembresia.getMiembro().getMembresia().getDuracionDias()));
            log.info("Pago a guardar: {}", pagoMembresia);
            pagoMembresiaService.save(pagoMembresia);
        } catch (DataIntegrityViolationException e) {
            String errorMessage = e.getMessage();
            log.error("Error de integridad de datos: {}", errorMessage);
            if (errorMessage.contains("uq_pago")) {
                // Agregamos un error global que no se asocia a un campo en particular
                bindingResult.reject("global.error", "El pago ya existe. Verifique los datos ingresados.");
            }
            return "pagos/nuevo-pago";
        }

        redirectAttributes.addFlashAttribute("exito", "El pago de membresía se ha guardado correctamente");
        return "redirect:/pagos";
    }

    @GetMapping("change")
    public String changePagoMembresia(@RequestParam(value = "id") Integer idPagoMembresia, Model model) {
        Optional<PagoMembresia> pagoMembresia = pagoMembresiaService.findById(idPagoMembresia);
        model.addAttribute("pagoMembresia", pagoMembresia.orElse(new PagoMembresia()));
        return "pagos/editar-pago";
    }

    @PostMapping("change-pago")
    public String changePagoMembresia(@Valid PagoMembresia pagoMembresia, BindingResult bindingResult, RedirectAttributes redirectAttributes, Model model) {
        if (bindingResult.hasErrors()) {
            for (ObjectError error : bindingResult.getAllErrors()) {
                log.error("Ocurrió un error: {}", error.getDefaultMessage());
            }
            return "pagos/editar-pago";
        }

        try {
            log.info("Pago a guardar: {}", pagoMembresia);
            pagoMembresiaService.save(pagoMembresia);
        } catch (DataIntegrityViolationException e) {
            String errorMessage = e.getMessage();
            log.error("Error de integridad de datos: {}", errorMessage);
            if (errorMessage.contains("uq_miembro")) {
                // Agregamos un error global que no se asocia a un campo en particular
                bindingResult.reject("global.error", "El pago ya existe. Verifique los datos ingresados.");
            }
            return "pagos/editar-pago";
        }

        redirectAttributes.addFlashAttribute("exito", "El pago de membresía se ha guardado correctamente");
        return "redirect:/pagos";
    }

    @GetMapping("historial-pagos")
    public String viewHistoialPagos(@RequestParam(value = "id") Integer idMiembro, Model model) {
        Optional<Miembro> miembro = miembroService.findById(idMiembro);
        List<PagoMembresia> historialPagos = pagoMembresiaService.findByMiembroId(idMiembro);
        model.addAttribute("historialPagos", historialPagos);
        model.addAttribute("miembro", miembro.orElse(new Miembro()));
        return "pagos/historial";
    }

}
