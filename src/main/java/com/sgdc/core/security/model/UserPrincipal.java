package com.sgdc.core.security.model;

import com.sgdc.core.security.service.LoginAttemptService;
import com.sgdc.core.usuarios.domain.Rol;
import com.sgdc.core.usuarios.domain.Usuario;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.*;
import java.util.stream.Collectors;

public class UserPrincipal implements UserDetails {

    @Getter
    private final Usuario usuario;
    private final Collection<? extends GrantedAuthority> authorities;
    private final boolean accountNonLocked;

    public UserPrincipal(Usuario usuario, Collection<? extends GrantedAuthority> authorities, boolean accountNonLocked) {
        this.usuario = usuario;
        this.authorities = authorities;
        this.accountNonLocked = accountNonLocked;
    }

    public static UserPrincipal build(Usuario usuario, LoginAttemptService attemptService) {
        List<GrantedAuthority> authorities = usuario.getRoles().stream().map(role ->
                new SimpleGrantedAuthority(role.getNombre())
        ).collect(Collectors.toList());

        // Aquí pedimos al servicio si aún está bloqueado
        boolean nonLocked = !attemptService.isLocked(usuario.getNombre());

        return new UserPrincipal(
                usuario,
                authorities,
                nonLocked
        );
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
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
        return this.accountNonLocked;
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
