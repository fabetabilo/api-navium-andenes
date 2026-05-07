package com.navium.andenes.exception;

import java.util.HashMap;
import java.util.Map;

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
	public ResponseEntity<Map<String, Object>> manejarErroresValidacion(RuntimeException ex) {
		return new ResponseEntity<>(crearRespuesta(ESTADO_VALIDACION, MENSAJE_VALIDACION), HttpStatus.BAD_REQUEST);
	}
    
	@ExceptionHandler(NotFoundException.class)
	public ResponseEntity<Map<String, Object>> manejarNotFound(NotFoundException ex) {
		return new ResponseEntity<>(crearRespuesta(ESTADO_NOT_FOUND, MENSAJE_NOT_FOUND), HttpStatus.NOT_FOUND);
	}
    
	@ExceptionHandler(RuntimeException.class)
	public ResponseEntity<Map<String, Object>> manejarRuntime(RuntimeException ex) {
		return new ResponseEntity<>(crearRespuesta(ESTADO_VALIDACION, MENSAJE_VALIDACION), HttpStatus.BAD_REQUEST);
	}
    
	@ExceptionHandler(Exception.class)
	public ResponseEntity<Map<String, Object>> manejarGeneric(Exception ex) {
		return new ResponseEntity<>(crearRespuesta(ESTADO_INTERNAL, MENSAJE_INTERNAL), HttpStatus.INTERNAL_SERVER_ERROR);
	}
    
    /** Respuesta reutilizable */
	private Map<String, Object> crearRespuesta(String estado, String mensaje) {
		Map<String, Object> respuesta = new HashMap<>();
		respuesta.put("estado", estado);
		respuesta.put("mensaje", mensaje);
		return respuesta;
	}
}
