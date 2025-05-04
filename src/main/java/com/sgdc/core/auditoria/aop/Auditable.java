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

