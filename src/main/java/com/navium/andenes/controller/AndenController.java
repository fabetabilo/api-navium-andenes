package com.navium.andenes.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.navium.andenes.model.Anden;
import com.navium.andenes.model.Asignacion;
import com.navium.andenes.service.AndenService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v0/andenes")
@RequiredArgsConstructor
public class AndenController {
    
    private final AndenService andenService;
    
    /**
     * Obtiene un Anden por id
     */
    @GetMapping("/{id}")
    public ResponseEntity<Anden> obtenerPorId(@PathVariable Long id) {
        Anden anden = this.andenService.obtenerAndenPorId(id);
        return ResponseEntity.ok(anden);
    }
    
    /**
     * Obtiene todos los andenes existentes
     */
    @GetMapping
    public ResponseEntity<List<Anden>> obtenerTodos() {
        List<Anden> andenes = this.andenService.obtenerAndenes();
        if (andenes.isEmpty()) {
            return ResponseEntity.noContent().build();
            
        }
        return ResponseEntity.ok(andenes);
    }
    
    /**
     * Obtiene todos los andenes por zona
     */
    @GetMapping("/zona/{zona}")
    public ResponseEntity<List<Anden>> obtenerPorZona(@PathVariable String zona) {
        List<Anden> andenes = this.andenService.obtenerPorZona(zona);
        if (andenes.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(andenes);
    }
    
    /**
     * Obtiene un Anden por codigo compuesto (zona + numero)
     */
    @GetMapping("/codigo/{codigo}")
    public ResponseEntity<Anden> obtenerPorCodigo(@PathVariable String codigo) {
        Anden anden = this.andenService.obtenerPorCodigo(codigo);
        return ResponseEntity.ok(anden);
    }
    
    /**
     * Obtiene todos los andenes con estado DISPONIBLE
     */
    @GetMapping("/disponibles")
    public ResponseEntity<List<Anden>> obtenerDisponibles() {
        List<Anden> andenes = this.andenService.obtenerAndenesDisponibles();
        if (andenes.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(andenes);
    }

    /**
     * Obtiene todos los andenes de estado OCUPADO
     */
    @GetMapping("/ocupados")
    public ResponseEntity<List<Anden>> obtenerOcupados() {
        List<Anden> andenes = this.andenService.obtenerAndenesOcupados();
        if (andenes.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(andenes);
    }

    /**
     * Obtiene todos los andenes en estado MANTENIMIENTO
     */
    @GetMapping("/mantenimiento")
    public ResponseEntity<List<Anden>> obtenerEnMantenimiento() {
        List<Anden> andenes = this.andenService.obtenerAndenesMantenimiento();
        if (andenes.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(andenes);
    }

    /**
     * Crea un nuevo Anden
     */
    @PostMapping
    public ResponseEntity<Anden> crearAnden(@RequestBody Anden anden) {
        Anden adn = this.andenService.crearAnden(anden);
        return ResponseEntity.status(201).body(adn);
    }
    
    /**
     * Eliminar un Anden
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarAnden(@PathVariable Long id) {
        this.andenService.eliminarAnden(id);
        return ResponseEntity.noContent().build();
    }
    
    
    
    // --- METODOS DE NEGOCIO ---
     
    /**
     * Realiza la asignacion de contenedor y transporte a un Anden
     */
    @PostMapping("/{id}/asignar")
    public ResponseEntity<Asignacion> asignarAnden(@PathVariable Long id, @RequestParam String patente, @RequestParam Long contenedorId) {
        Asignacion asignacion = andenService.asignarAnden(id, patente, contenedorId);
        return ResponseEntity.ok(asignacion);
    }
    
    /**
     * Marca a Anden en estado de mantenimiento
     */
    @PostMapping("/{id}/mantenimiento")
    public ResponseEntity<Anden> marcarAndenEnMantenimiento(@PathVariable Long id) {
        Anden anden = andenService.marcarAndenEnMantenimiento(id);
        return ResponseEntity.ok(anden);
    }
    
    /**
     * Habilita a un Anden que esta en mantenimiento
     */
    @PostMapping("/{id}/habilitar")
    public ResponseEntity<Anden> habilitarAnden(@PathVariable Long id) {
        Anden anden = andenService.habilitarAnden(id);
        return ResponseEntity.ok(anden);
    }
    
    /**
     * Libera un Anden
     */
    @PostMapping("/{id}/liberar")
    public ResponseEntity<Void> liberarAnden(@PathVariable Long id) {
        andenService.liberarAnden(id);
        return ResponseEntity.noContent().build();
    }
}