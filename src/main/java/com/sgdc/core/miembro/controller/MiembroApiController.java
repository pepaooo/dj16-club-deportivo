package com.sgdc.core.miembro.controller;

import com.sgdc.core.miembro.domain.Miembro;
import com.sgdc.core.miembro.domain.dto.MiembroDTO;
import com.sgdc.core.miembro.domain.dto.MiembroSearchDTO;
import com.sgdc.core.miembro.service.MiembroService;
import org.modelmapper.ModelMapper;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/miembros")
public class MiembroApiController {

    private final MiembroService miembroService;

    private final ModelMapper modelMapper;

    public MiembroApiController(MiembroService miembroService, ModelMapper modelMapper) {
        this.miembroService = miembroService;
        this.modelMapper = modelMapper;
    }

    @GetMapping("search")
    @ResponseBody
    public List<MiembroDTO> search(@RequestParam("q") String term) {
        return miembroService.search(term).stream()
                .map(this::convertToDto)
                .toList();
    }

    @GetMapping("search-active")
    @ResponseBody
    public List<MiembroSearchDTO> searchActive(@RequestParam("q") String term) {
        return miembroService.searchActive(term);
    }

//    @GetMapping("detail")
//    @ResponseBody
//    public MiembroDetalleDTO getMiembroDetalle(@RequestParam("id") Integer id) {
//        ModelMapper customMapper = new ModelMapper();
//        Miembro miembro = miembroService.findById(id);
//        return customMapper.map(miembro, MiembroDetalleDTO.class);
//    }

    private MiembroDTO convertToDto(Miembro miembro) {
        return modelMapper.map(miembro, MiembroDTO.class);
    }

}
