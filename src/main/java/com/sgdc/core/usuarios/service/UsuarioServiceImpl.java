package com.sgdc.core.usuarios.service;

import com.sgdc.core.auditoria.aop.Auditable;
import com.sgdc.core.miembro.domain.Miembro;
import com.sgdc.core.miembro.repository.MiembroRepository;
import com.sgdc.core.usuarios.domain.Rol;
import com.sgdc.core.usuarios.domain.Usuario;
import com.sgdc.core.usuarios.domain.dto.RolInfo;
import com.sgdc.core.usuarios.domain.dto.UsuarioDetalleDTO;
import com.sgdc.core.usuarios.repository.RolRepository;
import com.sgdc.core.usuarios.repository.UsuarioRepository;
import jakarta.persistence.EntityNotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.security.crypto.password.PasswordEncoder;
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

    private final PasswordEncoder passwordEncoder;

    public UsuarioServiceImpl(UsuarioRepository repository, RolRepository rolRepository, MiembroRepository miembroRepository, PasswordEncoder passwordEncoder) {
        this.repository = repository;
        this.rolRepository = rolRepository;
        this.miembroRepository = miembroRepository;
        this.passwordEncoder = passwordEncoder;
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

        // Campos de auditoría
        dto.setFechaCreacion(usuario.getFechaCreacion());
        dto.setCreadoPor(usuario.getCreadoPor());
        dto.setFechaModificacion(usuario.getFechaModificacion());
        dto.setModificadoPor(usuario.getModificadoPor());

        return dto;
    }

    @Override
    public List<UsuarioDetalleDTO> search(String keyword) {
        return repository.searchUsuarios(keyword);
    }

    @Auditable(
            tipoAccion = "CREATE",
            tabla = "usuario",
            entidadId = "#result.id",
            descripcion = "'Creación del usuario '+#result.nombre"
    )
    @Override
    public UsuarioDetalleDTO save(UsuarioDetalleDTO dto) {
        // Validar si el usuario tiene un miembro asignado en caso de tener rol de "Miembro"
        Integer membRoleId = rolRepository.findByNombreIgnoreCase("Miembro")
                .orElseThrow(() -> new EntityNotFoundException("Rol 'Miembro' no encontrado.")).getId();
        if (dto.getRolesIds() != null && dto.getRolesIds().contains(membRoleId) && dto.getIdMiembro() == null) {
            throw new IllegalArgumentException("Un usuario con rol MIEMBRO debe tener un Miembro asociado.");
        }

        dto.setContrasena(passwordEncoder.encode(dto.getContrasena()));
        Usuario usuario = toEntity(dto);
        log.info("Usuario encontrado: " + usuario);
        // Si no se especifica el estatus se asigna "Activo"
        if (dto.getEstatus() == null || dto.getEstatus().isBlank()) {
            usuario.setEstatus("Activo");
        }
        // Intentos fallidos
        usuario.setFailedAttempt(0);
        return toDTO(repository.save(usuario));
    }

    @Auditable(
            tipoAccion = "UPDATE",
            tabla = "usuario",
            entidadId = "#result.id",
            descripcion = "'Actualización del usuario '+#result.nombre"
    )
    @Override
    public UsuarioDetalleDTO update(UsuarioDetalleDTO dto) {
        // Validar si el usuario tiene un miembro asignado en caso de tener rol de "Miembro"
        Integer membRoleId = rolRepository.findByNombreIgnoreCase("Miembro")
                .orElseThrow(() -> new EntityNotFoundException("Rol 'Miembro' no encontrado.")).getId();
        if (dto.getRolesIds() != null && dto.getRolesIds().contains(membRoleId) && dto.getIdMiembro() == null) {
            throw new IllegalArgumentException("Un usuario con rol MIEMBRO debe tener un Miembro asociado.");
        }

        Usuario usuario = findById(dto.getId());
        // Actualizar los campos del usuario
        if (dto.getContrasena() != null) {
            usuario.setContrasena(passwordEncoder.encode(dto.getContrasena()));
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
        return toDTO(repository.save(usuario));
    }

    @Auditable(
            tipoAccion = "UPDATE",
            tabla = "usuario",
            entidadId = "#result.id",
            descripcion = "'Activación del usuario '+#result.nombre"
    )
    @Override
    public UsuarioDetalleDTO activate(Integer id) {
        Usuario usuario = findById(id);
        usuario.setEstatus("Activo");
        return toDTO(repository.save(usuario));
    }

    @Auditable(
            tipoAccion = "UPDATE",
            tabla = "usuario",
            entidadId = "#result.id",
            descripcion = "'Inactivación del usuario '+#result.nombre"
    )
    @Override
    public UsuarioDetalleDTO deactivate(Integer id) {
        Usuario usuario = findById(id);
        usuario.setEstatus("Inactivo");
        return toDTO(repository.save(usuario));
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

    private UsuarioDetalleDTO toDTO(Usuario usuario) {
        UsuarioDetalleDTO dto = new UsuarioDetalleDTO();
        dto.setId(usuario.getId());
        dto.setNombre(usuario.getNombre());
        dto.setContrasena(usuario.getContrasena());
        dto.setEstatus(usuario.getEstatus());
        dto.setFechaCreacion(usuario.getFechaCreacion());
        dto.setUltimoAcceso(usuario.getUltimoAcceso());
        dto.setIdMiembro(usuario.getMiembro() != null ? usuario.getMiembro().getId() : null);
        return dto;
    }
}
