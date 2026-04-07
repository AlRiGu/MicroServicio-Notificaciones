package main.java.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import main.java.enums.MedioNotificacion;
import main.java.enums.TipoNotificacion;

import java.time.LocalDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class NotificacionNueva {
    private String asunto;
    private String cuerpo;
    private String emailDestino;
    private String telefonoDestino;
    private LocalDateTime programacionEnvio;
    private List<MedioNotificacion> medios;
    private TipoNotificacion tipoNotificacion;
}