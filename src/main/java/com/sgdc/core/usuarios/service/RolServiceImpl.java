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

package com.sgdc.core.usuarios.service;

import com.sgdc.core.usuarios.domain.Rol;
import com.sgdc.core.usuarios.repository.RolRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RolServiceImpl implements RolService {

    private final RolRepository repository;

    public RolServiceImpl(RolRepository repository) {
        this.repository = repository;
    }

    @Override
    public List<Rol> findAll() {
        return repository.findAll();
    }

    @Override
    public Rol findById(Integer id) {
        return repository.findById(id).orElseThrow(() -> new RuntimeException("Rol no encontrado con id: " + id));
    }

    @Override
    public void save(Rol rol) {
        repository.save(rol);
    }
}
