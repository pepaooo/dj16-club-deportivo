/*
 * Copyright (C) 2025 the original author or authors.
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

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
