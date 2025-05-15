package com.sgdc.core.reservas.controller;

import com.sgdc.core.miembro.service.MiembroService;
import com.sgdc.core.pagos.domain.PagoMembresia;
import com.sgdc.core.pagos.service.PagoMembresiaService;
import com.sgdc.core.reservas.domain.EstadoReserva;
import com.sgdc.core.reservas.domain.Instalacion;
import com.sgdc.core.reservas.domain.Reserva;
import com.sgdc.core.reservas.domain.dto.InstalacionDTO;
import com.sgdc.core.reservas.domain.dto.ReservaDTO;
import com.sgdc.core.reservas.exception.ReservaInvalidaException;
import com.sgdc.core.reservas.exception.ReservaSolapadaException;
import com.sgdc.core.reservas.service.InstalacionService;
import com.sgdc.core.reservas.service.ReservaService;
import com.sgdc.core.security.model.UserPrincipal;
import com.sgdc.core.usuarios.domain.dto.UsuarioDTO;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
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

    private final MiembroService miembroService;

    private final InstalacionService instalacionService;

    private final PagoMembresiaService pagoService;

    public ReservaController(ReservaService reservaService, MiembroService miembroService, InstalacionService instalacionService, PagoMembresiaService pagoService) {
        this.reservaService = reservaService;
        this.miembroService = miembroService;
        this.instalacionService = instalacionService;
        this.pagoService = pagoService;
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
        // Para calcular reservas activas/inactivas
        long disponibles = reservas.stream().filter(m -> EstadoReserva.PENDIENTE.getLabel().equalsIgnoreCase(m.getEstadoReserva())).count();
        model.addAttribute("reservasPendientes", disponibles);
        long mantenimiento = reservas.stream().filter(m -> EstadoReserva.CONFIRMADA.getLabel().equalsIgnoreCase(m.getEstadoReserva())).count();
        model.addAttribute("reservasConfirmadas", mantenimiento);
        long cerradas = reservas.stream().filter(m -> EstadoReserva.CANCELADA.getLabel().equalsIgnoreCase(m.getEstadoReserva())).count();
        model.addAttribute("reservasCanceladas", cerradas);

        return "reservas/inicio";
    }

    @GetMapping("get")
    public String getReserva(@RequestParam(value = "id") Integer idReserva, Model model, Authentication auth) {
        Reserva reserva = reservaService.findById(idReserva);
        model.addAttribute("reserva", reserva);
        addSolapadas(reserva, model);

        boolean isMiembro = auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("MIEMBRO"));
        model.addAttribute("isMiembro", isMiembro);
        log.info("isMiembro: {}", isMiembro);
        if(isMiembro){
            Integer miId = ((UserPrincipal)auth.getPrincipal()).getUsuario().getMiembro().getId();
            model.addAttribute("currentMiembroId", miId);
        }

        return "reservas/ver-reserva";
    }

    @GetMapping("calendario")
    public String calendario(Model model) {
        return "reservas/calendar";
    }

    @GetMapping("modal-detail")
    public String modalDetail(@RequestParam("id") Integer id, Model model, Authentication auth) {
        // Reserva principal
        Reserva reserva = reservaService.findById(id);
        model.addAttribute("reserva", reserva);
        // Lista de pendientes solapadas (cancela a confirmar)
        addSolapadas(reserva, model);

        boolean isMiembro = auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("MIEMBRO"));
        model.addAttribute("isMiembro", isMiembro);
        log.info("isMiembro: {}", isMiembro);
        if(isMiembro){
            Integer miId = ((UserPrincipal)auth.getPrincipal()).getUsuario().getMiembro().getId();
            model.addAttribute("currentMiembroId", miId);
        }

        // Apuntar al fragmento detalleReserva dentro de fragments.html
        return "reservas/fragments :: detalleReserva";
    }

    @GetMapping("new")
    @PreAuthorize("hasAnyAuthority('ADMIN','STAFF','GERENTE','MIEMBRO')")
    public String newReserva(Model model, Authentication auth) {
        ReservaDTO reservaDTO = new ReservaDTO();
        // Agregar los posibles estados al modelo
//        model.addAttribute("miembros", miembroService.findAll());
//        model.addAttribute("instalaciones", instalacionService.findAll());

        boolean isMiembro = auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("MIEMBRO"));
        model.addAttribute("isMiembro", isMiembro);
        log.info("isMiembro: {}", isMiembro);

        if (isMiembro) {
            UserPrincipal up = (UserPrincipal) auth.getPrincipal();
            Integer miId = up.getUsuario().getMiembro().getId();
            reservaDTO.setIdMiembro(miId);
            // 1) Su propio ID
            model.addAttribute("currentMiembroId", miId);
            // 2) Su nombre completo
            String nombre = up.getUsuario().getMiembro().getNombre() + " " +
                    up.getUsuario().getMiembro().getApellidoPaterno() + " " +
                    up.getUsuario().getMiembro().getApellidoMaterno();
            model.addAttribute("currentMiembroNombre", nombre);
            // 3) Su membresía activa (puedes obtenerlo de tu servicio de pagos)
            Optional<PagoMembresia> pagoOpt = pagoService.findActiveByMiembro(miId);
            if (pagoOpt.isPresent()) {
                PagoMembresia pago = pagoOpt.get();
                model.addAttribute("currentMembresiaName", pago.getMembresia().getNombre());
                model.addAttribute("currentMembresiaId", pago.getMembresia().getId());
                List<InstalacionDTO> insts = instalacionService.findByMembresiaId(pago.getMembresia().getId());
                model.addAttribute("currentInstalaciones", insts);
            } else {
                // no tiene membresía activa
                model.addAttribute("noMembership", true);
            }
        }
        model.addAttribute("reservaDTO", reservaDTO);
        return "reservas/nueva-reserva";
    }

    @PostMapping("create-reserva")
    @PreAuthorize("hasAnyAuthority('ADMIN','STAFF','GERENTE','MIEMBRO')")
    public String guardarReserva(@Valid ReservaDTO reservaDTO, BindingResult bindingResult, RedirectAttributes redirectAttributes, Model model) {
        if (bindingResult.hasErrors()) {
            logErrors(bindingResult);
//            model.addAttribute("miembros", miembroService.findAll());
//            model.addAttribute("instalaciones", instalacionService.findAll());
            return "reservas/nueva-reserva";
        }

        try {
            // Guardar la reserva
            reservaDTO.setEstadoReserva(EstadoReserva.PENDIENTE.getLabel());
            reservaService.save(reservaDTO);
        } catch (ReservaInvalidaException e) {
            String message = e.getMessage();
            log.error("Error de reserva inválida: {}", message);
            switch (message) {
                case "La fecha de inicio no puede ser en el pasado" ->
                        bindingResult.rejectValue("fechaHoraInicio", "fechaHoraInicio", "La fecha/hora de inicio no puede ser en el pasado.");
                case "La fecha fin debe ser posterior a la de inicio" ->
                        bindingResult.rejectValue("fechaHoraFin", "fechaHoraFin", "La fecha/hora fin debe ser posterior a la de inicio.");
                default -> bindingResult.reject("global.error", message);
            }
//            model.addAttribute("miembros", miembroService.findAll());
//            model.addAttribute("instalaciones", instalacionService.findAll());
            return "reservas/nueva-reserva";
        } catch (ReservaSolapadaException e) {
            log.error("Error de integridad de datos: {}", e.getMessage());
            bindingResult.reject("global.error", "Ya existe una reserva confirmada en el espacio deseado. Por favor, vuelva a intentarlo en otro horario.");
//            model.addAttribute("miembros", miembroService.findAll());
//            model.addAttribute("instalaciones", instalacionService.findAll());
            return "reservas/nueva-reserva";
        } catch (DataIntegrityViolationException e) {
            log.error("Error de integridad de datos: {}", e.getMessage());
            bindingResult.reject("global.error", "Se ha presentado un error al crear la reserva. Por favor, vuelva a intentarlo en otro horario.");
//            model.addAttribute("miembros", miembroService.findAll());
//            model.addAttribute("instalaciones", instalacionService.findAll());
            return "reservas/nueva-reserva";
        }

        redirectAttributes.addFlashAttribute("exito", "La reserva se ha guardado correctamente");
        return "redirect:/reservas";
    }

    @GetMapping("change")
    public String changeReserva(@RequestParam(value = "id") Integer idReserva, Model model) {
        Reserva reserva = reservaService.findById(idReserva);
        model.addAttribute("reservaDTO", reserva);
        // Agregar los posibles estados al modelo
        model.addAttribute("estados", EstadoReserva.values());
        return "reservas/editar-reserva";
    }

    @PostMapping("change-reserva")
    public String changeReserva(@Valid ReservaDTO reservaDTO, BindingResult bindingResult, RedirectAttributes redirectAttributes) {
        if (bindingResult.hasErrors()) {
            logErrors(bindingResult);
            return "reservas/editar-reserva";
        }

        try {
            log.info("Reserva a guardar: {}", reservaDTO);
            reservaService.save(reservaDTO);
        } catch (DataIntegrityViolationException e) {
            log.error("Error de integridad de datos: {}", e.getMessage());
            bindingResult.rejectValue("global.error", "Esta reserva ya existe. Por favor, vuelva a intentarlo en otra fecha/hora.");
            return "reservas/editar-reserva";
        }

        redirectAttributes.addFlashAttribute("exito", "La reserva se ha guardado correctamente");
        return "redirect:/reservas";
    }

    @PostMapping("confirmar")
    public String confirmarReserva(@RequestParam(value = "id") Integer idReserva, RedirectAttributes redirectAttributes) {
        reservaService.confirmarReserva(idReserva);
        redirectAttributes.addFlashAttribute("exito", "La reserva " + idReserva + " se ha guardado correctamente");
        return "redirect:/reservas";
    }

    @PostMapping("cancelar")
    public String cancelarReserva(@RequestParam(value = "id") Integer idReserva, RedirectAttributes redirectAttributes) {
        reservaService.cancelarReserva(idReserva);
        redirectAttributes.addFlashAttribute("exito", "La reserva " + idReserva + " se ha guardado correctamente");
        return "redirect:/reservas";
    }

    private void addSolapadas(Reserva r, Model model) {
        if (!r.isCancelada()) {
            List<Reserva> solapadas = reservaService.buscarPendientesSolapadas(
                    r.getInstalacion().getId(),
                    r.getFechaHoraInicio(),
                    r.getFechaHoraFin(),
                    r.getId()
            );
            model.addAttribute("pendientesSolapadas", solapadas);
        }
    }

    private void logErrors(BindingResult br) {
        br.getAllErrors().forEach(err ->
                log.error("Validación: {}", err.getDefaultMessage())
        );
    }

}
