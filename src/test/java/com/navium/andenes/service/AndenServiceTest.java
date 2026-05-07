package com.navium.andenes.service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import com.navium.andenes.exception.NotFoundException;
import com.navium.andenes.model.Anden;
import com.navium.andenes.model.Asignacion;
import com.navium.andenes.model.EstadoAnden;
import com.navium.andenes.model.TipoAnden;
import com.navium.andenes.repository.AndenRepository;
import com.navium.andenes.repository.AsignacionRepository;

@ExtendWith(MockitoExtension.class)
class AndenServiceTest {

    @InjectMocks
    private AndenService andenService;

    @Mock
    private AndenRepository andenRepository;

    @Mock
    private AsignacionRepository asignacionRepository;

    @Test
    void obtenerAndenPorId_invalidId_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> andenService.obtenerAndenPorId(null));
        assertThrows(IllegalArgumentException.class, () -> andenService.obtenerAndenPorId(0L));
    }

    @Test
    void obtenerAndenPorId_notFound_throwsNotFoundException() {
        when(andenRepository.findById(10L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> andenService.obtenerAndenPorId(10L));
    }

    @Test
    void obtenerAndenPorId_returnsAnden() {
        Anden anden = buildAnden(10L, "A", 1, EstadoAnden.DISPONIBLE);
        when(andenRepository.findById(10L)).thenReturn(Optional.of(anden));

        Anden result = andenService.obtenerAndenPorId(10L);

        assertEquals(anden, result);
    }

    @Test
    void obtenerAndenes_returnsList() {
        List<Anden> andenes = List.of(buildAnden(1L, "A", 1, EstadoAnden.DISPONIBLE));
        when(andenRepository.findAll()).thenReturn(andenes);

        List<Anden> result = andenService.obtenerAndenes();

        assertEquals(andenes, result);
    }

    @Test
    void obtenerPorZona_normalizesZona() {
        List<Anden> andenes = List.of(buildAnden(1L, "A", 1, EstadoAnden.DISPONIBLE));
        when(andenRepository.findByZona("A")).thenReturn(andenes);

        List<Anden> result = andenService.obtenerPorZona("a");

        assertEquals(andenes, result);
        verify(andenRepository).findByZona("A");
    }

    @Test
    void obtenerPorCodigo_notFound_throwsNotFoundException() {
        when(andenRepository.findByCodigo("A1")).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> andenService.obtenerPorCodigo("a1"));
        verify(andenRepository).findByCodigo("A1");
    }

    @Test
    void obtenerPorCodigo_returnsAnden() {
        Anden anden = buildAnden(2L, "B", 2, EstadoAnden.DISPONIBLE);
        when(andenRepository.findByCodigo("B2")).thenReturn(Optional.of(anden));

        Anden result = andenService.obtenerPorCodigo("b2");

        assertEquals(anden, result);
    }

    @Test
    void obtenerAndenesDisponibles_delegatesToRepository() {
        List<Anden> andenes = List.of(buildAnden(1L, "A", 1, EstadoAnden.DISPONIBLE));
        when(andenRepository.findByEstado(EstadoAnden.DISPONIBLE)).thenReturn(andenes);

        List<Anden> result = andenService.obtenerAndenesDisponibles();

        assertEquals(andenes, result);
    }

    @Test
    void obtenerAndenesOcupados_delegatesToRepository() {
        List<Anden> andenes = List.of(buildAnden(2L, "B", 2, EstadoAnden.OCUPADO));
        when(andenRepository.findByEstado(EstadoAnden.OCUPADO)).thenReturn(andenes);

        List<Anden> result = andenService.obtenerAndenesOcupados();

        assertEquals(andenes, result);
    }

    @Test
    void obtenerAndenesMantenimiento_delegatesToRepository() {
        List<Anden> andenes = List.of(buildAnden(3L, "C", 3, EstadoAnden.MANTENIMIENTO));
        when(andenRepository.findByEstado(EstadoAnden.MANTENIMIENTO)).thenReturn(andenes);

        List<Anden> result = andenService.obtenerAndenesMantenimiento();

        assertEquals(andenes, result);
    }

    @Test
    void marcarAndenEnMantenimiento_rejectsOcupado() {
        Anden anden = buildAnden(4L, "A", 4, EstadoAnden.OCUPADO);
        when(andenRepository.findById(4L)).thenReturn(Optional.of(anden));

        assertThrows(RuntimeException.class, () -> andenService.marcarAndenEnMantenimiento(4L));

        verify(andenRepository, never()).save(any(Anden.class));
    }

    @Test
    void marcarAndenEnMantenimiento_updatesEstado() {
        Anden anden = buildAnden(5L, "A", 5, EstadoAnden.DISPONIBLE);
        when(andenRepository.findById(5L)).thenReturn(Optional.of(anden));
        when(andenRepository.save(any(Anden.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Anden result = andenService.marcarAndenEnMantenimiento(5L);

        assertEquals(EstadoAnden.MANTENIMIENTO, result.getEstado());
    }

    @Test
    void habilitarAnden_rejectsEstadoNoMantenimiento() {
        Anden anden = buildAnden(6L, "A", 6, EstadoAnden.DISPONIBLE);
        when(andenRepository.findById(6L)).thenReturn(Optional.of(anden));

        assertThrows(RuntimeException.class, () -> andenService.habilitarAnden(6L));
    }

    @Test
    void habilitarAnden_updatesEstado() {
        Anden anden = buildAnden(7L, "A", 7, EstadoAnden.MANTENIMIENTO);
        when(andenRepository.findById(7L)).thenReturn(Optional.of(anden));
        when(andenRepository.save(any(Anden.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Anden result = andenService.habilitarAnden(7L);

        assertEquals(EstadoAnden.DISPONIBLE, result.getEstado());
    }

    @Test
    void asignarAnden_invalidInputs_throwIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> andenService.asignarAnden(0L, "AA11", 1L));
        assertThrows(IllegalArgumentException.class, () -> andenService.asignarAnden(1L, " ", 1L));
        assertThrows(IllegalArgumentException.class, () -> andenService.asignarAnden(1L, "AA11", 0L));
    }

    @Test
    void asignarAnden_notFound_throwsNotFoundException() {
        when(andenRepository.findById(9L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> andenService.asignarAnden(9L, "AA11", 12L));
    }

    @Test
    void asignarAnden_rejectsNoDisponible() {
        Anden anden = buildAnden(10L, "A", 10, EstadoAnden.MANTENIMIENTO);
        when(andenRepository.findById(10L)).thenReturn(Optional.of(anden));

        assertThrows(IllegalStateException.class, () -> andenService.asignarAnden(10L, "AA11", 12L));

        verify(asignacionRepository, never()).save(any(Asignacion.class));
    }

    @Test
    void asignarAnden_setsOcupadoAndCreatesAsignacion() {
        Anden anden = buildAnden(11L, "A", 11, EstadoAnden.DISPONIBLE);
        when(andenRepository.findById(11L)).thenReturn(Optional.of(anden));
        when(andenRepository.save(any(Anden.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(asignacionRepository.save(any(Asignacion.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Asignacion result = andenService.asignarAnden(11L, "AA11", 12L);

        ArgumentCaptor<Anden> andenCaptor = ArgumentCaptor.forClass(Anden.class);
        verify(andenRepository).save(andenCaptor.capture());
        assertEquals(EstadoAnden.OCUPADO, andenCaptor.getValue().getEstado());

        ArgumentCaptor<Asignacion> asignacionCaptor = ArgumentCaptor.forClass(Asignacion.class);
        verify(asignacionRepository).save(asignacionCaptor.capture());
        Asignacion savedAsignacion = asignacionCaptor.getValue();
        assertEquals(11L, savedAsignacion.getAndenId());
        assertEquals("AA11", savedAsignacion.getPatenteTransporte());
        assertEquals(12L, savedAsignacion.getContenedorId());
        assertNotNull(savedAsignacion.getHoraInicio());

        assertEquals(savedAsignacion, result);
    }

    @Test
    void liberarAnden_invalidId_throwsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, () -> andenService.liberarAnden(null));
        assertThrows(IllegalArgumentException.class, () -> andenService.liberarAnden(0L));
    }

    @Test
    void liberarAnden_andenDisponible_throwsIllegalStateException() {
        Anden anden = buildAnden(12L, "A", 12, EstadoAnden.DISPONIBLE);
        when(andenRepository.findById(12L)).thenReturn(Optional.of(anden));

        assertThrows(IllegalStateException.class, () -> andenService.liberarAnden(12L));
    }

    @Test
    void liberarAnden_andenMantenimiento_throwsIllegalStateException() {
        Anden anden = buildAnden(13L, "A", 13, EstadoAnden.MANTENIMIENTO);
        when(andenRepository.findById(13L)).thenReturn(Optional.of(anden));

        assertThrows(IllegalStateException.class, () -> andenService.liberarAnden(13L));
    }

    @Test
    void liberarAnden_noAsignacionActiva_throwsNotFoundException() {
        Anden anden = buildAnden(14L, "A", 14, EstadoAnden.OCUPADO);
        when(andenRepository.findById(14L)).thenReturn(Optional.of(anden));
        when(asignacionRepository.findByAndenIdAndHoraFinIsNull(14L)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class, () -> andenService.liberarAnden(14L));
    }

    @Test
    void liberarAnden_success_updatesAsignacionAndAnden() {
        Anden anden = buildAnden(15L, "A", 15, EstadoAnden.OCUPADO);
        Asignacion asignacion = buildAsignacion(15L);
        when(andenRepository.findById(15L)).thenReturn(Optional.of(anden));
        when(asignacionRepository.findByAndenIdAndHoraFinIsNull(15L)).thenReturn(Optional.of(asignacion));
        when(asignacionRepository.save(any(Asignacion.class))).thenAnswer(invocation -> invocation.getArgument(0));
        when(andenRepository.save(any(Anden.class))).thenAnswer(invocation -> invocation.getArgument(0));

        andenService.liberarAnden(15L);

        ArgumentCaptor<Asignacion> asignacionCaptor = ArgumentCaptor.forClass(Asignacion.class);
        verify(asignacionRepository).save(asignacionCaptor.capture());
        assertNotNull(asignacionCaptor.getValue().getHoraFin());

        ArgumentCaptor<Anden> andenCaptor = ArgumentCaptor.forClass(Anden.class);
        verify(andenRepository).save(andenCaptor.capture());
        assertEquals(EstadoAnden.DISPONIBLE, andenCaptor.getValue().getEstado());
    }

    @Test
    void crearAnden_invalidZona_throwsIllegalArgumentException() {
        Anden anden = buildAnden(null, null, 1, EstadoAnden.DISPONIBLE);

        assertThrows(IllegalArgumentException.class, () -> andenService.crearAnden(anden));
    }

    @Test
    void crearAnden_invalidNumero_throwsIllegalArgumentException() {
        Anden anden = buildAnden(null, "A", 0, EstadoAnden.DISPONIBLE);

        assertThrows(IllegalArgumentException.class, () -> andenService.crearAnden(anden));
    }

    @Test
    void crearAnden_duplicateZonaNumero_throwsIllegalStateException() {
        Anden anden = buildAnden(null, "A", 1, EstadoAnden.DISPONIBLE);
        when(andenRepository.existsByZonaAndNumero("A", 1)).thenReturn(true);

        assertThrows(IllegalStateException.class, () -> andenService.crearAnden(anden));
    }

    @Test
    void crearAnden_normalizesZonaAndDefaultsEstado() {
        Anden anden = buildAnden(null, " a ", 2, null);
        when(andenRepository.existsByZonaAndNumero("A", 2)).thenReturn(false);
        when(andenRepository.save(any(Anden.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Anden result = andenService.crearAnden(anden);

        ArgumentCaptor<Anden> andenCaptor = ArgumentCaptor.forClass(Anden.class);
        verify(andenRepository).save(andenCaptor.capture());
        Anden saved = andenCaptor.getValue();
        assertEquals("A", saved.getZona());
        assertEquals(EstadoAnden.DISPONIBLE, saved.getEstado());
        assertEquals(saved, result);
    }

    @Test
    void eliminarAnden_notFound_throwsNotFoundException() {
        when(andenRepository.existsById(20L)).thenReturn(false);

        assertThrows(NotFoundException.class, () -> andenService.eliminarAnden(20L));
    }

    @Test
    void eliminarAnden_ocupado_throwsRuntimeException() {
        Anden anden = buildAnden(21L, "A", 21, EstadoAnden.OCUPADO);
        when(andenRepository.existsById(21L)).thenReturn(true);
        when(andenRepository.findById(21L)).thenReturn(Optional.of(anden));

        assertThrows(RuntimeException.class, () -> andenService.eliminarAnden(21L));
    }

    @Test
    void eliminarAnden_success_deletes() {
        Anden anden = buildAnden(22L, "A", 22, EstadoAnden.DISPONIBLE);
        when(andenRepository.existsById(22L)).thenReturn(true);
        when(andenRepository.findById(22L)).thenReturn(Optional.of(anden));

        andenService.eliminarAnden(22L);

        verify(andenRepository).deleteById(22L);
    }

    private Anden buildAnden(Long id, String zona, int numero, EstadoAnden estado) {
        Anden anden = new Anden();
        anden.setId(id);
        anden.setZona(zona);
        anden.setNumero(numero);
        anden.setEstado(estado);
        anden.setTipo(TipoAnden.CARGA);
        if (zona != null) {
            anden.setCodigo(zona.trim().toUpperCase() + numero);
        }
        return anden;
    }

    private Asignacion buildAsignacion(Long andenId) {
        Asignacion asignacion = new Asignacion();
        asignacion.setId(100L);
        asignacion.setAndenId(andenId);
        asignacion.setPatenteTransporte("AA11");
        asignacion.setContenedorId(12L);
        asignacion.setHoraInicio(LocalDateTime.now().minusMinutes(10));
        return asignacion;
    }
}
