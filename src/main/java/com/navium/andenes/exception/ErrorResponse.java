package com.navium.andenes.exception;

import java.time.OffsetDateTime;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Schema(description = "Respuesta de error estandarizada para la API")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ErrorResponse {

    @Schema(description = "Codigo de error estandar", example = "NOT_FOUND")
    private String estado;

    @Schema(description = "Mensaje legible del error", example = "Recurso no encontrado")
    private String mensaje;

    @Schema(description = "Codigo HTTP asociado al error", example = "404")
    private int status;

    @Schema(description = "Fecha/hora del error (UTC)", example = "2026-05-07T13:45:30Z")
    private OffsetDateTime timestamp;

    @Schema(description = "Detalle opcional (util para debugging)", example = "Anden no encontrado", nullable = true)
    private String detalle;
}
