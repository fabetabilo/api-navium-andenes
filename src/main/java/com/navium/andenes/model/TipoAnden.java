package com.navium.andenes.model;

import io.swagger.v3.oas.annotations.media.Schema;

/** Define el tipo de Anden fisico en el puerto */
@Schema(description = "Tipo fisico de anden")
public enum TipoAnden {

    @Schema(description = "Anden destinado a carga")
    CARGA,
    @Schema(description = "Anden destinado a descarga")
    DESCARGA,
    @Schema(description = "Anden refrigerado")
    REFRIGERADO
    
}
