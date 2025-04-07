package com.sgdc.core.pagos.repository;

import com.sgdc.core.pagos.domain.PagoMembresia;
import com.sgdc.core.pagos.domain.dto.PagoMembresiaResumenDTO;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface PagoMembresiaRepository extends JpaRepository<PagoMembresia, Integer>, JpaSpecificationExecutor<PagoMembresia> {

    List<PagoMembresia> findByMiembro_IdOrderByIdDesc(Integer idMiembro);

    boolean existsByMiembro_Id(Integer idMiembro);

    @Query("SELECT new com.sgdc.core.pagos.domain.dto.PagoMembresiaResumenDTO(" +
            "p.id, " +
            "m.nombre, " +
            "m.apellidoPaterno, " +
            "m.apellidoMaterno, " +
            "tm.nombre, " +
            "p.monto, " +
            "p.fechaInicio, " +
            "p.fechaFin, " +
            "CASE WHEN p.fechaInicio <= CURRENT_TIMESTAMP AND p.fechaFin >= CURRENT_TIMESTAMP THEN 'Activo' ELSE 'Vencido' END" +
            ") " +
            "FROM Miembro m " +
            "LEFT JOIN PagoMembresia p ON m.id = p.miembro.id " +
            "LEFT JOIN Membresia tm ON p.membresia.id = tm.id " +
            "WHERE p.fechaInicio = (" +
            "   SELECT MAX(p2.fechaInicio) FROM PagoMembresia p2 " +
            "   WHERE p2.miembro.id = m.id AND p2.fechaInicio <= CURRENT_TIMESTAMP) " +
            "ORDER BY p.id DESC "
            )
    List<PagoMembresiaResumenDTO> findResumenPagos();

    @Query("SELECT new com.sgdc.core.pagos.domain.dto.PagoMembresiaResumenDTO(" +
            "p.id, m.nombre, m.apellidoPaterno, m.apellidoMaterno, tm.nombre, p.monto , p.fechaInicio, p.fechaFin, " +
            "CASE WHEN p.fechaInicio <= CURRENT_TIMESTAMP AND p.fechaFin >= CURRENT_TIMESTAMP THEN 'Activo' ELSE 'Vencido' END" +
            ") " +
            "FROM PagoMembresia p " +
            "LEFT JOIN p.miembro m " +
            "LEFT JOIN p.membresia tm " +
            "WHERE p.fechaInicio = (" +
            "   SELECT MAX(p2.fechaInicio) FROM PagoMembresia p2 " +
            "   WHERE p2.miembro.id = m.id AND p2.fechaInicio <= CURRENT_TIMESTAMP) " +
            "AND (LOWER(m.nombre) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
            "   OR LOWER(m.apellidoPaterno) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
            "   OR LOWER(m.apellidoMaterno) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
            "   OR LOWER(tm.nombre) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
//            "   OR LOWER(CAST(p.monto AS string)) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
//            "   OR LOWER(FUNCTION('DATE_FORMAT', p.fechaPago, '%Y-%m-%d')) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
            "   OR LOWER(FUNCTION('DATE_FORMAT', p.fechaInicio, '%Y-%m-%d')) LIKE LOWER(CONCAT('%', :keyword, '%')) " +
            "   OR LOWER(FUNCTION('DATE_FORMAT', p.fechaFin, '%Y-%m-%d')) LIKE LOWER(CONCAT('%', :keyword, '%'))" +
            ") ORDER BY p.id DESC")
    List<PagoMembresiaResumenDTO> searchResumen(@Param("keyword") String keyword);


}
