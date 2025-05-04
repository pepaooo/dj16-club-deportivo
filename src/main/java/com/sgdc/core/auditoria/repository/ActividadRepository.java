package com.sgdc.core.auditoria.repository;

import com.sgdc.core.auditoria.model.Actividad;
import com.sgdc.core.usuarios.domain.Usuario;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface ActividadRepository extends JpaRepository<Actividad, Integer>, JpaSpecificationExecutor<Actividad> {
    // Listas de todas las actividades ordenadas de forma descendente por ID
    List<Actividad> findAllByOrderByIdDesc();

    // Listar actividades por usuario
    List<Actividad> findByUsuario(Usuario usuario);
}
