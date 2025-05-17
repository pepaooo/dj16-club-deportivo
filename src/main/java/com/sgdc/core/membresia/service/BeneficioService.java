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

package com.sgdc.core.membresia.service;

import com.sgdc.core.membresia.domain.Beneficio;

import java.util.List;
import java.util.Optional;

public interface BeneficioService {
    List<Beneficio> findAll();

    Optional<Beneficio> findById(Integer id);

    List<Beneficio> search(String keyword);

    Beneficio save(Beneficio beneficio);

    Beneficio update(Beneficio beneficio);

    void delete(Integer id);

    List<Beneficio> findByMembresia(Integer idMembresia);
}
