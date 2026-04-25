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

        return ResponseEntity.ok(andenService.obtenerTodosAndenes());
    }
    
    /**
     * Obtiene todos los andenes con estado DISPONIBLE
     * TEMPORAL!! mas adelante, diferir por ESTADO
     */
    @GetMapping("/disponibles")
    public ResponseEntity<List<Anden>> obtenerDisponibles() {
        
        return ResponseEntity.ok(andenService.obtenerAndenesDisponibles());
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
     * Libera un Anden
     */
    @PostMapping("/{id}/liberar")
    public ResponseEntity<Void> liberarAnden(@PathVariable Long id) {
        andenService.liberarAnden(id);

        return ResponseEntity.noContent().build();
    }
}