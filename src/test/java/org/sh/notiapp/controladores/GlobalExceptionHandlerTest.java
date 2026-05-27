package org.sh.notiapp.controladores;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class GlobalExceptionHandlerTest {

    @Test
    @DisplayName("Debe capturar cualquier Exception y devolver 500 con el StackTrace")
    void handleAllExceptions_Devuelve500YStackTrace() {
        // GIVEN: Instanciamos el manejador directamente.
        // ¡No necesitamos levantar Spring Boot (@SpringBootTest) para esto!
        GlobalExceptionHandler manejador = new GlobalExceptionHandler();

        // Creamos una excepción de prueba con un mensaje identificativo
        Exception excepcionPrueba = new RuntimeException("¡Fallo catastrófico en los servidores!");

        // WHEN: Llamamos al método pasándole nuestra excepción directamente
        ResponseEntity<String> respuesta = manejador.handleAllExceptions(excepcionPrueba);

        // THEN: Comprobamos que el código HTTP devuelto es un 500 (INTERNAL_SERVER_ERROR)
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, respuesta.getStatusCode());

        // Comprobamos que el cuerpo de la respuesta contiene nuestro mensaje de error
        assertTrue(respuesta.getBody() != null);
        assertTrue(respuesta.getBody().contains("¡Fallo catastrófico en los servidores!"));

        // Comprobamos que también incluye la traza de la clase (StackTrace)
        assertTrue(respuesta.getBody().contains("java.lang.RuntimeException"));
    }
}