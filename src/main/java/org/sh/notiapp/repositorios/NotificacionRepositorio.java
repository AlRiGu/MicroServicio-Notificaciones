package org.sh.notiapp.repositorios;

import org.sh.notiapp.entidades.Notificacion;
import org.sh.notiapp.enums.EstadoNotificacion;
import org.sh.notiapp.enums.TipoNotificacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface NotificacionRepositorio extends JpaRepository<Notificacion, Long> {
    List<Notificacion> findByEstado(EstadoNotificacion estado);
    List<Notificacion> findByEstadoAndTipoNotificacion(EstadoNotificacion estado, TipoNotificacion tipoNotificacion);
}