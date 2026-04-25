package com.navium.andenes.model;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Asignacion representa el historial de {@link Anden}
 */
@Entity
@Table(name = "asignaciones")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Asignacion {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private Long andenId;

    @Column(nullable = false, length = 10)
    private String patenteTransporte;

    @Column(nullable = false)
    private Long contenedorId;

    @Column(nullable = false)
    private LocalDateTime horaInicio;

    @Column(nullable = true)
    private LocalDateTime horaFin; // null si esta activa, temporal!!
    
}