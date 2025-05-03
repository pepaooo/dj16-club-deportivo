package com.sgdc.core.security.model;

import com.sgdc.core.security.service.LoginAttemptService;
import com.sgdc.core.usuarios.domain.Usuario;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class UserPrincipal implements UserDetails {
    @Getter
    private final Usuario usuario;
    private final Collection<? extends GrantedAuthority> authorities;

    public UserPrincipal(Usuario usuario, Collection<? extends GrantedAuthority> authorities) {
        this.usuario = usuario;
        this.authorities = authorities;
    }

    public static UserPrincipal build(Usuario usuario) {
        List<GrantedAuthority> authorities = usuario.getRoles().stream().map(role ->
                new SimpleGrantedAuthority(role.getNombre())
        ).collect(Collectors.toList());
        return new UserPrincipal(
                usuario,
                authorities
        );
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
//        if (null == usuario.getRoles()) {
//            return Collections.emptySet();
//        }
//        Set<SimpleGrantedAuthority> grantedAuthorities = new HashSet<>();
//        for (Rol role : usuario.getRoles()) {
//            grantedAuthorities.add(new SimpleGrantedAuthority(role.getNombre()));
//        }
//        return grantedAuthorities;
        return authorities;
    }

    /**
     * getUsername
     *
     * @return username
     */
    @Override
    public String getUsername() {
        return usuario.getNombre();
    }

    /**
     * getPassword (OTP)
     *
     * @return password
     */
    @Override
    public String getPassword() {
        return usuario.getContrasena();
    }

    /**
     * getName
     *
     * @return name
     */
    public String getName() {
        return usuario.getMiembro() != null ? usuario.getMiembro().getNombre() : usuario.getNombre();
    }

    /**
     * getEmail
     *
     * @return email
     */
    public String getEmail() {
        return usuario.getMiembro() != null ? usuario.getMiembro().getCorreoElectronico() : "";
    }

    /**
     * isEnabled
     *
     * @return if user is enabled
     */
    @Override
    public boolean isEnabled() {
        return usuario.getEstatus().equalsIgnoreCase("Activo");
    }

    /**
     * isAccountNonLocked
     *
     * @return if user is locked
     */
    @Override
    public boolean isAccountNonLocked() {
        // si no hay lockTime → está desbloqueado
        if (usuario.getLockTime() == null) return true;
        // si ya expiró el lock → también
        if (usuario.isLockTimeExpired(LoginAttemptService.LOCK_DURATION_MIN)) {
            return true;
        }
        // caso contrario → bloqueado
        return false;
    }

    /**
     * isAccountNonExpired
     *
     * @return if account is not expired
     */
    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    /**
     * isCredentialsNonExpired
     *
     * @return if credential is not expired
     */
    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

}
