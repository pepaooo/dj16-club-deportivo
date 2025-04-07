package com.sgdc.core.miembro.service;

import com.sgdc.core.miembro.domain.Miembro;

import java.util.List;
import java.util.Optional;

public interface MiembroService {

    List<Miembro> findAll();

    Miembro findById(Integer id);

    List<Miembro> search(String keyword);

    void save(Miembro miembro);

}
