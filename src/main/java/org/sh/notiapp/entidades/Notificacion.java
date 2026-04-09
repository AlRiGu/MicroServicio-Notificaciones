package org.sh.notiapp.entidades;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.List;

import org.sh.notiapp.enums.EstadoNotificacion;
import org.sh.notiapp.enums.MedioNotificacion;
import org.sh.notiapp.enums.TipoNotificacion;

@Getter
@Setter
@Entity
@Table(name = "notificaciones")
public class Notificacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String asunto;

    @Column(columnDefinition = "TEXT")
    private String cuerpo;

    private String emailDestino;
    private String telefonoDestino;
    private LocalDateTime programacionEnvio;

    @ElementCollection(targetClass = MedioNotificacion.class)
    @Enumerated(EnumType.STRING)
    @CollectionTable(name = "notificacion_medios", joinColumns = @JoinColumn(name = "notificacion_id"))
    @Column(name = "medio")
    private List<MedioNotificacion> medios;

    @Enumerated(EnumType.STRING)
    private TipoNotificacion tipoNotificacion;

    @Enumerated(EnumType.STRING)
    private EstadoNotificacion estado;

    private String mensajeError;
    private LocalDateTime momentoRealEnvio;
}