package com.sgdc.core.membresia.controller;

import com.sgdc.core.membresia.service.MembresiaService;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/membresias")
public class MembresiasApiController {

    private final MembresiaService membresiaService;

    public MembresiasApiController(MembresiaService membresiaService) {
        this.membresiaService = membresiaService;
    }

    @GetMapping("tarifa")
    @ResponseBody
    public Map<String, Object> getTarifa(@RequestParam(value = "id") Integer idTipoMembresia) {
        BigDecimal tarifa = membresiaService.findById(idTipoMembresia).getTarifa();
        Map<String, Object> response = new HashMap<>();
        response.put("tarifa", tarifa);
        return response;
    }

}
