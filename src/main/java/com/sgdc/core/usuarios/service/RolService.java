package com.sgdc.core.usuarios.service;

import com.sgdc.core.usuarios.domain.Rol;

import java.util.List;

public interface RolService {

    List<Rol> findAll();

    Rol findById(Integer id);

    void save(Rol rol);

}
