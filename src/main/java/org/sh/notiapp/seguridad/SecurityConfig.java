package org.sh.notiapp.seguridad;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
public class SecurityConfig {

    private final JwtFilter jwtFilter;

    public SecurityConfig(JwtFilter jwtFilter) {
        this.jwtFilter = jwtFilter;
    }
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {

        return http
                .csrf(csrf -> csrf.disable())
                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint((req, res, e) ->
                                res.sendError(HttpServletResponse.SC_UNAUTHORIZED))
                )
                .authorizeHttpRequests(auth -> auth

                        // POST solo para VICERRECTORADO
                        .requestMatchers(HttpMethod.POST, "/notificaciones")
                        .hasAnyRole("VICERRECTORADO","ADMINISTRADOR")

                        // GET requiere estar autenticado
                        .requestMatchers(HttpMethod.GET, "/notificaciones/**")
                        .authenticated()

                        // DELETE solo para ADMINISTRADOR
                        .requestMatchers(HttpMethod.DELETE, "/notificaciones/**").hasRole("ADMINISTRADOR")

                        // PUT solo para ADMINISTRADOR
                        .requestMatchers(HttpMethod.PUT, "/notificaciones/**").hasRole("ADMINISTRADOR")

                        // POST PENDIENTES solo para ADMINISTRADOR
                        .requestMatchers(HttpMethod.POST, "/pendientes/**").hasRole("ADMINISTRADOR")

                        // Todo lo demás prohibido
                        .anyRequest().denyAll()
                )
                .addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class)
                .build();
    }
}