package com.sgdc.core.membresia.controller;

import com.sgdc.core.membresia.domain.Membresia;
import com.sgdc.core.membresia.domain.dto.MembresiaDetalleDTO;
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
public class MembresiasApiController {

    private final MembresiaService membresiaService;

    private final InstalacionService instalacionService;

    private final ModelMapper modelMapper;

    public MembresiasApiController(MembresiaService membresiaService, InstalacionService instalacionService, ModelMapper modelMapper) {
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
    public ResponseEntity<MembresiaDetalleDTO> getDetalle(@PathVariable Integer id) {
        Membresia m = membresiaService.findById(id);
        return ResponseEntity.ok(convertToDto(m));
    }

    @GetMapping("/{id}/instalaciones")
    public List<InstalacionDTO> byMembresia(@PathVariable Integer id) {
        return instalacionService.findByMembresiaId(id);
    }

    private MembresiaDetalleDTO convertToDto(Membresia m) {
        return modelMapper.map(m, MembresiaDetalleDTO.class);
    }

}
