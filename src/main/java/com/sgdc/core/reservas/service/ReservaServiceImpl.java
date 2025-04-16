package com.sgdc.core.reservas.service;

import com.sgdc.core.miembro.domain.Miembro;
import com.sgdc.core.reservas.domain.Reserva;
import com.sgdc.core.reservas.repository.ReservaRepository;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class ReservaServiceImpl implements ReservaService {

    private final ReservaRepository repository;

    public ReservaServiceImpl(ReservaRepository repository) {
        this.repository = repository;
    }

    @Override
    public List<Reserva> findAll() {
        return repository.findAll();
    }

    @Override
    public Optional<Reserva> findById(Integer id) {
        return repository.findById(id);
    }

    @Override
    public List<Reserva> search(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return repository.findAllByOrderByIdDesc();
        }
        String pattern = "%" + keyword.toLowerCase() + "%";

        Specification<Reserva> spec = (root, query, cb) -> {

            // JOIN con miembro
            Join<Reserva, Miembro> miembroJoin = root.join("miembro", JoinType.LEFT);
            Expression<String> nombreMiembroExpr = cb.lower(miembroJoin.get("nombre"));

            // Para atributos de tipo String se hace directamente.
            Expression<String> estadoReservaExpr = cb.lower(root.get("estadoReserva"));

            // Para las fechas, podemos usar una función de formateo. Por ejemplo, en MariaDB se puede usar DATE_FORMAT.
            Expression<String> fechaHoraInicioExpr = cb.function("DATE_FORMAT", String.class, root.get("fechaHoraInicio"), cb.literal("%Y-%m-%d"));
            Expression<String> fechaHoraFinExpr = cb.function("DATE_FORMAT", String.class, root.get("fechaHoraFin"), cb.literal("%Y-%m-%d"));

            return cb.or(
                    cb.like(nombreMiembroExpr, pattern),
                    cb.like(estadoReservaExpr, pattern),
                    cb.like(fechaHoraInicioExpr, pattern),
                    cb.like(fechaHoraFinExpr, pattern)
            );
        };

        return repository.findAll(spec, Sort.by(Sort.Direction.DESC, "id"));
    }

    @Override
    public void save(Reserva reserva) {
        repository.save(reserva);
    }
}
