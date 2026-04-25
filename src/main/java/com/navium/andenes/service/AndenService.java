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
        validarId(id, "AndenId");
        return andenRepository.findById(id)
                              .orElseThrow(() -> new RuntimeException("Anden no encontrado"));
    }
    
    @Transactional(readOnly = true)
    public List<Anden> obtenerAndenes() {
        return andenRepository.findAll();
    }

    @Transactional(readOnly = true)
    public List<Anden> obtenerPorZona(String zona) {
        return andenRepository.findByZona(zona.toUpperCase());
    }
    
    /**
     * Obtiene un Anden por codigo compuesto: zona + numero
     */
    @Transactional(readOnly = true)
    public Anden obtenerPorCodigo(String codigo) {
    return andenRepository.findByCodigo(codigo.toUpperCase())
                          .orElseThrow(() -> new RuntimeException("Anden CODIGO: " + codigo + " no encontrado"));
    }
    
    @Transactional(readOnly = true)
    public List<Anden> obtenerAndenesDisponibles() {
        return andenRepository.findByEstado(EstadoAnden.DISPONIBLE);
    }

    @Transactional(readOnly = true)
    public List<Anden> obtenerAndenesOcupados() {
        return andenRepository.findByEstado(EstadoAnden.OCUPADO);
    }

    @Transactional(readOnly = true)
    public List<Anden> obtenerAndenesMantenimiento() {
        return andenRepository.findByEstado(EstadoAnden.MANTENIMIENTO);
    }
    
    // TODO: Obtener andenes por zona + estado
    
    /**
     * Marca a un Anden en estado MANTENIMIENTO.
     * REGLA: Un anden con stado OCUPADO no puede marcarse en MANTENIMIENTO
     * @param id Anden a marcar como en MANTENIMIENTO
     */
    @Transactional
    public Anden marcarAndenEnMantenimiento(Long id) {
        Anden anden = obtenerAndenPorId(id);
        if (anden.getEstado() == EstadoAnden.OCUPADO) {
            throw new RuntimeException("No se puede colocar en mantenimiento un anden ocupado");
        }
        anden.setEstado(EstadoAnden.MANTENIMIENTO);
        
        return andenRepository.save(anden);
    }
    
    /**
     * Habilita un Anden en mantenimiento. Cambia su estado de MANTENIMIENTO a DISPONIBLE
     * @param id Anden a habilitar
     */
    @Transactional
    public Anden habilitarAnden(Long id) {
        Anden anden = obtenerAndenPorId(id);
        if (anden.getEstado() != EstadoAnden.MANTENIMIENTO) {
            throw new RuntimeException("El anden no se encuentra en mantenimiento");
        }
        anden.setEstado(EstadoAnden.DISPONIBLE);
        
        return andenRepository.save(anden);
    }
    
    /**
     * Crea una asignacion respecto a un anden, transporte y contenedor.
     * @param andenId Anden a asignar
     * @param patente Transporte asociado a la asignacion
     * @param contenedorId Contenedor asociado a la asignacion
     * @return Asignacion
     */
    @Transactional
    public Asignacion asignarAnden(Long andenId, String patente, Long contenedorId) {
        
        validarId(andenId, "AndenId");
        validarTexto(patente, "Patente");
        validarId(contenedorId, "ContenedorId");
        
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
        validarId(andenId, "AndenId");
        Anden anden = andenRepository.findById(andenId).orElseThrow(() -> new RuntimeException("Anden no encontrado"));
        
        if (anden.getEstado() == EstadoAnden.DISPONIBLE) {
            throw new IllegalStateException("Anden ya esta disponible");
        }
        if (anden.getEstado() == EstadoAnden.MANTENIMIENTO) {
            throw new IllegalStateException("No se puede liberar un anden en mantenimiento"); // ANDEN en mantenimiento se habilita mediante su propio metodo
        }

        Asignacion asignacion = asignacionRepository.findByAndenIdAndHoraFinIsNull(andenId)
                                                    .orElseThrow(() -> new RuntimeException("No existe asignacion activa para Anden: " + andenId));
        
        // cerrar asignacion
        asignacion.setHoraFin(LocalDateTime.now());
        asignacionRepository.save(asignacion);
        
        // liberar anden
        anden.setEstado(EstadoAnden.DISPONIBLE);
        andenRepository.save(anden);
    }
    
    // --- CREATES - UPDATES - DELETES
    
    /**
     * Crea y normaliza un Anden
     * @param anden Anden a crear
     */
    @Transactional
    public Anden crearAnden(Anden anden) {
        validarTexto(anden.getZona(), "Zona");
        if (anden.getNumero() <= 0) {
            throw new IllegalArgumentException("Numero de anden invalido");
        }
        
        anden.setZona(anden.getZona().trim().toUpperCase());
        
        // valida que el codigo compuesto no se repita
        if (andenRepository.existsByZonaAndNumero(anden.getZona(), anden.getNumero())) {
            throw new IllegalStateException("Ya existe un anden en esa zona y numero");
        }
        
        // estado por defecto DISPONIBLE
        if (anden.getEstado() == null) {
            anden.setEstado(EstadoAnden.DISPONIBLE);
        }
        
        return this.andenRepository.save(anden);
    }
    
    /**
     * Elimina un Anden. Este debe NO puede estar ocupado.
     * @param anden Anden a eliminar
     */
    @Transactional
    public void eliminarAnden(Long id) {
        if (!andenRepository.existsById(id)) {
            throw new RuntimeException("Anden no encontrado para eliminar");
        }
        // validar que no este ocupado antes de borrar
        Anden anden = obtenerAndenPorId(id);
        if (anden.getEstado() == EstadoAnden.OCUPADO) {
            throw new RuntimeException("Anden esta OCUPADO no se puede eliminar");
        }
        this.andenRepository.deleteById(id);
    }

    // --- VALIDADORES con excepciones ---
    private void validarTexto(String valor, String campo) {
        if (valor == null || valor.trim().isEmpty()) {
            throw new IllegalArgumentException(campo + " no puede ser vacio");
        }
    }

    private void validarId(Long id, String campo) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException(campo + " invalido");
        }
    }
    
    
}