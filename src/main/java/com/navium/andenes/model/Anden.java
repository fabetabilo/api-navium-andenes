package com.navium.andenes.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Anden representa el espacio fisico real de un anden en el puerto/centro logistico.
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Anden {
    
    private Long id; // identificador interno
    private String zona; // A, B, C, DE, etc
    private int numero; // 12, 5, 3, etc
    private String codigo; // commpuesto de zona + numero; A12, B4, etc
    private TipoAnden tipo;
    private EstadoAnden estado;
    private String sector;
}
