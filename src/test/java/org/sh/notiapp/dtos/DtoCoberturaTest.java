package org.sh.notiapp.dtos;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class DtoCoberturaTest {

    @Test
    @DisplayName("Forzar cobertura de métodos generados por Lombok en NotificacionDTO")
    void testNotificacionDTO() {
        NotificacionDTO dto1 = new NotificacionDTO();
        dto1.setId(1L);
        dto1.setAsunto("Test");

        NotificacionDTO dto2 = new NotificacionDTO(1L, "Test", null, null, null, null, null, null, null, null, null);

        assertNotNull(dto1.toString()); // Cubre toString
        assertEquals(dto1.hashCode(), dto2.hashCode()); // Cubre hashCode
        assertTrue(dto1.equals(dto2)); // Cubre equals
        assertNotNull(dto1.getId()); // Cubre Getters
    }

    @Test
    @DisplayName("Forzar cobertura de métodos generados por Lombok en NotificacionNueva")
    void testNotificacionNueva() {
        NotificacionNueva nueva1 = new NotificacionNueva();
        nueva1.setAsunto("Asunto");

        NotificacionNueva nueva2 = new NotificacionNueva("Asunto", null, null, null, null, null, null);

        assertNotNull(nueva1.toString());
        assertEquals(nueva1.hashCode(), nueva2.hashCode());
        assertTrue(nueva1.equals(nueva2));
        assertNotNull(nueva1.getAsunto());
    }
}
