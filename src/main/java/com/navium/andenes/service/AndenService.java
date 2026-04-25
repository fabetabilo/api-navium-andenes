package com.navium.andenes.service;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.stereotype.Service;

import com.navium.andenes.model.Anden;
import com.navium.andenes.model.Asignacion;
import com.navium.andenes.model.EstadoAnden;
import com.navium.andenes.repository.AndenRepository;
import com.navium.andenes.repository.AsignacionRepository;

import org.springframework.transaction.annotation.Transactional;
import lombok.RequiredArgsConstructor;

/**
 * Servicio de Anden.
 * Omite una interface y la implementacion de la interface de service
 */
@Service
@RequiredArgsConstructor
public class AndenService {
    
    private final AndenRepository andenRepository;
    private final AsignacionRepository asignacionRepository;
    
    @Transactional(readOnly = true)
    public Anden obtenerAndenPorId(Long id) {
        return andenRepository.findById(id)
                              .orElseThrow(() -> new RuntimeException("Anden no encontrado"));
    }
    
    @Transactional(readOnly = true)
    public List<Anden> obtenerTodosAndenes() {
        return andenRepository.findAll();
    }
    
    @Transactional(readOnly = true)
    public List<Anden> obtenerAndenesDisponibles() {
        return andenRepository.findByEstado(EstadoAnden.DISPONIBLE);
    }

    // To do: metodos obtener por zona, codigo [...]

    /**
     * Crea una asignacion respecto a un anden, transporte y contenedor.
     * @param andenId Anden a asignar
     * @param patente Transporte asociado a la asignacion
     * @param contenedorId Contenedor asociado a la asignacion
     * @return Asignacion
     */
    @Transactional
    public Asignacion asignarAnden(Long andenId, String patente, Long contenedorId) {
        
        Anden anden = andenRepository.findById(andenId)
                                     .orElseThrow(() -> new RuntimeException("Anden no encontrado"));
        
        if (anden.getEstado() != EstadoAnden.DISPONIBLE) {
            throw new IllegalStateException("Anden no disponible");
        }
        
        // asignacion de anden
        anden.setEstado(EstadoAnden.OCUPADO);
        andenRepository.save(anden);
        
        Asignacion asignacion = new Asignacion();
        asignacion.setAndenId(andenId);
        asignacion.setPatenteTransporte(patente);
        asignacion.setContenedorId(contenedorId);
        asignacion.setHoraInicio(LocalDateTime.now());
        
        return asignacionRepository.save(asignacion);
    }
    
    /**
     * Libera un anden de su asignacion
     * @param andenId Anden a liberar
     */
    @Transactional
    public void liberarAnden(Long andenId) {
        
        Anden anden = andenRepository.findById(andenId).orElseThrow(() -> new RuntimeException("Anden no encontrado"));
        
        if (anden.getEstado() == EstadoAnden.DISPONIBLE) {
            throw new IllegalStateException("Anden disponible");
        }
        
        Asignacion asignacion = asignacionRepository.findByAndenIdHoraFinIsNull(andenId)
                                                    .orElseThrow(() -> new RuntimeException("No existe asignacion activa para Anden: " + andenId));
        
        // cerrar asignacion
        asignacion.setHoraFin(LocalDateTime.now());
        asignacionRepository.save(asignacion);
        
        // liberar anden
        anden.setEstado(EstadoAnden.DISPONIBLE);
        andenRepository.save(anden);
    }
    
    // --- CREATES - UPDATES - DELETES
    
    @Transactional
    public Anden crearAnden(Anden anden) {
        return this.andenRepository.save(anden);
    }
    
    @Transactional
    public void eliminarAnden(Long id) {
        this.andenRepository.deleteById(id);
    }
}