package com.sgdc.core.security.config;

import com.sgdc.core.security.filter.CaptchaAuthenticationFilter;
import com.sgdc.core.security.handler.CustomAuthenticationFailureHandler;
import com.sgdc.core.security.service.CaptchaService;
import com.sgdc.core.security.service.CustomUserDetailsService;
import com.sgdc.core.security.service.LoginAttemptService;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.ProviderManager;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import java.security.SecureRandom;

@Configuration
@EnableWebSecurity
public class SecurityConfiguration {

    private final LoginAttemptService loginAttemptService;
    private final CaptchaService captchaService;
    private final CustomAuthenticationFailureHandler failureHandler;
    private final CustomUserDetailsService uds;
    //private final CaptchaValidationFilter captchaValidationFilter;

    public SecurityConfiguration(LoginAttemptService loginAttemptService, CaptchaService captchaService, CustomAuthenticationFailureHandler failureHandler, CustomUserDetailsService uds) {
        this.loginAttemptService = loginAttemptService;
        this.captchaService = captchaService;
        this.failureHandler = failureHandler;
        this.uds = uds;
        //this.captchaValidationFilter = captchaValidationFilter;
    }

//    @Bean
//    public CaptchaAuthenticationFilter captchaFilter(AuthenticationManager authMgr) {
//        CaptchaAuthenticationFilter f =
//                new CaptchaAuthenticationFilter(loginAttemptService, captchaService);
//        f.setAuthenticationManager(authMgr);
//        f.setAuthenticationFailureHandler(failureHandler);
//        f.setUsernameParameter("username");
//        f.setPasswordParameter("password");
//        f.setFilterProcessesUrl("/login");
//        return f;
//    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http, AuthenticationManager authMgr) throws Exception {

        // 1) Crea e inyecta tu filtro custom en lugar del default
        CaptchaAuthenticationFilter caf =
                new CaptchaAuthenticationFilter(loginAttemptService, captchaService);
        caf.setAuthenticationManager(authMgr);
        caf.setAuthenticationFailureHandler(failureHandler);
        caf.setAuthenticationSuccessHandler((req, res, auth) -> {
            // reset de intentos al logueo exitoso
            loginAttemptService.loginSucceeded(auth.getName());
            res.sendRedirect("/");
        });

        http
                // Inyectamos el filtro de CAPTCHA antes de autenticar
                .addFilterAt(caf,
                        UsernamePasswordAuthenticationFilter.class)
//                .addFilterBefore(captchaValidationFilter,
//                        UsernamePasswordAuthenticationFilter.class)
                .authorizeHttpRequests((authz) -> authz
                        .requestMatchers("/bootstrap/**", "/iconos/**", "/themes/**", "/images/**",
                                "/", "/index", "/login", "doLogin",
                                "/error", "/error/**")
                        .permitAll()
                        .requestMatchers("/user").hasAnyAuthority("USER")
                        .requestMatchers("/admin").hasAnyAuthority("ADMIN")
                        .anyRequest().authenticated()
                )

//                .addFilterAt(caf,
//                        UsernamePasswordAuthenticationFilter.class)

                .formLogin(login -> login
                        .loginPage("/login") //new
                        .loginProcessingUrl("/doLogin")// POST /doLogin -> tu filter
//                        .successHandler((req, res, auth) -> {
//                            // opcional: redirige a "/" y reset failed attempts
//                            String user = auth.getName();
//                            loginAttemptService.loginSucceeded(user);
//                            res.sendRedirect("/");
//                        })
                        //   .failureHandler(failureHandler) // ya no uso failureHandler aquí, porque lo inyecto en el filtro
                        //.usernameParameter("email")
                        //.passwordParameter("pass")
                        //.loginProcessingUrl("/doLogin")
                        //    .defaultSuccessUrl("/")
                        //.successForwardUrl("/login_success_handler")
                        //.failureForwardUrl("/login_failure_handler")
                        /*.successHandler(new AuthenticationSuccessHandler() {
                            @Override
                            public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
                                System.out.println("Logged user: " + authentication.getName());
                                response.sendRedirect("/");
                            }
                        })
                        .failureHandler(new AuthenticationFailureHandler() {
                            @Override
                            public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException exception) throws IOException, ServletException {
                                System.out.println("Login failed");
                                System.out.println(exception);
                                response.sendRedirect("/login");
                            }
                        })*/
                        .permitAll())
                .logout(logout -> logout
                        //.logoutUrl("/logout")
                        .logoutSuccessUrl("/login?logout")
                        .deleteCookies("JSESSIONID") //NEW Cookies to clear
                        .invalidateHttpSession(true));
//                .csrf(Customizer.withDefaults())
//                .cors(Customizer.withDefaults()); //new
                //.userDetailsService(uds);
        return http.build();
    }

    @Bean
    PasswordEncoder passwordEncoder() {
        //return PasswordEncoderFactories.createDelegatingPasswordEncoder();
        return new BCryptPasswordEncoder(10, new SecureRandom());
        //return new BCryptPasswordEncoder(BCryptPasswordEncoder.BCryptVersion.$2Y, 12);
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider();
        authenticationProvider.setUserDetailsService(uds);
        authenticationProvider.setPasswordEncoder(passwordEncoder());
        return authenticationProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration)
            throws Exception {
        return new ProviderManager(authenticationProvider());
    }

//    @Bean
//    UserDetailsManager inMemoryUserDetailsManager() {
//        var user1 = User.withUsername("user").password("{noop}Mexico123.").roles("USER").build();
//        var user2 = User.withUsername("admin").password("{noop}Mexico123.").roles("USER", "ADMIN").build();
//        return new InMemoryUserDetailsManager(user1, user2);
//    }
}
