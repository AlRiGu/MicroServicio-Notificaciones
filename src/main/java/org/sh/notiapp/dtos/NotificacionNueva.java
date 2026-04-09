package org.sh.notiapp.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

import org.sh.notiapp.enums.MedioNotificacion;
import org.sh.notiapp.enums.TipoNotificacion;

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