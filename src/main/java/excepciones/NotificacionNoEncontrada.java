package main.java.excepciones;

public class NotificacionNoEncontrada extends RuntimeException {
    public NotificacionNoEncontrada() {
        super("La notificación solicitada no existe.");
    }
}