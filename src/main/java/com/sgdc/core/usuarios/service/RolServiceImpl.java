package com.sgdc.core.usuarios.service;

import com.sgdc.core.usuarios.domain.Rol;
import com.sgdc.core.usuarios.repository.RolRepository;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class RolServiceImpl implements RolService {

    private final RolRepository repository;

    public RolServiceImpl(RolRepository repository) {
        this.repository = repository;
    }

    @Override
    public List<Rol> findAll() {
        return repository.findAll();
    }

    @Override
    public Rol findById(Integer id) {
        return repository.findById(id).orElseThrow(() -> new RuntimeException("Rol no encontrado con id: " + id));
    }

    @Override
    public void save(Rol rol) {
        repository.save(rol);
    }
}
