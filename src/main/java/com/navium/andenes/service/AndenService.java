package com.navium.andenes.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;
import org.springframework.dao.DataIntegrityViolationException;

import com.navium.andenes.dto.AndenInformacion;
import com.navium.andenes.exception.AsignacionActivaConflictException;
import com.navium.andenes.exception.NotFoundException;
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
                              .orElseThrow(() -> new NotFoundException("Anden no encontrado"));
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
                          .orElseThrow(() -> new NotFoundException("Anden CODIGO: " + codigo + " no encontrado"));
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
    
    /**
     * Obtiene un Anden con su asignacion
     */
    @Transactional(readOnly = true)
    public AndenInformacion obtenerAndenConAsignacion(Long id) {
        Anden anden = obtenerAndenPorId(id);

        Optional<Asignacion> asignacionOpt = asignacionRepository.findByAndenIdAndHoraFinIsNull(id);

        return new AndenInformacion(
            anden.getCodigo(),
            anden.getTipo().name(),
            anden.getEstado().name(),
            asignacionOpt.map(Asignacion::getId).orElse(null),
            asignacionOpt.map(Asignacion::getPatenteTransporte).orElse(null),
            asignacionOpt.map(Asignacion::getContenedorId).orElse(null),
            asignacionOpt.map(Asignacion::getHoraInicio).orElse(null),
            asignacionOpt.map(Asignacion::getHoraFin).orElse(null)
        );
    }

    /**
     * Obtiene todos los andenes ocupados con su asignacion activa
     * 
     * Obteniene unicamente andenes en estado OCUPADO (aquellos que tienen una asignacion activa) 
     * y realiza una sola consulta batch para obtener todas las asignaciones correspondientes, 
     * evitando el problema N+1
     * 
     * >>si me acuerdo, hacer indices en la base de datos<<
     * 
     * @return Lista de andenes ocupados con su informacion de asignaciin
     */
    @Transactional(readOnly = true)
    public List<AndenInformacion> obtenerAndenesOcupadosConAsignacion() {
        // si esta ocupado, tiene asignacion
        List<Anden> andenesOcupados = andenRepository.findByEstado(EstadoAnden.OCUPADO);
        
        if (andenesOcupados.isEmpty()) {
            return List.of();
        }
        
        List<Long> andenIds = andenesOcupados.stream()
                .map(Anden::getId)
                .toList();
        
        // obtiene todas las asignaciones en una sola query batch evitando N+1
        List<Asignacion> asignaciones = asignacionRepository.findByAndenIdIn(andenIds);
        
        // toma la asignacion mas reciente por horaInicio y mapea
        Map<Long, Asignacion> asignacionPorAndenId = asignaciones.stream()
                .filter(a -> a.getHoraInicio() != null)
                .collect(Collectors.toMap(
                        Asignacion::getAndenId,
                        asignacion -> asignacion,
                        (existing, replacement) -> existing.getHoraInicio().isAfter(replacement.getHoraInicio()) ? existing : replacement
                ));
        
        return andenesOcupados.stream()
                .map(anden -> {
                    Asignacion asignacion = asignacionPorAndenId.get(anden.getId());
                    return new AndenInformacion(
                        anden.getCodigo(),
                        anden.getTipo().name(),
                        anden.getEstado().name(),
                        asignacion != null ? asignacion.getId() : null,
                        asignacion != null ? asignacion.getPatenteTransporte() : null,
                        asignacion != null ? asignacion.getContenedorId() : null,
                        asignacion != null ? asignacion.getHoraInicio() : null,
                        asignacion != null ? asignacion.getHoraFin() : null
                    );
                })
                .toList();
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
                                     .orElseThrow(() -> new NotFoundException("Anden no encontrado"));

        if (asignacionRepository.findByAndenIdAndHoraFinIsNull(andenId).isPresent()) {
            throw new AsignacionActivaConflictException("Ya existe una asignacion activa para el anden: " + andenId);
        }
        
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
        
        try {
            return asignacionRepository.save(asignacion);
        } catch (DataIntegrityViolationException ex) {
            throw new AsignacionActivaConflictException("Ya existe una asignacion activa para el anden: " + andenId);
        }
    }
    
    /**
     * Libera un anden de su asignacion.
     * 
     * El cierra la asignacion activa del anden estableciendo {@code horaFin}
     * con la fecha y hora actual, y cambia el estado del Anden a DISPONIBLE
     * 
     * REGLAS:
     * El anden no puede estar ya en estado DISPONIBLE</li>
     * El anden no puede estar en estado MANTENIMIENTO (usar {@link #habilitarAnden(Long)})</li>
     * Debe existir una asignacion activa (horaFin null) para el Anden
     * 
     * (queda pendiente y sujeta a cambios la logica)
     * 
     * @param andenId Anden a liberar
     */
    @Transactional
    public void liberarAnden(Long andenId) {
        validarId(andenId, "AndenId");
        Anden anden = andenRepository.findById(andenId).orElseThrow(() -> new NotFoundException("Anden no encontrado"));
        
        if (anden.getEstado() == EstadoAnden.DISPONIBLE) {
            throw new IllegalStateException("Anden ya esta disponible");
        }
        if (anden.getEstado() == EstadoAnden.MANTENIMIENTO) {
            throw new IllegalStateException("No se puede liberar un anden en mantenimiento"); // ANDEN en mantenimiento se habilita mediante su propio metodo
        }

        Asignacion asignacion = asignacionRepository.findByAndenIdAndHoraFinIsNull(andenId)
                                                    .orElseThrow(() -> new NotFoundException("No existe asignacion activa para Anden: " + andenId));
        
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
            throw new NotFoundException("Anden no encontrado para eliminar");
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