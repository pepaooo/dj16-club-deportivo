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

@Component
public class BooleanStringValidator implements ConfigValidator {

    @Override
    public boolean supports(String key) {
        // Soporta cualquier parámetro booleano definido en el sistema.
        return List.of("notificaciones_activadas").contains(key);
    }

    @Override
    public void validate(String value) {
        if (value == null || value.isEmpty()) {
            throw new IllegalArgumentException("El valor no puede ser nulo o vacío");
        }
        if (!value.equalsIgnoreCase("true") && !value.equalsIgnoreCase("false")) {
            throw new IllegalArgumentException("El valor debe ser 'true' o 'false'");
        }
    }
}
