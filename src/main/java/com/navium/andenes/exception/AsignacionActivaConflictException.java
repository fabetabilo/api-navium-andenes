package com.navium.andenes.exception;

/**
 * Indica conflicto por asignacion activa para el mismo anden.
 */
public class AsignacionActivaConflictException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    public AsignacionActivaConflictException(String message) {
        super(message);
    }
}
