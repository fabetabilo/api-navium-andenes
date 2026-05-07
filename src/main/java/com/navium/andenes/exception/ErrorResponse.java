package com.navium.andenes.exception;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Schema(description = "Respuesta de error estandarizada para la API")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ErrorResponse {

    @Schema(description = "Codigo de error estandar", example = "NOT_FOUND")
    private String estado;

    @Schema(description = "Mensaje legible del error", example = "Recurso no encontrado")
    private String mensaje;
}
