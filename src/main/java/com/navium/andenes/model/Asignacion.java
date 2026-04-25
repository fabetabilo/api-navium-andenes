package com.navium.andenes.model;

import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Asignacion representa el historial de {@link Anden}
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Asignacion {
    
    private Long id;
    private Long andenId;
    private String patenteTransporte;
    private Long contenedorId;
    private LocalDateTime horaInicio;
    private LocalDateTime horaFin; // null si esta activa, temporal!!
    
}