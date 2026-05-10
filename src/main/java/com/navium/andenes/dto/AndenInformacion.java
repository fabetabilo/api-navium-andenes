package com.navium.andenes.dto;

import java.time.LocalDateTime;

/**
 * DTO (Data Transfer Object) que representa la información de un Anden junto con su asignación activa.
 * 
 * <p>Este record se utiliza como DTO para simplificar la respuesta API cuando se consulta un andén
 * con su asignación actual. El objetivo es aligerar la respuesta devolviendo solo la información
 * esencial para el cliente, evitando la transferencia de datos redundantes o innecesarios.</p>
 * 
 * <h3>Relación Andén-Asignación</h3>
 * <p>Un {@link com.navium.andenes.model.Anden} puede tener una {@link com.navium.andenes.model.Asignacion}
 * activa en un momento dado. Esta asignación representa el contenedor y transporte actualmente
 * asignados al andén. Cuando el andén está libre, los campos de asignación son null.</p>
 * 
 * <h3>Código de Andén</h3>
 * <p>El {@code codigo} es un identificador compuesto que combina la zona y el número del andén
 * (ej: "A12" para zona A, número 12). Este campo sirve como identificador único de negocio
 * del andén, eliminando la necesidad de exponer el ID técnico de base de datos.</p>
 * 
 * @param codigo Identificador compuesto del andén (zona + número), ej: "A12"
 * @param tipo Tipo de andén (ej: "CARGA", "DESCARGA")
 * @param estado Estado actual del andén (ej: "DISPONIBLE", "OCUPADO", "MANTENIMIENTO")
 * @param asignacionId ID de la asignación activa, null si el andén está libre
 * @param patenteTransporte Patente del transporte asignado, null si no hay asignación
 * @param contenedorId ID del contenedor asignado, null si no hay asignación
 * @param horaInicio Fecha y hora de inicio de la asignación, null si no hay asignación
 * @param horaFin Fecha y hora de fin de la asignación, null si la asignación está activa
 */
public record AndenInformacion(
    String codigo,
    String tipo,
    String estado,
    Long asignacionId, // Very importante
    String patenteTransporte,
    Long contenedorId,
    LocalDateTime horaInicio,
    LocalDateTime horaFin
) {}
