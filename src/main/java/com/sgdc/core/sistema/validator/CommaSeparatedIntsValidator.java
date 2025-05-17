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

package com.sgdc.core.sistema.validator;

import org.springframework.stereotype.Component;

import java.util.List;
import java.util.regex.Pattern;

@Component
public class CommaSeparatedIntsValidator implements ConfigValidator {

    @Override
    public boolean supports(String key) {
        // Soporta cualquier parámetro que sea una lista de enteros separados por comas.
        return List.of("notificacion_periodo").contains(key);
    }

    private static final Pattern PATTERN =
            Pattern.compile("^\\d+(,\\d+)*$");

    @Override
    public void validate(String value) {
        if (value == null || value.isEmpty()) {
            throw new IllegalArgumentException("El valor no puede ser nulo o vacío");
        }
        if (!PATTERN.matcher(value).matches()) {
            throw new IllegalArgumentException("El valor debe ser una lista de enteros separados por comas");
        }
    }
}
