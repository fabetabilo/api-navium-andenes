package com.navium.andenes.controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.navium.andenes.model.Anden;
import com.navium.andenes.model.Asignacion;
import com.navium.andenes.model.EstadoAnden;
import com.navium.andenes.model.TipoAnden;
import com.navium.andenes.service.AndenService;

@ExtendWith(MockitoExtension.class)
class AndenControllerTest {

	private final ObjectMapper objectMapper = new ObjectMapper();

	private MockMvc mockMvc;

	@Mock
	private AndenService andenService;

	@InjectMocks
	private AndenController andenController;

	@BeforeEach
	void setUp() {
		mockMvc = MockMvcBuilders.standaloneSetup(andenController).build();
	}

	@Test
	void obtenerPorId_returnsAnden() throws Exception {
		Anden anden = buildAnden(1L, "A", 1, EstadoAnden.DISPONIBLE);
		when(andenService.obtenerAndenPorId(1L)).thenReturn(anden);

		mockMvc.perform(get("/api/v0/andenes/1"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.id").value(1L))
			.andExpect(jsonPath("$.zona").value("A"))
			.andExpect(jsonPath("$.numero").value(1));
	}

	@Test
	void obtenerTodos_returnsNoContentWhenEmpty() throws Exception {
		when(andenService.obtenerAndenes()).thenReturn(List.of());

		mockMvc.perform(get("/api/v0/andenes"))
			.andExpect(status().isNoContent())
			.andExpect(content().string(""));
	}

	@Test
	void obtenerTodos_returnsList() throws Exception {
		List<Anden> andenes = List.of(buildAnden(1L, "A", 1, EstadoAnden.DISPONIBLE));
		when(andenService.obtenerAndenes()).thenReturn(andenes);

		mockMvc.perform(get("/api/v0/andenes"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$[0].id").value(1L));
	}

	@Test
	void obtenerPorZona_returnsNoContentWhenEmpty() throws Exception {
		when(andenService.obtenerPorZona("A")).thenReturn(List.of());

		mockMvc.perform(get("/api/v0/andenes/zona/A"))
			.andExpect(status().isNoContent())
			.andExpect(content().string(""));
	}

	@Test
	void obtenerPorZona_returnsList() throws Exception {
		List<Anden> andenes = List.of(buildAnden(2L, "A", 2, EstadoAnden.DISPONIBLE));
		when(andenService.obtenerPorZona("A")).thenReturn(andenes);

		mockMvc.perform(get("/api/v0/andenes/zona/A"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$[0].id").value(2L));
	}

	@Test
	void obtenerPorCodigo_returnsAnden() throws Exception {
		Anden anden = buildAnden(3L, "B", 3, EstadoAnden.DISPONIBLE);
		when(andenService.obtenerPorCodigo("B3")).thenReturn(anden);

		mockMvc.perform(get("/api/v0/andenes/codigo/B3"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.id").value(3L));
	}

	@Test
	void obtenerDisponibles_returnsNoContentWhenEmpty() throws Exception {
		when(andenService.obtenerAndenesDisponibles()).thenReturn(List.of());

		mockMvc.perform(get("/api/v0/andenes/disponibles"))
			.andExpect(status().isNoContent())
			.andExpect(content().string(""));
	}

	@Test
	void obtenerDisponibles_returnsList() throws Exception {
		List<Anden> andenes = List.of(buildAnden(4L, "C", 4, EstadoAnden.DISPONIBLE));
		when(andenService.obtenerAndenesDisponibles()).thenReturn(andenes);

		mockMvc.perform(get("/api/v0/andenes/disponibles"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$[0].id").value(4L));
	}

	@Test
	void obtenerOcupados_returnsNoContentWhenEmpty() throws Exception {
		when(andenService.obtenerAndenesOcupados()).thenReturn(List.of());

		mockMvc.perform(get("/api/v0/andenes/ocupados"))
			.andExpect(status().isNoContent())
			.andExpect(content().string(""));
	}

	@Test
	void obtenerOcupados_returnsList() throws Exception {
		List<Anden> andenes = List.of(buildAnden(5L, "C", 5, EstadoAnden.OCUPADO));
		when(andenService.obtenerAndenesOcupados()).thenReturn(andenes);

		mockMvc.perform(get("/api/v0/andenes/ocupados"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$[0].id").value(5L));
	}

	@Test
	void obtenerEnMantenimiento_returnsNoContentWhenEmpty() throws Exception {
		when(andenService.obtenerAndenesMantenimiento()).thenReturn(List.of());

		mockMvc.perform(get("/api/v0/andenes/mantenimiento"))
			.andExpect(status().isNoContent())
			.andExpect(content().string(""));
	}

	@Test
	void obtenerEnMantenimiento_returnsList() throws Exception {
		List<Anden> andenes = List.of(buildAnden(6L, "D", 6, EstadoAnden.MANTENIMIENTO));
		when(andenService.obtenerAndenesMantenimiento()).thenReturn(andenes);

		mockMvc.perform(get("/api/v0/andenes/mantenimiento"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$[0].id").value(6L));
	}

	@Test
	void crearAnden_returnsCreated() throws Exception {
		Anden request = buildAnden(null, "A", 7, EstadoAnden.DISPONIBLE);
		Anden created = buildAnden(7L, "A", 7, EstadoAnden.DISPONIBLE);
        when(andenService.crearAnden(any(Anden.class))).thenReturn(created);
        
		mockMvc.perform(post("/api/v0/andenes")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(request)))
			.andExpect(status().isCreated())
			.andExpect(jsonPath("$.id").value(7L));
	}

	@Test
	void eliminarAnden_returnsNoContent() throws Exception {
		mockMvc.perform(delete("/api/v0/andenes/8"))
			.andExpect(status().isNoContent());

		verify(andenService).eliminarAnden(8L);
	}

	@Test
	void asignarAnden_returnsAsignacion() throws Exception {
		Asignacion asignacion = new Asignacion();
		asignacion.setId(10L);
		asignacion.setAndenId(9L);
		asignacion.setPatenteTransporte("AA11");
		asignacion.setContenedorId(100L);
		when(andenService.asignarAnden(9L, "AA11", 100L)).thenReturn(asignacion);

		mockMvc.perform(post("/api/v0/andenes/9/asignar")
				.param("patente", "AA11")
				.param("contenedorId", "100"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.id").value(10L))
			.andExpect(jsonPath("$.andenId").value(9L));
	}

	@Test
	void marcarAndenEnMantenimiento_returnsAnden() throws Exception {
		Anden anden = buildAnden(11L, "A", 11, EstadoAnden.MANTENIMIENTO);
		when(andenService.marcarAndenEnMantenimiento(11L)).thenReturn(anden);

		mockMvc.perform(post("/api/v0/andenes/11/mantenimiento"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.id").value(11L))
			.andExpect(jsonPath("$.estado").value("MANTENIMIENTO"));
	}

	@Test
	void habilitarAnden_returnsAnden() throws Exception {
		Anden anden = buildAnden(12L, "A", 12, EstadoAnden.DISPONIBLE);
		when(andenService.habilitarAnden(12L)).thenReturn(anden);

		mockMvc.perform(post("/api/v0/andenes/12/habilitar"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.id").value(12L))
			.andExpect(jsonPath("$.estado").value("DISPONIBLE"));
	}

	@Test
	void liberarAnden_returnsNoContent() throws Exception {
		mockMvc.perform(post("/api/v0/andenes/13/liberar"))
			.andExpect(status().isNoContent());

		verify(andenService).liberarAnden(13L);
	}

	private Anden buildAnden(Long id, String zona, int numero, EstadoAnden estado) {
		Anden anden = new Anden();
		anden.setId(id);
		anden.setZona(zona);
		anden.setNumero(numero);
		anden.setEstado(estado);
		anden.setTipo(TipoAnden.CARGA);
		if (zona != null) {
			anden.setCodigo(zona + numero);
		}
		return anden;
	}
}
