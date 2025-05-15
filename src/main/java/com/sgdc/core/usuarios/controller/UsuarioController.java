package com.sgdc.core.usuarios.controller;

import com.sgdc.core.usuarios.domain.OnCreate;
import com.sgdc.core.usuarios.domain.OnUpdate;
import com.sgdc.core.usuarios.domain.dto.UsuarioDetalleDTO;
import com.sgdc.core.usuarios.service.RolService;
import com.sgdc.core.usuarios.service.UsuarioService;
import jakarta.validation.ValidationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.propertyeditors.StringTrimmerEditor;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.ObjectError;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;

@Controller
@RequestMapping("usuarios")
public class UsuarioController {

    private static final Logger log = LoggerFactory.getLogger(UsuarioController.class);

    private final UsuarioService usuarioService;

    private final RolService rolService;

    public UsuarioController(UsuarioService usuarioService, RolService rolService) {
        this.usuarioService = usuarioService;
        this.rolService = rolService;
    }

    @InitBinder
    public void initBinder(WebDataBinder binder) {
        // Trimea y convierte cadena vacía en null
        binder.registerCustomEditor(
                String.class,
                new StringTrimmerEditor(true)
        );
    }

    @GetMapping
    public String inicio(@RequestParam(value = "q", required = false) String keyword, Model model) {
        List<UsuarioDetalleDTO> usuarios = usuarioService.search(keyword);
        // Agregar al modelo los resultados y también el término de búsqueda para que el input lo retenga.
        model.addAttribute("usuarios", usuarios);
        model.addAttribute("q", keyword);
        return "usuarios/inicio";
    }

    @GetMapping("get")
    public String getUsuario(@RequestParam(value = "id") Integer id, Model model) {
        UsuarioDetalleDTO usuario = usuarioService.findUsuarioDetalleDTOById(id);
        //log.info("UsuarioDetalleDTO: {}", usuario);
        model.addAttribute("usuario", usuario);
        return "usuarios/ver-usuario";
    }

    @GetMapping("new")
    public String newUsuarioDetalleDTO(Model model) {
        model.addAttribute("usuario", new UsuarioDetalleDTO());
        model.addAttribute("roles", rolService.findAll());
        return "usuarios/nuevo-usuario";
    }

    @PostMapping("create-usuario")
    public String createUsuarioDetalleDTO(@Validated(OnCreate.class) @ModelAttribute(name = "usuario") UsuarioDetalleDTO usuario, BindingResult bindingResult, RedirectAttributes redirectAttributes, Model model) {
        if (bindingResult.hasErrors()) {
            for (ObjectError error : bindingResult.getAllErrors()) {
                log.error("Ocurrió un error: {}", error.getDefaultMessage());
            }
            // Agregamos los atributos necesarios para la vista.
            model.addAttribute("roles", rolService.findAll());
            return "usuarios/nuevo-usuario";
        }

        try {
            log.info("UsuarioDetalleDTO a guardar: {}", usuario);
            usuarioService.save(usuario);
        } catch (DataIntegrityViolationException e) {
            String errorMessage = e.getMessage();
            log.error("Error de integridad de datos: {}", errorMessage);
            if (errorMessage.contains("Duplicate entry")) {
                // Agregamos un error global que no se asocia a un campo en particular
                bindingResult.rejectValue("nombre", "nombre", "El usuario ya existe. Por favor, use otro.");
            }
            // Agregamos los atributos necesarios para la vista.
            model.addAttribute("roles", rolService.findAll());
            return "usuarios/nuevo-usuario";
        }

        redirectAttributes.addFlashAttribute("exito", "El usuario se ha guardado correctamente");
        return "redirect:/usuarios";
    }

    @GetMapping("change")
    public String changeUsuarioDetalleDTO(@RequestParam(value = "id") Integer idUsuarioDetalleDTO, Model model) {
        UsuarioDetalleDTO usuario = usuarioService.findUsuarioDetalleDTOById(idUsuarioDetalleDTO);
        model.addAttribute("usuario", usuario);
        // Agregamos los atributos adicionales al bean necesarios para la vista.
        model.addAttribute("roles", rolService.findAll());
        if (usuario.getIdMiembro() != null) {
            model.addAttribute("miembroId", usuario.getIdMiembro());
            model.addAttribute("miembroTexto", usuario.getMiembro());
        }
        return "usuarios/editar-usuario";
    }

    @PostMapping("change-usuario")
    public String changeUsuarioDetalleDTO(@Validated(OnUpdate.class) @ModelAttribute(name = "usuario") UsuarioDetalleDTO usuario, BindingResult bindingResult, RedirectAttributes redirectAttributes, Model model) {
        if (bindingResult.hasErrors()) {
            for (ObjectError error : bindingResult.getAllErrors()) {
                log.error("Ocurrió un error: {}", error.getDefaultMessage());
            }
            // Agregamos los atributos necesarios para la vista.
            model.addAttribute("roles", rolService.findAll());
            return "usuarios/editar-usuario";
        }

        try {
            log.info("Usuario a guardar: {}", usuario);
            usuarioService.update(usuario);
        } catch (DataIntegrityViolationException e) {
            String errorMessage = e.getMessage();
            log.error("Error de integridad de datos: {}", errorMessage);
            if (errorMessage.contains("Duplicate entry")) {
                // Agregamos un error global que no se asocia a un campo en particular
                bindingResult.rejectValue("nombre", "nombre", "El usuario ya existe. Por favor, use otro.");
            } else {
                // Agregamos un error global que no se asocia a un campo en particular
                bindingResult.reject("global.error", "El usuario no se ha modificado. Verifique los datos ingresados.");
            }
            // Agregamos los atributos necesarios para la vista.
            model.addAttribute("roles", rolService.findAll());
            return "usuarios/editar-usuario";
        } catch (ValidationException e) {
            log.error("Error inesperado: {}", e.getMessage());
            // Agregamos un error global que no se asocia a un campo en particular
            bindingResult.reject("global.error", "El usuario no se ha modificado. Verifique los datos ingresados.");
            // Agregamos los atributos necesarios para la vista.
            model.addAttribute("roles", rolService.findAll());
            return "usuarios/editar-usuario";
        }

        redirectAttributes.addFlashAttribute("exito", "El usuario se ha guardado correctamente");
        return "redirect:/usuarios";
    }

    @PostMapping("activate")
    public String activarUsuario(@RequestParam(value = "id") Integer idUsuario, RedirectAttributes redirectAttributes) {
        usuarioService.activate(idUsuario);
        redirectAttributes.addFlashAttribute("exito", "El usuario se ha activado correctamente");
        return "redirect:/usuarios";
    }

    @PostMapping("deactivate")
    public String desactivarUsuario(@RequestParam(value = "id") Integer idUsuario, RedirectAttributes redirectAttributes) {
        usuarioService.deactivate(idUsuario);
        redirectAttributes.addFlashAttribute("exito", "El usuario se ha desactivado correctamente");
        return "redirect:/usuarios";
    }

}
