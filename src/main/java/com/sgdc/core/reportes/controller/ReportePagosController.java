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

package com.sgdc.core.reportes.controller;

import com.sgdc.core.membresia.service.MembresiaService;
import com.sgdc.core.miembro.service.MiembroService;
import com.sgdc.core.pagos.domain.PagoMembresia;
import com.sgdc.core.pagos.service.PagoMembresiaService;
import com.sgdc.core.reportes.domain.dto.PagoReportDTO;
import com.sgdc.core.reservas.domain.Reserva;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Controller
@RequestMapping("reportes/pagos")
public class ReportePagosController {

    private static final Logger log = LoggerFactory.getLogger(ReportePagosController.class);

    private final PagoMembresiaService pagoService;
    private final MiembroService miembroService;
    private final MembresiaService membresiaService;

    public ReportePagosController(PagoMembresiaService pagoService, MiembroService miembroService, MembresiaService membresiaService) {
        this.pagoService = pagoService;
        this.miembroService = miembroService;
        this.membresiaService = membresiaService;
    }

    @GetMapping
    public String show(
            @RequestParam(required = false) Integer idMiembro,
            @RequestParam(required = false) Integer idMembresia,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate fechaPagoInicio,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate fechaPagoFin,
            Model model) {

        LocalDateTime inicio = (fechaPagoInicio != null)
                ? fechaPagoInicio.atStartOfDay() : null;
        LocalDateTime fin = (fechaPagoFin != null)
                ? fechaPagoFin.atTime(23,59,59) : null;

        List<PagoReportDTO> resultados = pagoService
                .searchPagosReport(idMiembro, idMembresia, inicio, fin);

        model.addAttribute("resultados", resultados);
        model.addAttribute("listaMiembros",   miembroService.findAll());
        model.addAttribute("listaMembresias", membresiaService.findAll());
        return "reportes/pagos";
    }

    @GetMapping("/exportPdf")
    public ResponseEntity<byte[]> exportPdf(
            @RequestParam(required = false) Integer idMiembro,
            @RequestParam(required = false) Integer idMembresia,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate fechaPagoInicio,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
            LocalDate fechaPagoFin) {

        LocalDateTime inicio = (fechaPagoInicio != null)
                ? fechaPagoInicio.atStartOfDay() : null;
        LocalDateTime fin = (fechaPagoFin != null)
                ? fechaPagoFin.atTime(23,59,59) : null;

        byte[] pdf = pagoService
                .generatePdfReport(idMiembro, idMembresia, inicio, fin);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDispositionFormData("filename", "reporte_pagos.pdf");
        return ResponseEntity.ok().headers(headers).body(pdf);
    }

}
