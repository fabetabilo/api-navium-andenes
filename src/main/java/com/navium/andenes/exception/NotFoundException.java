package com.navium.andenes.exception;

/**
 * diferencia errores de "recurso no encontrado" y mapea consistente a HTTP 404
 * sin depender del texto del mensaje
 */
public class NotFoundException extends RuntimeException {
    
    private static final long serialVersionUID = 1L;
    
    public NotFoundException(String message) {
        super(message);
    }
}
