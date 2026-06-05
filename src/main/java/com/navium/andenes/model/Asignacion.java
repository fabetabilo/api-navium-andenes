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
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Asignacion representa el historial de {@link Anden}
 */
@Entity
@Table(name = "asignaciones")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Representa el historial de asignaciones de un anden")
public class Asignacion {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "Identificador interno", example = "10", accessMode = Schema.AccessMode.READ_ONLY)
    private Long id;

    @Column(name = "anden_id", nullable = false)
    @Schema(description = "Identificador del anden asignado", example = "3")
    private Long andenId;

    @Column(name = "patente_transporte", nullable = false, length = 10)
    @Schema(description = "Patente del transporte asociado", example = "ABCD12")
    private String patenteTransporte;

    @Column(name = "contenedor_id", nullable = false)
    @Schema(description = "Identificador del contenedor", example = "2001")
    private Long contenedorId;

    @Column(name = "hora_inicio", nullable = false)
    @Schema(description = "Hora de inicio de la asignacion", example = "2026-05-07T09:15:00")
    private LocalDateTime horaInicio;

    @Column(name = "hora_fin")
    @Schema(description = "Hora de fin de la asignacion (null si activa)", example = "2026-05-07T10:10:00", nullable = true)
    private LocalDateTime horaFin; // null si esta activa, temporal!!
    
}