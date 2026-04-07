package entidades;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import enums.EstadoNotificacion;
import enums.MedioNotificacion;
import enums.TipoNotificacion;

import java.time.LocalDateTime;
import java.util.List;

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