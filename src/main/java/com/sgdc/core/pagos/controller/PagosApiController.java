/*
 * Copyright (C) 2025 the original author or authors.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

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
