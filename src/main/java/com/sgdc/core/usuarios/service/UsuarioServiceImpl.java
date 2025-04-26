package com.sgdc.core.usuarios.service;

import com.sgdc.core.membresia.domain.Beneficio;
import com.sgdc.core.miembro.domain.Miembro;
import com.sgdc.core.miembro.repository.MiembroRepository;
import com.sgdc.core.usuarios.domain.Rol;
import com.sgdc.core.usuarios.domain.Usuario;
import com.sgdc.core.usuarios.domain.dto.RolInfo;
import com.sgdc.core.usuarios.domain.dto.UsuarioDetalleDTO;
import com.sgdc.core.usuarios.repository.RolRepository;
import com.sgdc.core.usuarios.repository.UsuarioRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.criteria.Expression;
import jakarta.persistence.criteria.Join;
import jakarta.persistence.criteria.JoinType;
import jakarta.persistence.criteria.Predicate;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class UsuarioServiceImpl implements UsuarioService {

    private static final Logger log = LoggerFactory.getLogger(UsuarioServiceImpl.class);
    private final UsuarioRepository repository;

    private final RolRepository rolRepository;

    private final MiembroRepository miembroRepository;

    public UsuarioServiceImpl(UsuarioRepository repository, RolRepository rolRepository, MiembroRepository miembroRepository) {
        this.repository = repository;
        this.rolRepository = rolRepository;
        this.miembroRepository = miembroRepository;
    }

    @Override
    public List<Usuario> findAll() {
        return repository.findAllByOrderByIdDesc();
    }

    @Override
    public Usuario findById(Integer id) {
        return repository.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con id: " + id));
    }

    @Override
    public UsuarioDetalleDTO findUsuarioDetalleDTOById(Integer id) {
        // Obtenemos el DTO
        UsuarioDetalleDTO dto = repository.searchById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado con ID: " + id));
        // Obtenemos el detalle de los roles del usuario
        Usuario usuario = repository.findById(id).orElseThrow(() -> new RuntimeException("Usuario no encontrado con ID: " + id));
        Set<Integer> rolesIds = usuario.getRoles().stream()
                .map(Rol::getId).collect(Collectors.toSet());
        // Asignar los IDs de los roles
        dto.setRolesIds(rolesIds);
        Set<RolInfo> roles = usuario.getRoles().stream()
                .map(rol -> new RolInfo(rol.getNombre(), rol.getDescripcion())
                ).collect(Collectors.toSet());
        dto.setRoles(roles);
        return dto;
    }

    @Override
    public List<UsuarioDetalleDTO> search(String keyword) {
        return repository.searchUsuarios(keyword);
    }

    @Override
    public void save(UsuarioDetalleDTO dto) {
        Usuario usuario = toEntity(dto);
        log.info("Usuario encontrado: " + usuario);
        // Si no se especifica el estatus se asigna "Activo"
        if (dto.getEstatus() == null || dto.getEstatus().isBlank()) {
            usuario.setEstatus("Activo");
        }
        repository.save(usuario);
    }

    @Override
    public void update(UsuarioDetalleDTO dto) {
        Usuario usuario = findById(dto.getId());
        // Actualizar los campos del usuario
        if (dto.getContrasena() != null) {
            usuario.setContrasena(dto.getContrasena());
        }
        // Actualizar el miembro
        if (dto.getIdMiembro() != null) {
            Miembro miembro = miembroRepository.findById(dto.getIdMiembro())
                    .orElseThrow(() -> new EntityNotFoundException("Miembro no encontrado con ID: " + dto.getIdMiembro()));
            usuario.setMiembro(miembro);
        } else {
            usuario.setMiembro(null);
        }
        // Actualizar los roles
        List<Rol> roles = rolRepository.findAllById(dto.getRolesIds());
        usuario.setRoles(new HashSet<>(roles));
        // Guardar el usuario actualizado
        repository.save(usuario);
    }

    @Override
    public void activate(Integer id) {
        Usuario usuario = findById(id);
        usuario.setEstatus("Activo");
        repository.save(usuario);
    }

    @Override
    public void deactivate(Integer id) {
        Usuario usuario = findById(id);
        usuario.setEstatus("Inactivo");
        repository.save(usuario);
    }

    private Usuario toEntity(UsuarioDetalleDTO dto) {
        Usuario usuario = new Usuario();
        usuario.setId(dto.getId());
        usuario.setNombre(dto.getNombre());
        usuario.setContrasena(dto.getContrasena());
        usuario.setEstatus(dto.getEstatus());
        usuario.setFechaCreacion(dto.getFechaCreacion());
        usuario.setUltimoAcceso(dto.getUltimoAcceso());
        usuario.setMiembro(dto.getIdMiembro() != null
                ? miembroRepository.findById(dto.getIdMiembro()).orElseThrow(() -> new RuntimeException("Miembro no encontrado con ID: " + dto.getIdMiembro())) : null);
        // buscar y asignar beneficios
        List<Rol> roles = rolRepository.findAllById(dto.getRolesIds());
        usuario.setRoles(new HashSet<>(roles));
        return usuario;
    }
}
