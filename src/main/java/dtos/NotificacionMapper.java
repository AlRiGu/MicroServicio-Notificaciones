package dtos;

import entidades.Notificacion;

public class NotificacionMapper {

    public static NotificacionDTO toDto(Notificacion entidad) {
        if (entidad == null)
            return null;

        return new NotificacionDTO(
                entidad.getId(),
                entidad.getAsunto(),
                entidad.getCuerpo(),
                entidad.getEmailDestino(),
                entidad.getTelefonoDestino(),
                entidad.getProgramacionEnvio(),
                entidad.getMedios(),
                entidad.getTipoNotificacion(),
                entidad.getEstado(),
                entidad.getMensajeError(),
                entidad.getMomentoRealEnvio());
    }

    public static Notificacion toEntity(NotificacionDTO dto) {
        if (dto == null)
            return null;

        Notificacion entidad = new Notificacion();
        entidad.setId(dto.getId());
        entidad.setAsunto(dto.getAsunto());
        entidad.setCuerpo(dto.getCuerpo());
        entidad.setEmailDestino(dto.getEmailDestino());
        entidad.setTelefonoDestino(dto.getTelefonoDestino());
        entidad.setProgramacionEnvio(dto.getProgramacionEnvio());
        entidad.setMedios(dto.getMedios());
        entidad.setTipoNotificacion(dto.getTipoNotificacion());
        entidad.setEstado(dto.getEstado());
        entidad.setMensajeError(dto.getMensajeError());
        entidad.setMomentoRealEnvio(dto.getMomentoRealEnvio());

        return entidad;
    }

    public static Notificacion toEntity(NotificacionNueva dtoNueva) {
        if (dtoNueva == null)
            return null;

        Notificacion entidad = new Notificacion();
        entidad.setAsunto(dtoNueva.getAsunto());
        entidad.setCuerpo(dtoNueva.getCuerpo());
        entidad.setEmailDestino(dtoNueva.getEmailDestino());
        entidad.setTelefonoDestino(dtoNueva.getTelefonoDestino());
        entidad.setProgramacionEnvio(dtoNueva.getProgramacionEnvio());
        entidad.setMedios(dtoNueva.getMedios());
        entidad.setTipoNotificacion(dtoNueva.getTipoNotificacion());

        // No se asignan: id, estado, mensajeError, momentoRealEnvio
        // porque se generan en el servicio o en la BD

        return entidad;
    }
}