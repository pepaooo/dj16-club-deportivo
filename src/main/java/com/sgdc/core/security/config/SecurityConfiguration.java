package com.sgdc.core.security.config;

import com.sgdc.core.security.filter.CaptchaValidationFilter;
import com.sgdc.core.security.filter.IpRateLimitingFilter;
import com.sgdc.core.security.handler.CustomAuthFailureHandler;
import com.sgdc.core.security.handler.CustomAuthSuccessHandler;
import com.sgdc.core.security.service.CustomUserDetailsService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;

@Configuration
@EnableWebSecurity
public class SecurityConfiguration {

    private final CustomUserDetailsService uds;
    private final IpRateLimitingFilter ipRateLimitingFilter;
    private final CaptchaValidationFilter captchaValidationFilter;
    private final CustomAuthFailureHandler customAuthFailureHandler;
    private final CustomAuthSuccessHandler customAuthSuccessHandler;


    public SecurityConfiguration(CustomUserDetailsService uds, IpRateLimitingFilter ipRateLimitingFilter, CaptchaValidationFilter captchaValidationFilter, CustomAuthFailureHandler customAuthFailureHandler, CustomAuthSuccessHandler customAuthSuccessHandler) {
        this.uds = uds;
        this.ipRateLimitingFilter = ipRateLimitingFilter;
        this.captchaValidationFilter = captchaValidationFilter;
        this.customAuthFailureHandler = customAuthFailureHandler;
        this.customAuthSuccessHandler = customAuthSuccessHandler;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider p = new DaoAuthenticationProvider();
        p.setUserDetailsService(uds);
        p.setPasswordEncoder(passwordEncoder());
        return p;
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http

                // Configurar formLogin primero, con los handlers
                .formLogin(form -> form
                        .loginPage("/login")
                        .loginProcessingUrl("/doLogin")
                        .successHandler(customAuthSuccessHandler)
                        .failureHandler(customAuthFailureHandler)
                        .permitAll()
                )

                // Primero: limitamos el número de POST /doLogin por IP
                .addFilterBefore(ipRateLimitingFilter,
                        UsernamePasswordAuthenticationFilter.class)
                // Segundo: validamos el CAPTCHA si toca
                .addFilterBefore(captchaValidationFilter,
                        UsernamePasswordAuthenticationFilter.class)

                // Provider
                .authenticationProvider(authenticationProvider())

                // Configuración de CSRF/CORS
                .csrf(csrf -> csrf
                        .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse()))
                .cors(Customizer.withDefaults())

                // Autorizar rutas
                .authorizeHttpRequests(auth -> auth
                                // recursos estáticos
                                //.requestMatchers(PathRequest.toStaticResources().atCommonLocations()).permitAll()
                                .requestMatchers("/bootstrap/**", "/iconos/**", "/themes/**", "/images/**",
                                        "/", "/index", "/login", "/doLogin", "/captcha", "/captcha/**",
                                        "/error", "/error/**")
                                .permitAll()
//                        .requestMatchers("/user").hasAnyAuthority("USER")
//                        .requestMatchers("/admin").hasAnyAuthority("ADMIN")
                                .anyRequest().authenticated()
                )

                // Logout
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/login")
                        .invalidateHttpSession(true)
                        .deleteCookies("JSESSIONID")
                );

        return http.build();
    }
}
