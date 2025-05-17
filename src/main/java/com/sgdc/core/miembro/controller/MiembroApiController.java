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

    private MiembroDTO convertToDto(Miembro miembro) {
        return modelMapper.map(miembro, MiembroDTO.class);
    }

}
