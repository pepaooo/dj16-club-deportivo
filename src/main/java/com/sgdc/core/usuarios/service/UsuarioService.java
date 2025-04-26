package com.sgdc.core.usuarios.service;

import com.sgdc.core.usuarios.domain.Usuario;
import com.sgdc.core.usuarios.domain.dto.UsuarioDetalleDTO;

import java.util.List;

public interface UsuarioService {

    List<Usuario> findAll();

    Usuario findById(Integer id);

    UsuarioDetalleDTO findUsuarioDetalleDTOById(Integer id);

    List<UsuarioDetalleDTO> search(String keyword);
    
    void save(UsuarioDetalleDTO dto);

    void update(UsuarioDetalleDTO dto);

    void activate(Integer id);

    void deactivate(Integer id);

}
