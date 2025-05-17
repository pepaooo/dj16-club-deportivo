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
import com.sgdc.core.miembro.domain.Miembro;
import com.sgdc.core.miembro.service.MiembroService;
import com.sgdc.core.reservas.domain.Reserva;
import com.sgdc.core.reservas.service.InstalacionService;
import com.sgdc.core.reservas.service.ReservaService;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
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
@RequestMapping("reportes/reservas")
public class ReporteReservasController {

    private final ReservaService reservaService;
    private final InstalacionService instalacionService;
    private final MiembroService miembroService;

    public ReporteReservasController(MiembroService miembroService, ReservaService reservaService, InstalacionService instalacionService) {
        this.miembroService = miembroService;
        this.reservaService = reservaService;
        this.instalacionService = instalacionService;
    }

    @GetMapping
    public String show(@RequestParam(required = false) Integer idInstalacion,
                       @RequestParam(required = false) Integer idMiembro,
                       @RequestParam(required = false)
                       @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
                       LocalDateTime fechaInicio,
                       @RequestParam(required = false)
                       @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
                       LocalDateTime fechaFin,
                       Model model) {

        List<Reserva> resultados = reservaService
                .searchReservas(idInstalacion, idMiembro, fechaInicio, fechaFin);

        model.addAttribute("resultados", resultados);
        model.addAttribute("listaInstalaciones", instalacionService.findAll());
        model.addAttribute("listaMiembros",     miembroService.findAll());
        return "reportes/reservas";
    }

    @GetMapping("/exportPdf")
    public ResponseEntity<byte[]> exportPdf(
            @RequestParam(required = false) Integer idInstalacion,
            @RequestParam(required = false) Integer idMiembro,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            LocalDateTime fechaInicio,
            @RequestParam(required = false)
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME)
            LocalDateTime fechaFin) {

        byte[] pdf = reservaService
                .generatePdfReport(idInstalacion, idMiembro, fechaInicio, fechaFin);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDispositionFormData("filename", "reporte_reservas.pdf");
        return ResponseEntity.ok().headers(headers).body(pdf);
    }

}
