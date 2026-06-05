package com.navium.andenes.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import io.swagger.v3.oas.annotations.media.Schema;

/**
 * Anden representa el espacio fisico real de un anden en el puerto/centro logistico.
 */
@Entity
@Table(name = "andenes", uniqueConstraints = {@UniqueConstraint(columnNames = {"zona", "numero"})})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Schema(description = "Representa el espacio fisico real de un anden en el puerto/centro logistico")
public class Anden {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Schema(description = "Identificador interno", example = "1", accessMode = Schema.AccessMode.READ_ONLY)
    private Long id; // identificador interno

    @Column(name = "zona", nullable = false)
    @Schema(description = "Zona del anden", example = "A")
    private String zona; // A, B, C, DE, etc

    @Column(name = "numero", nullable = false)
    @Schema(description = "Numero del anden dentro de la zona", example = "12")
    private int numero; // 12, 5, 3, etc

    @Column(name = "codigo", nullable = false, unique = true)
    @Schema(description = "Codigo compuesto por zona + numero", example = "A12", accessMode = Schema.AccessMode.READ_ONLY)
    private String codigo; // commpuesto de zona + numero; A12, B4, etc

    @Column(name = "tipo", nullable = false)
    @Enumerated(EnumType.STRING)
    @Schema(description = "Tipo de anden")
    private TipoAnden tipo;

    @Column(name = "estado", nullable = false)
    @Enumerated(EnumType.STRING)
    @Schema(description = "Estado operativo del anden")
    private EstadoAnden estado;

    @Column(name = "sector")
    @Schema(description = "Sector opcional dentro de la zona", example = "Norte")
    private String sector;
    
    /**
     * Metodo para generar codigo al momento de escritura en repositorio
     */
    @PrePersist
    @PreUpdate
    public void generarCodigo() {
        if (zona != null) {
            this.codigo = zona + numero;
        }
    }
    
}
