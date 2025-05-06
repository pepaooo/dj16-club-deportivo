package com.sgdc.core.membresia.service;

import com.sgdc.core.membresia.domain.Membresia;
import com.sgdc.core.membresia.domain.dto.MembresiaDTO;

import java.util.List;
import java.util.Optional;

public interface MembresiaService {

    List<Membresia> findAll();

    List<Membresia> findActive();

    MembresiaDTO findById(Integer id);

    List<Membresia> search(String keyword);

    MembresiaDTO save(MembresiaDTO membresia);

    MembresiaDTO update(MembresiaDTO membresia);

    void activateMembresia(Integer id);

    void deactivateMembresia(Integer id);

}
