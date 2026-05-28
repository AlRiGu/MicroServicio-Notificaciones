package org.sh.notiapp.entidades;

import jakarta.persistence.*;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;
import java.util.List;

import org.sh.notiapp.enums.EstadoNotificacion;
import org.sh.notiapp.enums.MedioNotificacion;
import org.sh.notiapp.enums.TipoNotificacion;

@Getter
@Setter
@ToString
@EqualsAndHashCode(onlyExplicitlyIncluded = true)
@NoArgsConstructor
@Entity
@Table(name = "notificaciones")
public class Notificacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @EqualsAndHashCode.Include
    private Long id;

    @Column(nullable = false)
    private String asunto;

    @Column(columnDefinition = "TEXT")
    private String cuerpo;

    @Column(nullable = false)
    private String emailDestino;
    private String telefonoDestino;
    private LocalDateTime programacionEnvio;

    @ElementCollection(targetClass = MedioNotificacion.class)
    @Enumerated(EnumType.STRING)
    @CollectionTable(name = "notificacion_medios", joinColumns = @JoinColumn(name = "notificacion_id"))
    @Column(name = "medio", nullable = false)
    private List<MedioNotificacion> medios;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private TipoNotificacion tipoNotificacion;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private EstadoNotificacion estado;

    private String mensajeError;
    private LocalDateTime momentoRealEnvio;
}