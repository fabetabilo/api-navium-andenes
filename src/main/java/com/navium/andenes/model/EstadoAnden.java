package com.navium.andenes.model;

import io.swagger.v3.oas.annotations.media.Schema;

/** Estado real y fisico de Anden */
@Schema(description = "Estado operativo del anden")
public enum EstadoAnden {

    @Schema(description = "Disponible para asignacion")
    DISPONIBLE,
    @Schema(description = "Ocupado por una asignacion activa")
    OCUPADO,
    @Schema(description = "En mantenimiento, no disponible")
    MANTENIMIENTO

}
