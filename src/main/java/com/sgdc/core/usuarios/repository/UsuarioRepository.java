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

package com.sgdc.core.usuarios.repository;

import com.sgdc.core.usuarios.domain.Usuario;
import com.sgdc.core.usuarios.domain.dto.UsuarioDetalleDTO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface UsuarioRepository extends JpaRepository<Usuario, Integer>, JpaSpecificationExecutor<Usuario> {

    List<Usuario> findAllByOrderByIdDesc();

    Optional<Usuario> findByNombre(String nombre);

    // Obtenemos el UsuarioDetalleDTO por id de la cuenta, con datos del miembro
    @Query("""
              SELECT new com.sgdc.core.usuarios.domain.dto.UsuarioDetalleDTO(
                u.id,
                u.nombre,
                null,
                u.estatus,
                u.ultimoAcceso,
                m.id,
                COALESCE(CONCAT(m.nombre, ' ', m.apellidoPaterno, ' ', m.apellidoMaterno )) ,
                m.correoElectronico,
                null,
                null,
                u.creadoPor,
                u.modificadoPor,
                u.fechaCreacion,
                u.fechaModificacion
              )
               FROM Usuario u
                LEFT JOIN u.miembro m
               WHERE u.id = :id
            """)
    Optional<UsuarioDetalleDTO> searchById(@Param("id") Integer idUsuario);

    @Query("""
              SELECT new com.sgdc.core.usuarios.domain.dto.UsuarioDetalleDTO(
                u.id,
                u.nombre,
                null,
                u.estatus,
                u.ultimoAcceso,
                m.id,
                COALESCE(CONCAT(m.nombre, ' ', m.apellidoPaterno, ' ', m.apellidoMaterno )) ,
                m.correoElectronico,
                null,
                null,
                u.creadoPor,
                u.modificadoPor,
                u.fechaCreacion,
                u.fechaModificacion
              )
                FROM Usuario u
                LEFT JOIN u.miembro m
               WHERE ( :q IS NULL
                       OR LOWER(u.nombre)             LIKE LOWER(CONCAT('%', :q, '%'))
                       OR LOWER(m.nombre)             LIKE LOWER(CONCAT('%', :q, '%'))
                       OR LOWER(m.apellidoPaterno)    LIKE LOWER(CONCAT('%', :q, '%'))
                       OR LOWER(m.apellidoMaterno)    LIKE LOWER(CONCAT('%', :q, '%'))
                       OR LOWER(m.correoElectronico)  LIKE LOWER(CONCAT('%', :q, '%')) )
               ORDER BY u.id DESC
            """)
    List<UsuarioDetalleDTO> searchUsuarios(@Param("q") String keyword);

}
