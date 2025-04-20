package com.sgdc.core.pagos.controller;

import com.sgdc.core.pagos.domain.dto.PagoMembresiaResumenDTO;
import com.sgdc.core.pagos.service.PagoMembresiaService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/pagos")
public class PagosApiController {

    private final PagoMembresiaService pagoMembresiaService;

    public PagosApiController(PagoMembresiaService pagoMembresiaService) {
        this.pagoMembresiaService = pagoMembresiaService;
    }

    @GetMapping("miembro/{id}")
    public List<PagoMembresiaResumenDTO> pagosDeMiembro(@PathVariable Integer id) {
        return pagoMembresiaService.resumenAllPagosByMiembro(id, 5);
    }
    
}
