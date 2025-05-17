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

package com.sgdc.core.auditoria.aop;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Auditable {
    /**
     * Tipo de acción: e.g. "CREATE", "UPDATE", "DELETE"
     */
    String tipoAccion();

    /**
     * Nombre de la tabla o entidad afectada
     */
    String tabla();

    /**
     * Expression SpEL para extraer el ID de la entidad, p.ej. "#result.id" o "#dto.id"
     */
    String entidadId();

    /**
     * Mensaje o plantilla para la descripción: permite SpEL, e.g. "'Objeto creado '+#result.id"
     */
    String descripcion() default "";
}

