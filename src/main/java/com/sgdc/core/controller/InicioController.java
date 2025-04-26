package com.sgdc.core.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class InicioController {

    @GetMapping(value = {"/"})
    public String inicio(Model model) {
        model.addAttribute("activePage", "/");
        return "inicio";
    }

    @GetMapping("reportes")
    public String reportes(Model model) {
        model.addAttribute("activePage", "/reportes");
        return "reportes/inicio";
    }

}
