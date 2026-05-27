package org.sh.notiapp.controladores;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.sh.notiapp.entidades.Notificacion;
import org.sh.notiapp.excepciones.NotificacionNoEncontrada;
import org.sh.notiapp.servicios.NotificacionServicio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/* ====================================================================================
 * CONFIGURACIÓN DEL ENTORNO DE PRUEBAS
 * ==================================================================================== */

// 1. Levantamos todo el entorno de Spring Boot y sobreescribimos la base de datos.
// Usamos "properties" para aislar los tests de los datos reales del ordenador.
// En lugar del archivo local (jdbc:h2:file), forzamos una base de datos en memoria
// (mem:testdb) que se crea vacía al empezar y se destruye al terminar (create-drop).
@SpringBootTest(properties = {
                "spring.datasource.url=jdbc:h2:mem:testdb",
                "spring.jpa.hibernate.ddl-auto=create-drop"
})

// 2. Prepara el "MockMvc" para lanzar peticiones HTTP falsas a nuestro
// controlador.
// Usamos "addFilters = false" para APAGAR temporalmente Spring Security
// (JwtFilter).
// Si no lo hacemos, cualquier POST, PUT o DELETE nos dará un error 403
// Forbidden
// porque los tests no tienen un token JWT real.
@AutoConfigureMockMvc(addFilters = false)

// 3. Simula un usuario autenticado básico para pasar las comprobaciones de
// seguridad.
@WithMockUser
class NotificacionControladorTest {

        @Autowired
        private MockMvc mockMvc;

        @MockBean
        private NotificacionServicio servicio;

        private Notificacion notificacionFalsa;

        @BeforeEach
        void setUp() {
                notificacionFalsa = new Notificacion();
                notificacionFalsa.setId(1L);
        }

        @Test
        @DisplayName("GET /notificaciones - Debe devolver 200 y una lista de DTOs")
        void obtenerTodasNotificaciones_DevuelveLista() throws Exception {
                Mockito.when(servicio.obtenerNotificacionesFiltradas(any(), any()))
                                .thenReturn(List.of(notificacionFalsa));

                mockMvc.perform(get("/notificaciones"))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$").isArray())
                                .andExpect(jsonPath("$[0].id").value(1));
        }

        @Test
        @DisplayName("GET /notificaciones/{id} - Debe devolver 200 y la notificación correcta")
        void obtenerUna_DevuelveNotificacion() throws Exception {
                Mockito.when(servicio.obtenerNotificacionPorId(1L))
                                .thenReturn(notificacionFalsa);

                mockMvc.perform(get("/notificaciones/1"))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.id").value(1));
        }

        @Test
        @DisplayName("GET /notificaciones - 200 y lista vacía")
        void obtenerTodas_Vacia() throws Exception {

                Mockito.when(servicio.obtenerNotificacionesFiltradas(any(), any()))
                                .thenReturn(List.of());

                mockMvc.perform(get("/notificaciones"))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$").isArray())
                                .andExpect(jsonPath("$.length()").value(0));
        }

        @Test
        @DisplayName("POST /notificaciones - Debe devolver 201 Created")
        void aniadir_CreaNotificacion() throws Exception {
                Mockito.when(servicio.aniadirNotificacion(any(Notificacion.class)))
                                .thenReturn(notificacionFalsa);

                // Le damos a la petición un JSON válido con todos los campos obligatorios
                String jsonEntrada = """
                                {
                                    "asunto": "Prueba de notificación",
                                    "cuerpo": "Este es el cuerpo del mensaje",
                                    "emailDestino": "alumno@universidad.es",
                                    "telefonoDestino": "600123456",
                                    "programacionEnvio": "2026-12-31T10:00:00",
                                    "medios": ["EMAIL"],
                                    "tipoNotificacion": "PASSWORD_RESET"
                                }
                                """;

                mockMvc.perform(post("/notificaciones")
                                .with(csrf())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(jsonEntrada))
                                .andExpect(status().isCreated())
                                .andExpect(header().exists("Location"))
                                .andExpect(jsonPath("$.id").value(1));
        }

        @Test
        @DisplayName("DELETE /notificaciones/{id} - Debe devolver 204 No Content")
        void eliminar_BorraNotificacion() throws Exception {
                mockMvc.perform(delete("/notificaciones/1").with(csrf()))
                                .andExpect(status().isNoContent());

                Mockito.verify(servicio).eliminarNotificacion(1L);
        }

        @Test
        @DisplayName("PUT /notificaciones/{id} - Debe devolver 200 y modificar la notificación")
        void modificar_ActualizaNotificacion() throws Exception {
                // GIVEN: Le decimos al servicio falso que cuando intente modificar la ID 1,
                // devuelva éxito
                Mockito.when(servicio.modificarNotificacion(eq(1L), any(Notificacion.class)))
                                .thenReturn(notificacionFalsa);

                // Simulamos un cuerpo JSON que enviaría el cliente con los datos modificados
                String jsonEntrada = """
                                {
                                    "mensaje": "Notificación modificada"
                                }
                                """;

                // WHEN & THEN: Hacemos la petición PUT y verificamos que responda 200 OK
                mockMvc.perform(put("/notificaciones/1")
                                .with(csrf()) // Necesario para peticiones PUT
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(jsonEntrada))
                                .andExpect(status().isOk())
                                .andExpect(jsonPath("$.id").value(1));
        }

        @Test
        @DisplayName("Excepciones - Debe devolver 404 NOT FOUND si la notificación no existe")
        void noEncontrado_Devuelve404() throws Exception {
                // GIVEN: Le decimos al servicio que lance tu excepción personalizada al buscar
                // un ID que no existe (ej: 99)
                Mockito.when(servicio.obtenerNotificacionPorId(99L))
                                .thenThrow(new NotificacionNoEncontrada());

                // WHEN & THEN: Hacemos la petición y verificamos que el @ExceptionHandler del
                // controlador la captura y devuelve 404
                mockMvc.perform(get("/notificaciones/99"))
                                .andExpect(status().isNotFound());
        }

        @Test
        @DisplayName("POST /notificaciones - 400 si el JSON está mal formado")
        void aniadir_JsonInvalido() throws Exception {

                String jsonInvalido = "{ mensaje: 123 "; // JSON roto

                mockMvc.perform(post("/notificaciones")
                                .with(csrf())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(jsonInvalido))
                                .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("POST /notificaciones - 400 si faltan campos obligatorios")
        void aniadir_FaltanCampos() throws Exception {

                String jsonSinCampos = "{}";

                mockMvc.perform(post("/notificaciones")
                                .with(csrf())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(jsonSinCampos))
                                .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("PUT /notificaciones/{id} - 400 si el JSON es inválido")
        void modificar_JsonInvalido() throws Exception {

                String jsonInvalido = "{ mensaje: }";

                mockMvc.perform(put("/notificaciones/1")
                                .with(csrf())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(jsonInvalido))
                                .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("PUT /notificaciones/{id} - 404 si la notificación no existe")
        void modificar_NoExiste() throws Exception {

                Mockito.when(servicio.modificarNotificacion(eq(99L), any()))
                                .thenThrow(new NotificacionNoEncontrada());

                String jsonEntrada = """
                                { "mensaje": "Nuevo mensaje" }
                                """;

                mockMvc.perform(put("/notificaciones/99")
                                .with(csrf())
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(jsonEntrada))
                                .andExpect(status().isNotFound());
        }

        @Test
        @DisplayName("DELETE /notificaciones/{id} - 404 si no existe")
        void eliminar_NoExiste() throws Exception {

                Mockito.doThrow(new NotificacionNoEncontrada())
                                .when(servicio).eliminarNotificacion(99L);

                mockMvc.perform(delete("/notificaciones/99").with(csrf()))
                                .andExpect(status().isNotFound());
        }

        @Test
        @DisplayName("GET /notificaciones/{id} - 400 si el ID no es numérico")
        void obtener_IdNoNumerico() throws Exception {

                mockMvc.perform(get("/notificaciones/abc"))
                                .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("POST /notificaciones - 415 si falta Content-Type")
        void aniadir_SinContentType() throws Exception {

                mockMvc.perform(post("/notificaciones")
                                .with(csrf())
                                .content("{\"mensaje\":\"hola\"}"))
                                .andExpect(status().isUnsupportedMediaType());
        }
}