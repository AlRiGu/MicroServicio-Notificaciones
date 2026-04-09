package org.sh.notiapp.controladores;

import org.sh.notiapp.servicios.NotificacionServicio;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/pendientes")
public class PendientesControlador {

    private final NotificacionServicio servicio;

    public PendientesControlador(NotificacionServicio servicio) {
        this.servicio = servicio;
    }

    @PostMapping("/abortar")
    public ResponseEntity<String> abortarPendientes(
            @RequestParam(required = false) org.sh.notiapp.enums.TipoNotificacion tipo) {
        servicio.abortarPendientes(tipo);
        return ResponseEntity.ok("Se han abortado las notificaciones pendientes indicadas");
    }
}
