package com.sgdc.core.security.config;

import com.sgdc.core.security.filter.CaptchaValidationFilter;
import com.sgdc.core.security.filter.IpRateLimitingFilter;
import com.sgdc.core.security.handler.CustomAuthFailureHandler;
import com.sgdc.core.security.handler.CustomAuthSuccessHandler;
import com.sgdc.core.security.handler.CustomLogoutSuccessHandler;
import com.sgdc.core.security.jwt.JWTAuthenticationFilter;
import com.sgdc.core.security.jwt.JWTTokenProvider;
import com.sgdc.core.security.service.CustomUserDetailsService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
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
    private final CustomLogoutSuccessHandler customLogoutSuccessHandler;
    private final JWTTokenProvider tokenProvider;

    public SecurityConfiguration(CustomUserDetailsService uds, IpRateLimitingFilter ipRateLimitingFilter, CaptchaValidationFilter captchaValidationFilter, CustomAuthFailureHandler customAuthFailureHandler, CustomAuthSuccessHandler customAuthSuccessHandler, CustomLogoutSuccessHandler customLogoutSuccessHandler, JWTTokenProvider tokenProvider) {
        this.uds = uds;
        this.ipRateLimitingFilter = ipRateLimitingFilter;
        this.captchaValidationFilter = captchaValidationFilter;
        this.customAuthFailureHandler = customAuthFailureHandler;
        this.customAuthSuccessHandler = customAuthSuccessHandler;
        this.customLogoutSuccessHandler = customLogoutSuccessHandler;
        this.tokenProvider = tokenProvider;
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
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration)
            throws Exception {
        return new ProviderManager(authenticationProvider());
    }

    // 1) Filtro para API REST
    @Bean
    @Order(1)
    public SecurityFilterChain apiSecurity(HttpSecurity http) throws Exception {
        http
                .securityMatcher("/api/**")
                .csrf(AbstractHttpConfigurer::disable)                     // APIs suelen desactivar CSRF
                .sessionManagement(sm -> sm
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS) // stateless para APIs
                )
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/auth/login").permitAll()   // <-- permitir login
                        .anyRequest().authenticated()
                )
                .addFilterBefore(new JWTAuthenticationFilter(tokenProvider, uds), UsernamePasswordAuthenticationFilter.class);
//                .authorizeHttpRequests(auth -> auth
//                        .anyRequest().hasAnyAuthority("ADMIN")
//                )
//                .httpBasic(Customizer.withDefaults());            // Basic Auth para APIs
        return http.build();
    }

    @Bean
    @Order(2)
    public SecurityFilterChain uiSecurity(HttpSecurity http) throws Exception {
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
                //.csrf(AbstractHttpConfigurer::disable)
                .cors(Customizer.withDefaults())
                .sessionManagement(sm ->
                        sm.sessionCreationPolicy(SessionCreationPolicy.IF_REQUIRED)
                )

                .addFilterAfter(new JWTAuthenticationFilter(tokenProvider, uds), UsernamePasswordAuthenticationFilter.class)
//                .sessionManagement(session -> session
//                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                // Autorizar rutas
                .authorizeHttpRequests(auth -> auth
                        // recursos estáticos
                        //.requestMatchers(PathRequest.toStaticResources().atCommonLocations()).permitAll()
                        .requestMatchers("/bootstrap/**", "/iconos/**", "/themes/**", "/images/**",
                                "/", "/index", "/login", "/doLogin", "/captcha", "/captcha/**",
                                "/error", "/error/**", "/.well-known/appspecific/**")
                        .permitAll()
                        .requestMatchers("/miembros/**").hasAnyAuthority("ADMIN", "STAFF", "GERENTE")
                        .requestMatchers("/pagos/**").hasAnyAuthority("ADMIN", "STAFF", "GERENTE")
                        .requestMatchers("/reservas/**").hasAnyAuthority("ADMIN", "STAFF", "GERENTE")
                        .requestMatchers("/reportes/**").hasAnyAuthority("ADMIN", "STAFF", "GERENTE")
                        .requestMatchers("/membresias/**").hasAnyAuthority("ADMIN", "GERENTE")
                        .requestMatchers("/beneficios/**").hasAnyAuthority("ADMIN", "GERENTE")
                        .requestMatchers("/notificaciones/**").hasAnyAuthority("ADMIN", "GERENTE")
                        .requestMatchers("/membresias/**").hasAnyAuthority("ADMIN", "GERENTE")
                        .requestMatchers("/instalaciones/**").hasAnyAuthority("ADMIN", "GERENTE")
                        .requestMatchers("/usuarios/**").hasAnyAuthority("ADMIN")
                        .requestMatchers("/auditoria/**").hasAnyAuthority("ADMIN", "GERENTE")
                        .requestMatchers("/configuracion/**").hasAnyAuthority("ADMIN")
                        .anyRequest().authenticated()
                )

                // Logout
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/login")
                        .deleteCookies("JSESSIONID")
                        .logoutSuccessHandler(customLogoutSuccessHandler)
                        .clearAuthentication(true)
                        .invalidateHttpSession(true)
                );

        return http.build();
    }
}
