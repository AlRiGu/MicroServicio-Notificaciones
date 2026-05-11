package org.sh.notiapp.servicios;

import org.sh.notiapp.entidades.Notificacion;
import org.sh.notiapp.enums.EstadoNotificacion;
import org.sh.notiapp.enums.TipoNotificacion;
import org.sh.notiapp.excepciones.NotificacionNoEncontrada;
import org.sh.notiapp.repositorios.NotificacionRepositorio;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class NotificacionServicio {

    private final NotificacionRepositorio repositorio;

    public NotificacionServicio(NotificacionRepositorio repositorio) {
        this.repositorio = repositorio;
    }

    private boolean actualizarEstadoSiCorresponde(Notificacion n) {
        if (n.getProgramacionEnvio() != null &&
                n.getProgramacionEnvio().compareTo(LocalDateTime.now()) <= 0 &&
                n.getEstado() == EstadoNotificacion.PENDIENTE) {

            n.setEstado(EstadoNotificacion.ENVIADA);
            n.setMomentoRealEnvio(n.getProgramacionEnvio());
            return true; // indica que hay que guardar
        }
        return false;
    }

    private void actualizarFecha(List<Notificacion> notificaciones) {
        boolean hayCambios = false;

        for (Notificacion n : notificaciones) {
            if (actualizarEstadoSiCorresponde(n)) {
                hayCambios = true;
            }
        }

        if (hayCambios) {
            repositorio.saveAll(notificaciones);
        }
    }

    public List<Notificacion> obtenerTodasNotificaciones() {
        List<Notificacion> notificaciones = repositorio.findAll();
        actualizarFecha(notificaciones);
        return notificaciones;
    }

    public List<Notificacion> obtenerNotificacionesFiltradas(EstadoNotificacion estado, TipoNotificacion tipo) {
        List<Notificacion> notificaciones;

        if (estado != null && tipo != null) {
            notificaciones = repositorio.findByEstadoAndTipoNotificacion(estado, tipo);
        } else if (estado != null) {
            notificaciones = repositorio.findByEstado(estado);
        } else if (tipo != null) {
            notificaciones = repositorio.findByTipoNotificacion(tipo);
        } else {
            notificaciones = repositorio.findAll();
        }

        actualizarFecha(notificaciones);
        return notificaciones;
    }

    public Notificacion obtenerNotificacionPorId(Long id) {
        Notificacion n = repositorio.findById(id)
                .orElseThrow(NotificacionNoEncontrada::new);

        if (actualizarEstadoSiCorresponde(n)) {
            repositorio.save(n);
        }

        return n;
    }

    public Notificacion aniadirNotificacion(Notificacion notificacion) {
        notificacion.setId(null);
        if (notificacion.getEstado() == null) {
            notificacion.setEstado(EstadoNotificacion.PENDIENTE);
        }
        return repositorio.save(notificacion);
    }

    public void eliminarNotificacion(Long id) {
        obtenerNotificacionPorId(id);
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

    public void abortarPendientes(TipoNotificacion tipo) {
        List<Notificacion> pendientes = (tipo == null)
                ? repositorio.findByEstado(EstadoNotificacion.PENDIENTE)
                : repositorio.findByEstadoAndTipoNotificacion(EstadoNotificacion.PENDIENTE, tipo);

        for (Notificacion n : pendientes) {
            n.setEstado(EstadoNotificacion.ABORTADA);
        }

        repositorio.saveAll(pendientes);
    }
}
