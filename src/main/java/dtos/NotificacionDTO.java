package dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import enums.EstadoNotificacion;
import enums.MedioNotificacion;
import enums.TipoNotificacion;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class NotificacionDTO {
    private Long id;
    private String asunto;
    private String cuerpo;
    private String emailDestino;
    private String telefonoDestino;
    private LocalDateTime programacionEnvio;
    private List<MedioNotificacion> medios;
    private TipoNotificacion tipoNotificacion;
    private EstadoNotificacion estado;
    private String mensajeError;
    private LocalDateTime momentoRealEnvio;
}