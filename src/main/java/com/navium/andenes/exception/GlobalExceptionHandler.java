package com.navium.andenes.exception;

import java.time.OffsetDateTime;
import java.time.ZoneOffset;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * Para facilitar el uso en frontends, se utiliza serializacion de respuestas en JSON
 */
@RestControllerAdvice
public class GlobalExceptionHandler {
    
	private static final String ESTADO_VALIDACION = "ERROR_DE_VALIDACION";
	private static final String ESTADO_NOT_FOUND = "NOT_FOUND";
	private static final String ESTADO_INTERNAL = "ERROR_INTERNO";
    
	private static final String MENSAJE_VALIDACION = "Error en la solicitud";
	private static final String MENSAJE_NOT_FOUND = "Recurso no encontrado";
	private static final String MENSAJE_INTERNAL = "Error interno del servidor";
    
	@ExceptionHandler({ IllegalArgumentException.class, IllegalStateException.class })
	public ResponseEntity<ErrorResponse> manejarErroresValidacion(RuntimeException ex) {
		return build(HttpStatus.BAD_REQUEST, ESTADO_VALIDACION, MENSAJE_VALIDACION, ex.getMessage());
	}
    
	@ExceptionHandler(NotFoundException.class)
	public ResponseEntity<ErrorResponse> manejarNotFound(NotFoundException ex) {
		return build(HttpStatus.NOT_FOUND, ESTADO_NOT_FOUND, MENSAJE_NOT_FOUND, ex.getMessage());
	}
    
	@ExceptionHandler(RuntimeException.class)
	public ResponseEntity<ErrorResponse> manejarRuntime(RuntimeException ex) {
		return build(HttpStatus.BAD_REQUEST, ESTADO_VALIDACION, MENSAJE_VALIDACION, ex.getMessage());
	}
    
	@ExceptionHandler(Exception.class)
	public ResponseEntity<ErrorResponse> manejarGeneric(Exception ex) {
		return build(HttpStatus.INTERNAL_SERVER_ERROR, ESTADO_INTERNAL, MENSAJE_INTERNAL, null);
	}

	private ResponseEntity<ErrorResponse> build(HttpStatus status, String estado, String mensaje, String detalle) {
		ErrorResponse body = ErrorResponse.builder()
			.estado(estado)
			.mensaje(mensaje)
			.status(status.value())
			.timestamp(OffsetDateTime.now(ZoneOffset.UTC))
			.detalle(detalle)
			.build();
		return new ResponseEntity<>(body, status);
	}
}
