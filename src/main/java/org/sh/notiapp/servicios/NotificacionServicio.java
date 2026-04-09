package org.sh.notiapp.servicios;

import org.sh.notiapp.entidades.Notificacion;
import org.sh.notiapp.enums.EstadoNotificacion;
import org.sh.notiapp.excepciones.NotificacionNoEncontrada;
import org.sh.notiapp.repositorios.NotificacionRepositorio;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NotificacionServicio {

    private final NotificacionRepositorio repositorio;

    public NotificacionServicio(NotificacionRepositorio repositorio) {
        this.repositorio = repositorio;
    }

    public List<Notificacion> obtenerTodasNotificaciones() {
        return repositorio.findAll();
    }

    public Notificacion obtenerNotificacionPorId(Long id) {
        return repositorio.findById(id)
                .orElseThrow(NotificacionNoEncontrada::new);
    }

    public Notificacion aniadirNotificacion(Notificacion notificacion) {
        notificacion.setId(null); // aseguramos que se cree nueva
        if (notificacion.getEstado() == null) {
            notificacion.setEstado(EstadoNotificacion.PENDIENTE);
        }
        return repositorio.save(notificacion);
    }

    public void eliminarNotificacion(Long id) {
        obtenerNotificacionPorId(id); // lanza excepción si no existe
        repositorio.deleteById(id);
    }

    public Notificacion modificarNotificacion(Long id, Notificacion cambios) {
        Notificacion existente = obtenerNotificacionPorId(id);

        existente.setAsunto(cambios.getAsunto());
        existente.setCuerpo(cambios.getCuerpo());
        existente.setEmailDestino(cambios.getEmailDestino());
        existente.setTelefonoDestino(cambios.getTelefonoDestino());
        existente.setProgramacionEnvio(cambios.getProgramacionEnvio());
        existente.setMedios(cambios.getMedios());
        existente.setTipoNotificacion(cambios.getTipoNotificacion());
        existente.setEstado(cambios.getEstado());
        existente.setMensajeError(cambios.getMensajeError());
        existente.setMomentoRealEnvio(cambios.getMomentoRealEnvio());

        return repositorio.save(existente);
    }

    public void abortarPendientes(org.sh.notiapp.enums.TipoNotificacion tipo) {
        List<Notificacion> pendientes;
        if (tipo == null) {
            pendientes = repositorio.findByEstado(EstadoNotificacion.PENDIENTE);
        } else {
            pendientes = repositorio.findByEstadoAndTipoNotificacion(EstadoNotificacion.PENDIENTE, tipo);
        }
        for (Notificacion notificacion : pendientes) {
            notificacion.setEstado(EstadoNotificacion.ABORTADA);
        }
        repositorio.saveAll(pendientes);
    }
}
