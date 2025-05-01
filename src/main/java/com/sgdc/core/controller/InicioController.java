package com.sgdc.core.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

@Controller
public class InicioController {

    @GetMapping(value = {"/"})
    public String inicio(Model model) {
        model.addAttribute("activePage", "/");
        return "inicio";
    }

    @GetMapping("/login")
    public String login() {
        return "login";
    }

    @PostMapping("/login_success_handler")
    public String loginSuccessHandler() {
        System.out.println("Logging user login success...");
        return "inicio";
    }

    @PostMapping("/login_failure_handler")
    public String loginFailureHandler() {
        System.out.println("Login failure handler....");
        return "login";
    }

    @GetMapping("reportes")
    public String reportes(Model model) {
        model.addAttribute("activePage", "/reportes");
        return "reportes/inicio";
    }

}
