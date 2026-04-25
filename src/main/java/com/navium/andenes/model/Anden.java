package com.navium.andenes.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Anden representa el espacio fisico real de un anden en el puerto/centro logistico.
 */
@Entity
@Table(name = "andenes", uniqueConstraints = {@UniqueConstraint(columnNames = {"zona", "numero"})})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Anden {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id; // identificador interno

    @Column(nullable = false)
    private String zona; // A, B, C, DE, etc

    @Column(nullable = false)
    private int numero; // 12, 5, 3, etc

    @Column(nullable = false, unique = true)
    private String codigo; // commpuesto de zona + numero; A12, B4, etc

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private TipoAnden tipo;

    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private EstadoAnden estado;

    @Column(nullable = true)
    private String sector;
}
