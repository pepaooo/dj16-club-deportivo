package com.sgdc.core.security.config;

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
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

import java.security.SecureRandom;

@Configuration
@EnableWebSecurity
public class SecurityConfiguration {

    private final UserDetailsService uds;

    public SecurityConfiguration(UserDetailsService uds) {
        this.uds = uds;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests((authz) -> authz
                        .requestMatchers("/bootstrap/**", "/iconos/**", "/themes/**", "/images/**",
                                "/", "/index", "/login",
                                "/error", "/error/**")
                        .permitAll()
                        .requestMatchers("/user").hasAnyAuthority("USER")
                        .requestMatchers("/admin").hasAnyAuthority("ADMIN")
                        .anyRequest().authenticated()
                )
                .formLogin(login -> login
                        .loginPage("/login") //new
                        //.usernameParameter("email")
                        //.passwordParameter("pass")
                        //.loginProcessingUrl("/doLogin")
                        .defaultSuccessUrl("/")
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
                        .logoutSuccessUrl("/")
                        .deleteCookies("JSESSIONID") //NEW Cookies to clear
                        .invalidateHttpSession(true))
                .csrf(Customizer.withDefaults())
                .cors(Customizer.withDefaults()); //new
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
