package controladores;

import dtos.NotificacionDTO;
import dtos.NotificacionMapper;
import dtos.NotificacionNueva;
import entidades.Notificacion;
import excepciones.NotificacionNoEncontrada;
import servicios.NotificacionServicio;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/notificaciones")
public class NotificacionControlador {

    private final NotificacionServicio servicio;

    public NotificacionControlador(NotificacionServicio servicio) {
        this.servicio = servicio;
    }

    @GetMapping("")
    public List<NotificacionDTO> obtenerTodasNotificaciones() {
        return servicio.obtenerTodasNotificaciones()
                .stream()
                .map(NotificacionMapper::toDto)
                .toList();
    }

    @GetMapping("/{id}")
    public ResponseEntity<NotificacionDTO> obtenerUna(@PathVariable Long id) {
        Notificacion entidad = servicio.obtenerNotificacionPorId(id);
        return ResponseEntity.ok(NotificacionMapper.toDto(entidad));
    }

    @PostMapping("")
    public ResponseEntity<NotificacionDTO> aniadir(
            @RequestBody NotificacionNueva nueva,
            UriComponentsBuilder uriBuilder) {

        Notificacion entidad = NotificacionMapper.toEntity(nueva);
        Notificacion guardada = servicio.aniadirNotificacion(entidad);

        URI location = uriBuilder.path("/notificaciones/{id}")
                .buildAndExpand(guardada.getId())
                .toUri();

        return ResponseEntity.created(location)
                .body(NotificacionMapper.toDto(guardada));
    }

    @PutMapping("/{id}")
    public ResponseEntity<NotificacionDTO> modificar(
            @PathVariable Long id,
            @RequestBody NotificacionDTO dto) {

        Notificacion entidad = NotificacionMapper.toEntity(dto);
        Notificacion modificada = servicio.modificarNotificacion(id, entidad);

        return ResponseEntity.ok(NotificacionMapper.toDto(modificada));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void eliminar(@PathVariable Long id) {
        servicio.eliminarNotificacion(id);
    }

    @ExceptionHandler(NotificacionNoEncontrada.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public void noEncontrado() {
    }
}
