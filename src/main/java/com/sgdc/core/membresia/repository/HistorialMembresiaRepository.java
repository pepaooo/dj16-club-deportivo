package com.sgdc.core.membresia.repository;

import com.sgdc.core.membresia.domain.HistorialMembresia;
import com.sgdc.core.pagos.domain.PagoMembresia;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.util.List;

public interface HistorialMembresiaRepository extends JpaRepository<HistorialMembresia, Integer>, JpaSpecificationExecutor<HistorialMembresia> {

    List<HistorialMembresia> findByMiembro_IdOrderByIdDesc(Integer idMiembro);


}
