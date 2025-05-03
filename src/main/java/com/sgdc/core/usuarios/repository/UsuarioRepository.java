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
                u.fechaCreacion,
                u.ultimoAcceso,
                m.id,
                COALESCE(CONCAT(m.nombre, ' ', m.apellidoPaterno, ' ', m.apellidoMaterno )) ,
                m.correoElectronico,
                null,
                null
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
                u.fechaCreacion,
                u.ultimoAcceso,
                m.id,
                COALESCE(CONCAT(m.nombre, ' ', m.apellidoPaterno, ' ', m.apellidoMaterno )) ,
                m.correoElectronico,
                null,
                null
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
