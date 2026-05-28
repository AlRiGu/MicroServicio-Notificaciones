package org.sh.notiapp.dtos;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.sh.notiapp.entidades.Notificacion;
import org.sh.notiapp.enums.EstadoNotificacion;
import org.sh.notiapp.enums.MedioNotificacion;
import org.sh.notiapp.enums.TipoNotificacion;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class NotificacionMapperTest {

    @Test
    @DisplayName("toDto: Si la entidad es null, devuelve null")
    void toDto_EntidadNull_DevuelveNull() {
        assertNull(NotificacionMapper.toDto(null));
    }

    @Test
    @DisplayName("toDto: Mapea correctamente todos los campos")
    void toDto_EntidadValida_MapeaCorrectamente() {
        Notificacion n = new Notificacion();
        n.setId(1L);
        n.setAsunto("Asunto");
        n.setMedios(List.of(MedioNotificacion.EMAIL));
        n.setEstado(EstadoNotificacion.PENDIENTE);
        n.setTipoNotificacion(TipoNotificacion.PASSWORD_RESET);

        NotificacionDTO dto = NotificacionMapper.toDto(n);

        assertNotNull(dto);
        assertEquals(1L, dto.getId());
        assertEquals("Asunto", dto.getAsunto());
        assertEquals(EstadoNotificacion.PENDIENTE, dto.getEstado());
        assertEquals(TipoNotificacion.PASSWORD_RESET, dto.getTipoNotificacion());
        assertTrue(dto.getMedios().contains(MedioNotificacion.EMAIL));
    }

    @Test
    @DisplayName("toEntity(DTO): Si el DTO es null, devuelve null")
    void toEntity_DtoNull_DevuelveNull() {
        assertNull(NotificacionMapper.toEntity((NotificacionDTO) null));
    }

    @Test
    @DisplayName("toEntity(DTO): Mapea correctamente todos los campos")
    void toEntity_DtoValido_MapeaCorrectamente() {
        NotificacionDTO dto = new NotificacionDTO();
        dto.setId(2L);
        dto.setAsunto("Asunto DTO");
        dto.setMomentoRealEnvio(LocalDateTime.now());

        Notificacion n = NotificacionMapper.toEntity(dto);

        assertNotNull(n);
        assertEquals(2L, n.getId());
        assertEquals("Asunto DTO", n.getAsunto());
        assertNotNull(n.getMomentoRealEnvio());
    }

    @Test
    @DisplayName("toEntity(Nueva): Si el DTO Nuevo es null, devuelve null")
    void toEntity_NuevaNull_DevuelveNull() {
        assertNull(NotificacionMapper.toEntity((NotificacionNueva) null));
    }

    @Test
    @DisplayName("toEntity(Nueva): Mapea omitiendo ID, estado, error y momento")
    void toEntity_NuevaValida_MapeaCorrectamente() {
        NotificacionNueva nueva = new NotificacionNueva();
        nueva.setAsunto("Asunto Nueva");
        nueva.setTipoNotificacion(TipoNotificacion.ANUNCIO_AULA_ESTUDIANTE);

        Notificacion n = NotificacionMapper.toEntity(nueva);

        assertNotNull(n);
        assertEquals("Asunto Nueva", n.getAsunto());
        assertEquals(TipoNotificacion.ANUNCIO_AULA_ESTUDIANTE, n.getTipoNotificacion());
        assertNull(n.getId()); // Comprobamos que el mapper ignora este campo
        assertNull(n.getEstado()); // Comprobamos que el mapper ignora este campo
    }
}