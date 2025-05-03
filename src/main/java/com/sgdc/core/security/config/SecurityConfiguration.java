package com.sgdc.core.security.config;

import com.sgdc.core.security.filter.CaptchaValidationFilter;
import com.sgdc.core.security.handler.CustomAuthenticationFailureHandler;
import com.sgdc.core.security.service.CustomUserDetailsService;
import com.sgdc.core.security.service.LoginAttemptService;
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
    private final LoginAttemptService loginAttemptService;
    private final CaptchaValidationFilter captchaFilter;
    private final CustomAuthenticationFailureHandler customAuthenticationFailureHandler;

    public SecurityConfiguration(CustomUserDetailsService uds, LoginAttemptService loginAttemptService, CaptchaValidationFilter captchaFilter, CustomAuthenticationFailureHandler customAuthenticationFailureHandler) {
        this.uds = uds;
        this.loginAttemptService = loginAttemptService;
        this.captchaFilter = captchaFilter;
        this.customAuthenticationFailureHandler = customAuthenticationFailureHandler;
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
                .addFilterBefore(captchaFilter,
                        UsernamePasswordAuthenticationFilter.class)

                // 1) Provider
                .authenticationProvider(authenticationProvider())

                // 2) CSRF activo
                .csrf(csrf -> csrf
                        .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
                )

                // 3) Autorizar rutas
                .authorizeHttpRequests(auth -> auth
                        // recursos estáticos
                        //.requestMatchers(PathRequest.toStaticResources().atCommonLocations()).permitAll()
                        .requestMatchers("/bootstrap/**", "/iconos/**", "/themes/**", "/images/**",
                                "/", "/index", "/login",
                                "/error", "/error/**")
                        .permitAll()
                        .requestMatchers("/user").hasAnyAuthority("USER")
                        .requestMatchers("/admin").hasAnyAuthority("ADMIN")
                        .anyRequest().authenticated()
                )

                // 4) Configuración de form login
                .formLogin(form -> form
                        .loginPage("/login")                  // GET /login → plantilla
                        .loginProcessingUrl("/doLogin")       // POST /doLogin → procesa credenciales
                        .defaultSuccessUrl("/", true)         // a dónde vas si ok
                        //.failureUrl("/login?error")           // a dónde vas si bad creds
                        .successHandler((req, res, auth) -> {
                            loginAttemptService.loginSucceeded(auth.getName());
                            res.sendRedirect("/");
                        })
                        .failureHandler(customAuthenticationFailureHandler)
                        .permitAll()
                )

                // 5) logout
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/login")
                        .invalidateHttpSession(true)
                        .deleteCookies("JSESSIONID")
                )
                .cors(Customizer.withDefaults());

        return http.build();
    }
}
