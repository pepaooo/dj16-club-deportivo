package com.sgdc.core.security.controller;

import com.sgdc.core.miembro.domain.Miembro;
import com.sgdc.core.security.model.UserPrincipal;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice
public class CurrentUserAdvice {

    @ModelAttribute("currentMemberName")
    public String currentMemberName() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.getPrincipal() instanceof UserPrincipal up) {
            Miembro m = up.getUsuario().getMiembro();
            if (m != null) {
                return m.getNombre() + " " + m.getApellidoPaterno() + " " + m.getApellidoMaterno();
            }
            return up.getUsername();
        }
        return "";
    }
}

