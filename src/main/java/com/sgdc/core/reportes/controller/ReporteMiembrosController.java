package com.sgdc.core.reportes.controller;

import com.sgdc.core.membresia.service.MembresiaService;
import com.sgdc.core.miembro.domain.Miembro;
import com.sgdc.core.miembro.service.MiembroService;
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
@RequestMapping("reportes/miembros")
public class ReporteMiembrosController {

    private final MiembroService miembroService;
    private final MembresiaService membresiaService; // Para cargar la lista del select

    public ReporteMiembrosController(MiembroService miembroService, MembresiaService membresiaService) {
        this.miembroService = miembroService;
        this.membresiaService = membresiaService;
    }

    @GetMapping
    public String showReporteMiembros(
            @RequestParam(required = false) Integer idMembresia,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaInicio,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaFin,
            Model model) {

        // Convertir LocalDate a LocalDateTime: inicio del día y fin del día, respectivamente.
        LocalDateTime fechaInicioDT = (fechaInicio != null) ? fechaInicio.atStartOfDay() : null;
        LocalDateTime fechaFinDT = (fechaFin != null) ? fechaFin.atTime(23, 59, 59) : null;

        List<Miembro> resultados = miembroService.searchMiembros(idMembresia, fechaInicioDT, fechaFinDT);
        model.addAttribute("resultados", resultados);
        // Se carga la lista de tipos de membresías para el campo select.
        model.addAttribute("listaMembresias", membresiaService.findAll());
        return "reportes/miembros";
    }

    @GetMapping("/exportPdf")
    public ResponseEntity<byte[]> exportPdf(
            @RequestParam(required = false) Integer idMembresia,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaInicio,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fechaFin) {

        byte[] pdfBytes = miembroService.generatePdfReport(idMembresia, fechaInicio, fechaFin);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        // Se sugiere definir un nombre de archivo en el Content-Disposition
        headers.setContentDispositionFormData("filename", "reporte_miembros.pdf");

        return new ResponseEntity<>(pdfBytes, headers, HttpStatus.OK);
    }

}
