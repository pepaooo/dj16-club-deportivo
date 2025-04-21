package com.sgdc.core.miembro.service;

import com.sgdc.core.miembro.domain.Miembro;
import com.sgdc.core.miembro.domain.dto.MiembroSearchDTO;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

public interface MiembroService {

    List<Miembro> findAll();

    Miembro findById(Integer id);

    List<Miembro> search(String keyword);

    List<MiembroSearchDTO> searchActive(String keyword);

    void save(Miembro miembro);

    List<Miembro> searchMiembros(Integer idMembresia, LocalDateTime fechaInicio, LocalDateTime fechaFin);

    byte[] generatePdfReport(Integer idMembresia, LocalDate fechaInicio, LocalDate fechaFin);


}
