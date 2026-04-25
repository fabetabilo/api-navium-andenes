package com.navium.andenes.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
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
        
        return ResponseEntity.ok(andenService.obtenerAndenPorId(id));
    }
    
    /**
     * Obtiene todos los andenes existentes
     */
    @GetMapping
    public ResponseEntity<List<Anden>> obtenerTodos() {

        return ResponseEntity.ok(andenService.obtenerAndenes());
    }
    
    /**
     * Obtiene todos los andenes por zona
     */
    @GetMapping("/zona/{zona}")
    public ResponseEntity<List<Anden>> obtenerPorZona(@PathVariable String zona) {
        
        return ResponseEntity.ok(andenService.obtenerPorZona(zona));
    }
    
    /**
     * Obtiene un Anden por codigo compuesto (zona + numero)
     */
    @GetMapping("/codigo/{codigo}")
    public ResponseEntity<Anden> obtenerPorCodigo(@PathVariable String codigo) {
        return ResponseEntity.ok(andenService.obtenerPorCodigo(codigo));
    }
    
    /**
     * Obtiene todos los andenes con estado DISPONIBLE
     */
    @GetMapping("/disponibles")
    public ResponseEntity<List<Anden>> obtenerDisponibles() {
        
        return ResponseEntity.ok(andenService.obtenerAndenesDisponibles());
    }

    /**
     * Obtiene todos los andenes de estado OCUPADO
     */
    @GetMapping("/ocupados")
    public ResponseEntity<List<Anden>> obtenerOcupados() {
        
        return ResponseEntity.ok(andenService.obtenerAndenesOcupados());
    }

    /**
     * Obtiene todos los andenes en estado MANTENIMIENTO
     */
    @GetMapping("/mantenimiento")
    public ResponseEntity<List<Anden>> obtenerEnMantenimiento() {
        
        return ResponseEntity.ok(andenService.obtenerAndenesMantenimiento());
    }

    /**
     * Crea un nuevo Anden
     */
    @PostMapping
    public ResponseEntity<Anden> crearAnden(@RequestBody Anden anden) {
        try {
            Anden adn = this.andenService.crearAnden(anden);
            
            return ResponseEntity.status(HttpStatus.CREATED).body(adn);
            
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }
    
    /**
     * Eliminar un Anden
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
        andenService.eliminarAnden(id);
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