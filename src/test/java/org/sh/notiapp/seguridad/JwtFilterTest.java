package org.sh.notiapp.seguridad;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class JwtFilterTest {

    private JwtFilter jwtFilter;
    private MockHttpServletRequest request;
    private MockHttpServletResponse response;
    private MockFilterChain filterChain;

    // Copiamos el mismo secreto que tienes en tu clase real para poder generar tokens de prueba
    private static final String SECRET = "desarrollodeaplicacionesweb20252026desarrollodeaplicacionesweb20252026";

    @BeforeEach
    void setUp() {
        // Inicializamos las herramientas simuladas antes de cada test
        jwtFilter = new JwtFilter();
        request = new MockHttpServletRequest();
        response = new MockHttpServletResponse();
        filterChain = new MockFilterChain();
    }

    @AfterEach
    void tearDown() {
        // MUY IMPORTANTE: Limpiar el contexto de seguridad para que un test no ensucie al siguiente
        SecurityContextHolder.clearContext();
    }

    @Test
    @DisplayName("Debe ignorar la petición y seguir la cadena si no hay cabecera Authorization")
    void doFilterInternal_SinCabecera_SigueLaCadena() throws ServletException, IOException {
        // GIVEN: Una petición sin cabeceras (por defecto en MockHttpServletRequest)

        // WHEN: Pasa por el filtro
        jwtFilter.doFilterInternal(request, response, filterChain);

        // THEN: El contexto de seguridad no se modifica y la petición avanza
        assertNull(SecurityContextHolder.getContext().getAuthentication());
        assertNotNull(filterChain.getRequest()); // Comprueba que se llamó a filterChain.doFilter()
    }

    @Test
    @DisplayName("Debe ignorar la petición y seguir la cadena si la cabecera no empieza por Bearer")
    void doFilterInternal_ConCabeceraInvalida_SigueLaCadena() throws ServletException, IOException {
        // GIVEN: Una cabecera que no es de tipo Bearer (ej. Basic Auth)
        request.addHeader("Authorization", "Basic usuario:password");

        // WHEN: Pasa por el filtro
        jwtFilter.doFilterInternal(request, response, filterChain);

        // THEN: Se ignora y la petición avanza
        assertNull(SecurityContextHolder.getContext().getAuthentication());
        assertNotNull(filterChain.getRequest());
    }

    @Test
    @DisplayName("Debe autenticar al usuario si el token JWT es válido")
    void doFilterInternal_ConTokenValido_AutenticaUsuario() throws ServletException, IOException {
        // GIVEN: Creamos un token real 100% válido usando la misma librería y clave
        String tokenValido = Jwts.builder()
                .setSubject("usuario123")
                .claim("role", List.of("ADMIN", "USER"))
                .signWith(Keys.hmacShaKeyFor(SECRET.getBytes(StandardCharsets.UTF_8)))
                .compact();

        request.addHeader("Authorization", "Bearer " + tokenValido);

        // WHEN: Pasa por el filtro
        jwtFilter.doFilterInternal(request, response, filterChain);

        // THEN: Verificamos que Spring Security haya guardado al usuario
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        assertNotNull(auth);
        assertEquals("usuario123", auth.getPrincipal());
        assertTrue(auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN")));
        assertTrue(auth.getAuthorities().stream().anyMatch(a -> a.getAuthority().equals("ROLE_USER")));

        // Y comprobamos que la petición avanza hacia el controlador
        assertNotNull(filterChain.getRequest());
    }

    @Test
    @DisplayName("Debe devolver 401 Unauthorized y detener la cadena si el token es falso o ha caducado")
    void doFilterInternal_ConTokenInvalido_Devuelve401() throws ServletException, IOException {
        // GIVEN: Un token inventado que no pasará la firma digital
        request.addHeader("Authorization", "Bearer eyJhbGciOiJIUzI1NiJ9.tokenfalso.falso");

        // WHEN: Pasa por el filtro
        jwtFilter.doFilterInternal(request, response, filterChain);

        // THEN: El filtro bloquea la petición, devuelve estado 401 y NUNCA llama a filterChain.doFilter()
        assertEquals(HttpServletResponse.SC_UNAUTHORIZED, response.getStatus());
        assertNull(filterChain.getRequest()); // Al ser null, verificamos que no avanzó
        assertNull(SecurityContextHolder.getContext().getAuthentication());
    }
}