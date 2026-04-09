package org.sh.notiapp.repositorios;

import org.sh.notiapp.entidades.Notificacion;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface NotificacionRepositorio extends JpaRepository<Notificacion, Long> {
}