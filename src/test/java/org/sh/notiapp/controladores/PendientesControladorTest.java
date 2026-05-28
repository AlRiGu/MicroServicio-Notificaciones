package org.sh.notiapp.controladores;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.sh.notiapp.enums.TipoNotificacion;
import org.sh.notiapp.servicios.NotificacionServicio;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(properties = {
        "spring.datasource.url=jdbc:h2:mem:testdb",
        "spring.jpa.hibernate.ddl-auto=create-drop"
})
@AutoConfigureMockMvc(addFilters = false)
@WithMockUser
class PendientesControladorTest {

    @Autowired
    private MockMvc mockMvc;

    @MockBean
    private NotificacionServicio servicio;

    @Test
    @DisplayName("POST /pendientes/abortar (Sin parámetro) - Devuelve 200 y mensaje de éxito")
    void abortarPendientes_SinTipo_Devuelve200() throws Exception {
        // WHEN & THEN: Hacemos la petición POST sin enviar el parámetro "tipo"
        mockMvc.perform(post("/pendientes/abortar")
                .with(csrf())) // Necesario para peticiones POST
                .andExpect(status().isOk())
                .andExpect(content().string("Se han abortado las notificaciones pendientes indicadas"));

        // Verificamos que el controlador llamó al servicio pasándole un valor "null"
        Mockito.verify(servicio).abortarPendientes(null);
    }

    @Test
    @DisplayName("POST /pendientes/abortar (Con parámetro) - Devuelve 200 y pasa el tipo al servicio")
    void abortarPendientes_ConTipo_Devuelve200() throws Exception {
        // Rescatamos uno de los ENUM que vimos en los logs de tu base de datos
        String tipoPrueba = "PASSWORD_RESET";

        // WHEN & THEN: Hacemos la petición POST inyectando el parámetro en la URL
        mockMvc.perform(post("/pendientes/abortar")
                .param("tipo", tipoPrueba)
                .with(csrf()))
                .andExpect(status().isOk())
                .andExpect(content().string("Se han abortado las notificaciones pendientes indicadas"));

        // Verificamos que el controlador tradujo el String de la URL al Enum correcto y
        // llamó al servicio
        Mockito.verify(servicio).abortarPendientes(TipoNotificacion.valueOf(tipoPrueba));
    }
}