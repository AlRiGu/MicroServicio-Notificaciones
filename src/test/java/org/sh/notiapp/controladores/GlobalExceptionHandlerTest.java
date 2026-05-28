package org.sh.notiapp.controladores;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.HttpMediaTypeNotSupportedException;
import org.springframework.web.bind.MethodArgumentNotValidException;

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

        // THEN: Comprobamos que el código HTTP devuelto es un 500
        // (INTERNAL_SERVER_ERROR)
        assertEquals(HttpStatus.INTERNAL_SERVER_ERROR, respuesta.getStatusCode());

        // Comprobamos que el cuerpo de la respuesta contiene nuestro mensaje de error
        assertTrue(respuesta.getBody() != null);
        assertTrue(respuesta.getBody().contains("¡Fallo catastrófico en los servidores!"));

        // Comprobamos que también incluye la traza de la clase (StackTrace)
        assertTrue(respuesta.getBody().contains("java.lang.RuntimeException"));
    }

    @Test
    @DisplayName("Debe devolver 415 cuando el Content-Type no es soportado")
    void handleUnsupportedMediaType_Devuelve415() {
        GlobalExceptionHandler manejador = new GlobalExceptionHandler();

        HttpMediaTypeNotSupportedException ex = new HttpMediaTypeNotSupportedException("application/octet-stream");

        ResponseEntity<String> respuesta = manejador.handleUnsupportedMediaType(ex);

        assertEquals(HttpStatus.UNSUPPORTED_MEDIA_TYPE, respuesta.getStatusCode());
    }

    @Test
    @DisplayName("Debe devolver 400 cuando falla la validación del DTO")
    void handleBadRequest_Devuelve400() {

        GlobalExceptionHandler manejador = new GlobalExceptionHandler();

        // Mock de la excepción de validación
        MethodArgumentNotValidException ex = Mockito.mock(MethodArgumentNotValidException.class);

        ResponseEntity<String> respuesta = manejador.handleBadRequest(ex);

        assertEquals(HttpStatus.BAD_REQUEST, respuesta.getStatusCode());
    }

    // ... tus tests anteriores ...

    @Test
    @DisplayName("Debe devolver 400 cuando el JSON es ilegible (HttpMessageNotReadableException)")
    void handleBadRequest_JsonIlegible_Devuelve400() {
        GlobalExceptionHandler manejador = new GlobalExceptionHandler();

        // Usamos el import
        // org.springframework.http.converter.HttpMessageNotReadableException;
        org.springframework.http.converter.HttpMessageNotReadableException ex = Mockito
                .mock(org.springframework.http.converter.HttpMessageNotReadableException.class);

        ResponseEntity<String> respuesta = manejador.handleBadRequest(ex);

        assertEquals(HttpStatus.BAD_REQUEST, respuesta.getStatusCode());
        assertTrue(respuesta.getBody().contains("Datos inválidos"));
    }

    @Test
    @DisplayName("Debe devolver 400 cuando un argumento en URL tiene el tipo incorrecto")
    void handleBadRequest_TypeMismatch_Devuelve400() {
        GlobalExceptionHandler manejador = new GlobalExceptionHandler();

        // Usamos el import
        // org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
        org.springframework.web.method.annotation.MethodArgumentTypeMismatchException ex = Mockito
                .mock(org.springframework.web.method.annotation.MethodArgumentTypeMismatchException.class);

        ResponseEntity<String> respuesta = manejador.handleBadRequest(ex);

        assertEquals(HttpStatus.BAD_REQUEST, respuesta.getStatusCode());
    }

}