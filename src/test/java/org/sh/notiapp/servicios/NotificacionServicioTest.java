package org.sh.notiapp.servicios;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.sh.notiapp.entidades.Notificacion;
import org.sh.notiapp.enums.EstadoNotificacion;
import org.sh.notiapp.enums.TipoNotificacion;
import org.sh.notiapp.excepciones.NotificacionNoEncontrada;
import org.sh.notiapp.repositorios.NotificacionRepositorio;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Tests del Servicio de Notificaciones")
class NotificacionServicioTest {

    @Mock
    private NotificacionRepositorio repositorio;

    @InjectMocks
    private NotificacionServicio servicio;

    // ==========================================
    // TESTS PARA OBTENER POR ID (Y ACTUALIZAR ESTADO)
    // ==========================================

    @Test
    @DisplayName("Lanza excepción si la notificación no existe por ID")
    void obtenerNotificacionPorId_NoExiste_LanzaExcepcion() {
        // Arrange
        when(repositorio.findById(1L)).thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(NotificacionNoEncontrada.class, () -> {
            servicio.obtenerNotificacionPorId(1L);
        });
    }

    @Test
    @DisplayName("Devuelve notificación sin actualizar si está programada en el futuro")
    void obtenerNotificacionPorId_Futura_NoActualiza() {
        // Arrange
        Notificacion n = new Notificacion();
        n.setEstado(EstadoNotificacion.PENDIENTE);
        n.setProgramacionEnvio(LocalDateTime.now().plusDays(1)); // Futuro
        when(repositorio.findById(1L)).thenReturn(Optional.of(n));

        // Act
        Notificacion resultado = servicio.obtenerNotificacionPorId(1L);

        // Assert
        assertThat(resultado.getEstado()).isEqualTo(EstadoNotificacion.PENDIENTE);
        verify(repositorio, never()).save(any());
    }

    @Test
    @DisplayName("Devuelve notificación sin actualizar si ya está ENVIADA o ABORTADA")
    void obtenerNotificacionPorId_NoPendiente_NoActualiza() {
        // Arrange
        Notificacion n = new Notificacion();
        n.setEstado(EstadoNotificacion.ENVIADA); // Ya no es PENDIENTE
        n.setProgramacionEnvio(LocalDateTime.now().minusDays(1));
        when(repositorio.findById(1L)).thenReturn(Optional.of(n));

        // Act
        Notificacion resultado = servicio.obtenerNotificacionPorId(1L);

        // Assert
        verify(repositorio, never()).save(any());
    }

    @Test
    @DisplayName("Devuelve notificación sin actualizar si no tiene fecha de programación (null)")
    void obtenerNotificacionPorId_ProgramacionNull_NoActualiza() {
        // Arrange
        Notificacion n = new Notificacion();
        n.setEstado(EstadoNotificacion.PENDIENTE);
        n.setProgramacionEnvio(null);
        when(repositorio.findById(1L)).thenReturn(Optional.of(n));

        // Act
        Notificacion resultado = servicio.obtenerNotificacionPorId(1L);

        // Assert
        verify(repositorio, never()).save(any());
    }

    @Test
    @DisplayName("BUG ESPERADO: Actualiza el estado a ENVIADA si la fecha pasó, pero asigna mal el momento real")
    void obtenerNotificacionPorId_Pasada_ActualizaEstado() {
        // Arrange
        Notificacion n = new Notificacion();
        n.setEstado(EstadoNotificacion.PENDIENTE);
        LocalDateTime fechaPasada = LocalDateTime.now().minusHours(2);
        n.setProgramacionEnvio(fechaPasada); 
        when(repositorio.findById(1L)).thenReturn(Optional.of(n));

        // Act
        Notificacion resultado = servicio.obtenerNotificacionPorId(1L);

        // Assert
        assertThat(resultado.getEstado()).isEqualTo(EstadoNotificacion.ENVIADA);
        // Este assert falla a propósito demostrando el bug 2 (la máquina del tiempo)
        // El momento real debería ser cercano a NOW, no hace 2 horas.
        assertThat(resultado.getMomentoRealEnvio()).isAfter(fechaPasada); 
        verify(repositorio, times(1)).save(n);
    }

    // ==========================================
    // TESTS PARA OBTENER TODAS Y FILTRADAS
    // ==========================================

    @Test
    @DisplayName("Obtiene todas las notificaciones y guarda la lista si hay cambios")
    void obtenerTodasNotificaciones_ConCambios_GuardaLista() {
        // Arrange
        Notificacion n = new Notificacion();
        n.setEstado(EstadoNotificacion.PENDIENTE);
        n.setProgramacionEnvio(LocalDateTime.now().minusMinutes(5));
        List<Notificacion> lista = List.of(n);
        when(repositorio.findAll()).thenReturn(lista);

        // Act
        List<Notificacion> resultado = servicio.obtenerTodasNotificaciones();

        // Assert
        assertThat(resultado).hasSize(1);
        verify(repositorio, times(1)).saveAll(lista);
    }

    @Test
    @DisplayName("Obtiene filtradas: Estado y Tipo no nulos")
    void obtenerFiltradas_AmbosNoNulos_LlamaMetodoCorrecto() {
        // Arrange
        when(repositorio.findByEstadoAndTipoNotificacion(EstadoNotificacion.PENDIENTE, TipoNotificacion.ANUNCIO_AULA_ESTUDIANTE))
                .thenReturn(new ArrayList<>());

        // Act
        servicio.obtenerNotificacionesFiltradas(EstadoNotificacion.PENDIENTE, TipoNotificacion.ANUNCIO_AULA_ESTUDIANTE);

        // Assert
        verify(repositorio, times(1)).findByEstadoAndTipoNotificacion(EstadoNotificacion.PENDIENTE, TipoNotificacion.ANUNCIO_AULA_ESTUDIANTE);
    }

    @Test
    @DisplayName("Obtiene filtradas: Solo Estado no nulo")
    void obtenerFiltradas_SoloEstado_LlamaMetodoCorrecto() {
        // Arrange
        when(repositorio.findByEstado(EstadoNotificacion.PENDIENTE)).thenReturn(new ArrayList<>());

        // Act
        servicio.obtenerNotificacionesFiltradas(EstadoNotificacion.PENDIENTE, null);

        // Assert
        verify(repositorio, times(1)).findByEstado(EstadoNotificacion.PENDIENTE);
    }

    @Test
    @DisplayName("Obtiene filtradas: Solo Tipo no nulo")
    void obtenerFiltradas_SoloTipo_LlamaMetodoCorrecto() {
        // Arrange
        when(repositorio.findByTipoNotificacion(TipoNotificacion.ANUNCIO_AULA_VIGILANTE)).thenReturn(new ArrayList<>());

        // Act
        servicio.obtenerNotificacionesFiltradas(null, TipoNotificacion.ANUNCIO_AULA_VIGILANTE);

        // Assert
        verify(repositorio, times(1)).findByTipoNotificacion(TipoNotificacion.ANUNCIO_AULA_VIGILANTE);
    }

    @Test
    @DisplayName("Obtiene filtradas por Estado y excluye correctamente las que han cambiado a ENVIADA por tiempo")
    void obtenerFiltradas_AmbosNulos_LlamaFindAll() {
        // Arrange
        Notificacion n = new Notificacion();
        n.setEstado(EstadoNotificacion.PENDIENTE);
        n.setProgramacionEnvio(LocalDateTime.now().minusMinutes(10)); // Expirada
        
        // Usamos una lista mutable para evitar problemas con Mockito
        List<Notificacion> listaExpira = new java.util.ArrayList<>(List.of(n));
        when(repositorio.findByEstado(EstadoNotificacion.PENDIENTE)).thenReturn(listaExpira);

        // Act
        List<Notificacion> resultado = servicio.obtenerNotificacionesFiltradas(EstadoNotificacion.PENDIENTE, null);

        // Assert: Como corregisteis el bug, la lista debe estar vacía de PENDIENTES
        assertThat(resultado).isEmpty();
    }

    @Test
    @DisplayName("Obtiene filtradas: Ambos nulos llama a findAll")
    void obtenerFiltradas_AmbosNulos_LlamaAll() {
        // Arrange
        when(repositorio.findAll()).thenReturn(new ArrayList<>());

        // Act
        servicio.obtenerNotificacionesFiltradas(null, null);

        // Assert
        verify(repositorio, times(1)).findAll();
    }

    // ==========================================
    // TESTS PARA AÑADIR, MODIFICAR Y ELIMINAR
    // ==========================================

    @Test
    @DisplayName("Añade notificación con estado null y lo cambia a PENDIENTE")
    void aniadirNotificacion_EstadoNull_SeteaPendiente() {
        // Arrange
        Notificacion n = new Notificacion();
        n.setId(5L); // ID que debería anularse
        n.setEstado(null);
        when(repositorio.save(any(Notificacion.class))).thenReturn(n);

        // Act
        servicio.aniadirNotificacion(n);

        // Assert
        assertThat(n.getId()).isNull();
        assertThat(n.getEstado()).isEqualTo(EstadoNotificacion.PENDIENTE);
        verify(repositorio, times(1)).save(n);
    }

    @Test
    @DisplayName("Añade notificación respetando el estado si no es null")
    void aniadirNotificacion_EstadoNoNull_RespetaEstado() {
        // Arrange
        Notificacion n = new Notificacion();
        n.setEstado(EstadoNotificacion.ENVIADA);
        when(repositorio.save(any(Notificacion.class))).thenReturn(n);

        // Act
        servicio.aniadirNotificacion(n);

        // Assert
        assertThat(n.getEstado()).isEqualTo(EstadoNotificacion.ENVIADA);
        verify(repositorio, times(1)).save(n);
    }

    @Test
    @DisplayName("Elimina notificación correctamente si existe")
    void eliminarNotificacion_Existe_EliminaCorrectamente() {
        // Arrange
        Notificacion n = new Notificacion();
        when(repositorio.findById(1L)).thenReturn(Optional.of(n));

        // Act
        servicio.eliminarNotificacion(1L);

        // Assert
        verify(repositorio, times(1)).deleteById(1L);
    }

    @Test
    @DisplayName("Modifica todos los campos de una notificación existente")
    void modificarNotificacion_Existe_ActualizaCampos() {
        // Arrange
        Notificacion existente = new Notificacion();
        when(repositorio.findById(1L)).thenReturn(Optional.of(existente));
        
        Notificacion cambios = new Notificacion();
        cambios.setAsunto("Nuevo");
        cambios.setEstado(EstadoNotificacion.ABORTADA);

        // Act
        servicio.modificarNotificacion(1L, cambios);

        // Assert
        assertThat(existente.getAsunto()).isEqualTo("Nuevo");
        assertThat(existente.getEstado()).isEqualTo(EstadoNotificacion.ABORTADA);
        verify(repositorio, times(1)).save(existente);
    }

    // ==========================================
    // TESTS PARA ABORTAR PENDIENTES
    // ==========================================

    @Test
    @DisplayName("Abortar pendientes: Sin tipo, aborta todas las pendientes")
    void abortarPendientes_SinTipo_AbortaTodas() {
        // Arrange
        Notificacion n = new Notificacion();
        n.setEstado(EstadoNotificacion.PENDIENTE);
        List<Notificacion> pendientes = List.of(n);
        when(repositorio.findByEstado(EstadoNotificacion.PENDIENTE)).thenReturn(pendientes);

        // Act
        servicio.abortarPendientes(null);

        // Assert
        assertThat(n.getEstado()).isEqualTo(EstadoNotificacion.ABORTADA);
        verify(repositorio, times(1)).findByEstado(EstadoNotificacion.PENDIENTE);
        verify(repositorio, times(1)).saveAll(pendientes);
    }

    @Test
    @DisplayName("Abortar pendientes: Con tipo, filtra por tipo y aborta")
    void abortarPendientes_ConTipo_AbortaPorTipo() {
        // Arrange
        Notificacion n = new Notificacion();
        n.setEstado(EstadoNotificacion.PENDIENTE);
        List<Notificacion> pendientes = List.of(n);
        when(repositorio.findByEstadoAndTipoNotificacion(EstadoNotificacion.PENDIENTE, TipoNotificacion.ANUNCIO_NOTA_ESTUDIANTE)).thenReturn(pendientes);

        // Act
        servicio.abortarPendientes(TipoNotificacion.ANUNCIO_NOTA_ESTUDIANTE);

        // Assert
        assertThat(n.getEstado()).isEqualTo(EstadoNotificacion.ABORTADA);
        verify(repositorio, times(1)).findByEstadoAndTipoNotificacion(EstadoNotificacion.PENDIENTE, TipoNotificacion.ANUNCIO_NOTA_ESTUDIANTE);
        verify(repositorio, times(1)).saveAll(pendientes);
    }
}