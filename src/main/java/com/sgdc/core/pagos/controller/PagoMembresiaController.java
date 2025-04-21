package com.sgdc.core.pagos.controller;

import com.sgdc.core.membresia.exception.MembresiaInactivaException;
import com.sgdc.core.membresia.service.MembresiaService;
import com.sgdc.core.miembro.domain.Miembro;
import com.sgdc.core.miembro.service.MiembroService;
import com.sgdc.core.pagos.domain.PagoAjuste;
import com.sgdc.core.pagos.domain.PagoMembresia;
import com.sgdc.core.pagos.domain.dto.PagoMembresiaDTO;
import com.sgdc.core.pagos.domain.dto.PagoMembresiaResumenDTO;
import com.sgdc.core.pagos.exception.PagoInactivoException;
import com.sgdc.core.pagos.exception.PagoInvalidoException;
import com.sgdc.core.pagos.service.PagoAjusteService;
import com.sgdc.core.pagos.service.PagoMembresiaService;
import com.sgdc.core.usuarios.domain.UsuarioDTO;
import com.sgdc.core.usuarios.service.UsuarioService;
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
import java.time.LocalDateTime;
import java.util.List;

@Controller
@RequestMapping("pagos")
public class PagoMembresiaController {

    private static final Logger log = LoggerFactory.getLogger(PagoMembresiaController.class);

    private final PagoMembresiaService pagoMembresiaService;

    private final MiembroService miembroService;

    private final MembresiaService membresiaService;

    private final PagoAjusteService pagoAjusteService;

    private final UsuarioService usuarioService;

    public PagoMembresiaController(PagoMembresiaService pagoMembresiaService, MiembroService miembroService, MembresiaService membresiaService, PagoAjusteService pagoAjusteService, UsuarioService usuarioService) {
        this.pagoMembresiaService = pagoMembresiaService;
        this.miembroService = miembroService;
        this.membresiaService = membresiaService;
        this.pagoAjusteService = pagoAjusteService;
        this.usuarioService = usuarioService;
    }

    @GetMapping
    public String inicio(@RequestParam(value = "q", required = false) String keyword, Model model) {
        List<PagoMembresiaResumenDTO> pagosMembresiaDTO = pagoMembresiaService.searchResumen(keyword);
        // Agregar al modelo los resultados y también el término de búsqueda para que el input lo retenga.
        model.addAttribute("pagosMembresiaDTO", pagosMembresiaDTO);
        model.addAttribute("q", keyword);
        // Otros atributos, por ejemplo para los resúmenes:
        model.addAttribute("totalDePagos", pagosMembresiaDTO.size());
        // Para calcular pagos activos/vencidos
        long activos = pagosMembresiaDTO.stream().filter(m -> "Activo".equalsIgnoreCase(m.getEstatusMembresia())).count();
        long programados = pagosMembresiaDTO.size() - activos;
        model.addAttribute("pagosActivos", activos);
        model.addAttribute("pagosProgramados", programados);
        return "pagos/inicio";
    }

    @GetMapping("get")
    public String getPagoMembresia(@RequestParam(value = "id") Integer idPagoMembresia,
                                   @RequestParam(value = "origen", required = false) String origen,
                                   Model model) {
        PagoMembresia pago = pagoMembresiaService.findById(idPagoMembresia);
        List<PagoAjuste> ajustes = pagoAjusteService.findByPagoMembresiaId(idPagoMembresia);
        BigDecimal montoFinal = pagoMembresiaService.calcularMontoFinal(pago, ajustes);
        model.addAttribute("pago", pago);
        model.addAttribute("montoFinal", montoFinal);
        model.addAttribute("ajustes", ajustes);
        model.addAttribute("origen", origen); // 'miembro' o 'pagos'
        return "pagos/ver-pago";
    }

    @GetMapping("new")
    public String newPagoMembresia(Model model) {
        model.addAttribute("pagoDTO", new PagoMembresiaDTO());
        model.addAttribute("tiposMembresia", membresiaService.findAll());
        // Agregamos los atributos adicionales al bean necesarios para la vista.
        return "pagos/nuevo-pago";
    }

    @PostMapping("create-pago")
    public String createPagoMembresia(@Valid @ModelAttribute("pagoDTO") PagoMembresiaDTO pagoDTO, BindingResult bindingResult, RedirectAttributes redirectAttributes, Model model) {
        log.info("Pago a guardar: {}", pagoDTO);
        if (bindingResult.hasErrors()) {
            for (ObjectError error : bindingResult.getAllErrors()) {
                log.error("Ocurrió un error: {}", error.getDefaultMessage());
            }
            model.addAttribute("pagoDTO", pagoDTO);
            model.addAttribute("tiposMembresia", membresiaService.findAll());
            return "pagos/nuevo-pago";
        }

        try {
            // TODO. Ajustar con el usuario de la sesión.
            pagoDTO.setUsuarioDTO(UsuarioDTO.builder().id(1).build());
            pagoMembresiaService.save(pagoDTO);
        } catch (MembresiaInactivaException e) {
            bindingResult.reject("global.error", "Al momento de registrar el pago, la membresía está inactiva.");
            model.addAttribute("tiposMembresia", membresiaService.findAll());
            return "pagos/nuevo-pago";
        } catch (PagoInvalidoException e) {
            bindingResult.reject("global.error", e.getMessage());
            model.addAttribute("tiposMembresia", membresiaService.findAll());
            return "pagos/nuevo-pago";
        } catch (DataIntegrityViolationException e) {
            String errorMessage = e.getMessage();
            log.error("Error de integridad de datos: {}", errorMessage);
//            // Agregamos un error global que no se asocia a un campo en particular
            bindingResult.reject("global.error", "Se ha presentado un error al momento de crear el registro del pago.");
            model.addAttribute("tiposMembresia", membresiaService.findAll());
//            redirectAttributes.addFlashAttribute("error", "Se ha presentado un error al momento de crear el registro del pago.");
//            return "redirect:/pagos";
            return "pagos/nuevo-pago";
        }

        redirectAttributes.addFlashAttribute("exito", "El pago de membresía se ha guardado correctamente");
        return "redirect:/pagos";
    }

    @GetMapping("ajustar")
    public String ajustarPagoMembresia(@RequestParam(value = "id") Integer idPagoMembresia, Model model) {
        PagoMembresia pago = pagoMembresiaService.findById(idPagoMembresia);
        model.addAttribute("pago", pago);
        // Inicializamos el objeto PagoAjuste y asignamos el pagoMembresia
        PagoAjuste ajuste = new PagoAjuste();
        ajuste.setPagoMembresia(pago);
        model.addAttribute("ajuste", ajuste);
        return "pagos/ajustar-pago";
    }

    @PostMapping("ajustar-pago")
    public String ajustarPagoMembresia(@Valid @ModelAttribute("ajuste") PagoAjuste ajuste, BindingResult bindingResult, RedirectAttributes redirectAttributes, Model model) {
        log.info("Pago a ajustar: {}", ajuste);
        // Recuperamos el pagoMembresia usando el id recibido
        PagoMembresia pago = pagoMembresiaService.findById(ajuste.getPagoMembresia().getId());
        ajuste.setPagoMembresia(pago);

        if (bindingResult.hasErrors()) {
            for (ObjectError error : bindingResult.getAllErrors()) {
                log.error("Ocurrió un error: {}", error.getDefaultMessage());
            }
            model.addAttribute("pago", pago);
            return "pagos/ajustar-pago";
        }

        try {
            ajuste.setFechaAjuste(LocalDateTime.now());
            // TODO. Ajustar con el usuario de la sesión.
            ajuste.setRegistradoPor(usuarioService.findById(1));
            pagoAjusteService.save(ajuste);
//        } catch (PagoInactivoException e) {
//            bindingResult.reject("global.error", e.getMessage());
//            model.addAttribute("pago", pago);
//            return "pagos/ajustar-pago";
        } catch (DataIntegrityViolationException e) {
            String errorMessage = e.getMessage();
            log.error("Error de integridad de datos: {}", errorMessage);
            bindingResult.reject("global.error", "Se ha presentado un error al guardar el ajuste. Por favor, verifique los datos.");
            model.addAttribute("pago", pago);
            return "pagos/ajustar-pago";
        }

        redirectAttributes.addFlashAttribute("exito", "El ajuste al pago de membresía se ha guardado correctamente");
        return "redirect:/pagos";
    }

    @PostMapping("cancelar")
    public String cancelarPago(@RequestParam Integer idPago,
                               @RequestParam String motivoCancelacion,
                               RedirectAttributes rs) {
        pagoMembresiaService.cancelarPago(idPago, motivoCancelacion);
        rs.addFlashAttribute("exito", "Pago cancelado correctamente");
        return "redirect:/pagos";
    }


    @GetMapping("historial-pagos")
    public String viewHistoialPagos(@RequestParam(value = "id") Integer idMiembro,
                                    @RequestParam(value = "q", required = false) String keyword,
                                    Model model) {
        Miembro miembro = miembroService.findById(idMiembro);
        List<PagoMembresia> historialPagos = pagoMembresiaService.search(idMiembro, keyword);
        model.addAttribute("historialPagos", historialPagos);
        model.addAttribute("miembro", miembro);
        model.addAttribute("q", keyword);
        return "pagos/historial";
    }

    @ExceptionHandler(PagoInactivoException.class)
    public String handlePagoInactivoException(PagoInactivoException ex, RedirectAttributes redirectAttributes) {
        redirectAttributes.addFlashAttribute("error", ex.getMessage());
        return "redirect:/pagos";
    }

}
