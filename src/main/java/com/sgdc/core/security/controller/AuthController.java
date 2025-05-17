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

package com.sgdc.core.security.controller;

import com.sgdc.core.security.jwt.JWTTokenProvider;
import com.sgdc.core.security.model.UserPrincipal;
import com.sgdc.core.security.request.LoginUserRequest;
import com.sgdc.core.usuarios.domain.Usuario;
import com.sgdc.core.usuarios.domain.dto.UsuarioDTO;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.Map;

@RestController
@RequestMapping("/api/auth")
@CrossOrigin("*")
public class AuthController {

    private final AuthenticationManager authManager;
    private final JWTTokenProvider tokenProvider;

    public AuthController(AuthenticationManager authManager,
                          JWTTokenProvider tokenProvider) {
        this.authManager  = authManager;
        this.tokenProvider = tokenProvider;
    }

    @PostMapping("/login")
    public ResponseEntity<Map<String,String>> login(@RequestBody LoginUserRequest creds, Principal principal) {
        // 1) Autenticar credenciales
        Authentication auth = authManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        creds.getUsername(), creds.getPassword()
                )
        );

        // 2) Generar JWT
        UserPrincipal userDetails = (UserPrincipal) auth.getPrincipal();
        UsuarioDTO dto = principalToDTO(userDetails);
        String jwt = tokenProvider.generateJwtToken(auth, dto);

        // 3) Devolver token en JSON
        return ResponseEntity.ok(Map.of("token", jwt));
    }

    private UsuarioDTO principalToDTO(UserPrincipal userDetails) {
        Usuario usuario = userDetails.getUsuario();
        return UsuarioDTO.builder()
                .id(usuario.getId())
                .nombre(usuario.getNombre())
                .build();
    }

}

