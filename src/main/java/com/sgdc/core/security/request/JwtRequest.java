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

package com.sgdc.core.security.request;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class JwtRequest {
    private String token;
    private String tokenType;
    private Long userId;
    private String userName;
    private Long expiryDuration;
    private Collection<? extends GrantedAuthority> authorities;

    public JwtRequest(String token, Long userId, String userName, Long expiryDuration,
                      Collection<? extends GrantedAuthority> authorities) {
        this.token = token;
        this.userId = userId;
        this.userName = userName;
        this.expiryDuration = expiryDuration;
        this.authorities = authorities;
        this.tokenType = "Bearer";
    }
}
