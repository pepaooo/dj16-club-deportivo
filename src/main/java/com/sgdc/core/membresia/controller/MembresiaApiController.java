package com.sgdc.core.membresia.controller;

import com.sgdc.core.membresia.domain.dto.MembresiaDTO;
import com.sgdc.core.membresia.domain.dto.MembresiaDetalleApiDTO;
import com.sgdc.core.membresia.service.MembresiaService;
import com.sgdc.core.reservas.domain.dto.InstalacionDTO;
import com.sgdc.core.reservas.service.InstalacionService;
import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/membresias")
public class MembresiaApiController {

    private final MembresiaService membresiaService;

    private final InstalacionService instalacionService;

    private final ModelMapper modelMapper;

    public MembresiaApiController(MembresiaService membresiaService, InstalacionService instalacionService, ModelMapper modelMapper) {
        this.membresiaService = membresiaService;
        this.instalacionService = instalacionService;
        this.modelMapper = modelMapper;
    }

    @GetMapping("tarifa")
    @ResponseBody
    public Map<String, Object> getTarifa(@RequestParam(value = "id") Integer idTipoMembresia) {
        BigDecimal tarifa = membresiaService.findById(idTipoMembresia).getTarifa();
        Map<String, Object> response = new HashMap<>();
        response.put("tarifa", tarifa);
        return response;
    }

    @GetMapping("/{id}")
    public ResponseEntity<MembresiaDetalleApiDTO> getDetalle(@PathVariable Integer id) {
        MembresiaDTO m = membresiaService.findById(id);
        return ResponseEntity.ok(convertToDto(m));
    }

    @GetMapping("/{id}/instalaciones")
    public List<InstalacionDTO> byMembresia(@PathVariable Integer id) {
        return instalacionService.findByMembresiaId(id);
    }

    private MembresiaDetalleApiDTO convertToDto(MembresiaDTO m) {
//        return MembresiaDetalleApiDTO.builder()
//                .nombre(m.getNombre())
//                .tarifa(m.getTarifa())
//                .duracionDias(m.getDuracionDias())
//                .build();
        return modelMapper.map(m, MembresiaDetalleApiDTO.class);
    }

}
