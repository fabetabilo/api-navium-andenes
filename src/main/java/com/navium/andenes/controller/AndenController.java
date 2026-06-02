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

import com.navium.andenes.dto.AndenInformacion;
import com.navium.andenes.model.Anden;
import com.navium.andenes.model.Asignacion;
import com.navium.andenes.exception.ErrorResponse;
import com.navium.andenes.service.AndenService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.ArraySchema;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v0/andenes")
@RequiredArgsConstructor
@Tag(name = "Andenes", description = "Operaciones CRUD y de negocio sobre andenes")
public class AndenController {
    
    private final AndenService andenService;
    
    /**
     * Obtiene un Anden por id
     */
    @GetMapping("/{id}")
    @Operation(summary = "Obtener anden por id")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Anden encontrado",
            content = @Content(schema = @Schema(implementation = Anden.class))),
        @ApiResponse(responseCode = "400", description = "Solicitud invalida",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "404", description = "Anden no encontrado",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "500", description = "Error interno",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<Anden> obtenerPorId(@Parameter(description = "Id del anden", example = "1") @PathVariable Long id) {
        Anden anden = this.andenService.obtenerAndenPorId(id);
        return ResponseEntity.ok(anden);
    }

    /**
     * Obtiene un Anden con asignacion activa
     */
    @GetMapping("/{id}/asignacion")
    @Operation(summary = "Obtener anden con asignación actual")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Anden con asignación encontrado",
            content = @Content(schema = @Schema(implementation = AndenInformacion.class))),
        @ApiResponse(responseCode = "400", description = "Solicitud invalida",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "404", description = "Anden no encontrado",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "500", description = "Error interno",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<AndenInformacion> obtenerAndenConAsignacionActual(@Parameter(description = "Id del anden", example = "1") @PathVariable Long id) {
        AndenInformacion informacion = andenService.obtenerAndenConAsignacion(id);
        return ResponseEntity.ok(informacion);
    }

    /**
     * Obtiene todos los andenes ocupados con su asignación activa
     */
    @GetMapping("/asignacion")
    @Operation(summary = "Listar andenes ocupados con asignación")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Listado de andenes ocupados con asignación",
            content = @Content(array = @ArraySchema(schema = @Schema(implementation = AndenInformacion.class)))),
        @ApiResponse(responseCode = "204", description = "Sin contenido")
    })
    public ResponseEntity<List<AndenInformacion>> obtenerOcupadosConAsignacion() {
        List<AndenInformacion> andenes = andenService.obtenerAndenesOcupadosConAsignacion();
        if (andenes.isEmpty()) {
            return ResponseEntity.noContent().build();
        }
        return ResponseEntity.ok(andenes);
    }
    
    /**
     * Obtiene todos los andenes existentes
     */
    @GetMapping
    @Operation(summary = "Listar andenes")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Listado de andenes",
            content = @Content(array = @ArraySchema(schema = @Schema(implementation = Anden.class)))),
        @ApiResponse(responseCode = "204", description = "Sin contenido")
    })
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
    @Operation(summary = "Listar andenes por zona")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Listado de andenes",
            content = @Content(array = @ArraySchema(schema = @Schema(implementation = Anden.class)))),
        @ApiResponse(responseCode = "204", description = "Sin contenido")
    })
    public ResponseEntity<List<Anden>> obtenerPorZona(
        @Parameter(description = "Zona a filtrar", example = "A") @PathVariable String zona) {
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
    @Operation(summary = "Obtener anden por codigo")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Anden encontrado",
            content = @Content(schema = @Schema(implementation = Anden.class))),
        @ApiResponse(responseCode = "404", description = "Anden no encontrado",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<Anden> obtenerPorCodigo(
        @Parameter(description = "Codigo compuesto zona+numero", example = "A12") @PathVariable String codigo) {
        Anden anden = this.andenService.obtenerPorCodigo(codigo);
        return ResponseEntity.ok(anden);
    }
    
    /**
     * Obtiene todos los andenes con estado DISPONIBLE
     */
    @GetMapping("/disponibles")
    @Operation(summary = "Listar andenes disponibles")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Listado de andenes",
            content = @Content(array = @ArraySchema(schema = @Schema(implementation = Anden.class)))),
        @ApiResponse(responseCode = "204", description = "Sin contenido")
    })
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
    @Operation(summary = "Listar andenes ocupados")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Listado de andenes",
            content = @Content(array = @ArraySchema(schema = @Schema(implementation = Anden.class)))),
        @ApiResponse(responseCode = "204", description = "Sin contenido")
    })
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
    @Operation(summary = "Listar andenes en mantenimiento")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Listado de andenes",
            content = @Content(array = @ArraySchema(schema = @Schema(implementation = Anden.class)))),
        @ApiResponse(responseCode = "204", description = "Sin contenido")
    })
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
    @Operation(summary = "Crear un anden")
    @ApiResponses({
        @ApiResponse(responseCode = "201", description = "Anden creado",
            content = @Content(schema = @Schema(implementation = Anden.class))),
        @ApiResponse(responseCode = "400", description = "Solicitud invalida",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "500", description = "Error interno",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<Anden> crearAnden(
        @io.swagger.v3.oas.annotations.parameters.RequestBody(
            description = "Datos del anden a crear",
            required = true,
            content = @Content(schema = @Schema(implementation = Anden.class))
        )
        @RequestBody Anden anden) {
        Anden adn = this.andenService.crearAnden(anden);
        return ResponseEntity.status(201).body(adn);
    }
    
    /**
     * Eliminar un Anden
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar un anden")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Anden eliminado"),
        @ApiResponse(responseCode = "400", description = "Solicitud invalida",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "404", description = "Anden no encontrado",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<Void> eliminarAnden(@Parameter(description = "Id del anden", example = "1") @PathVariable Long id) {
        this.andenService.eliminarAnden(id);
        return ResponseEntity.noContent().build();
    }
    
    
    
    // --- METODOS DE NEGOCIO ---
     
    /**
     * Realiza la asignacion de contenedor y transporte a un Anden
     */
    @PostMapping("/{id}/asignar")
    @Operation(summary = "Asignar anden")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Asignacion creada",
            content = @Content(schema = @Schema(implementation = Asignacion.class))),
        @ApiResponse(responseCode = "400", description = "Solicitud invalida",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "404", description = "Anden no encontrado",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<Asignacion> asignarAnden(
        @Parameter(description = "Id del anden", example = "1") @PathVariable Long id,
        @Parameter(description = "Patente del transporte", example = "ABCD12") @RequestParam String patente,
        @Parameter(description = "Id del contenedor", example = "2001") @RequestParam Long contenedorId) {
        Asignacion asignacion = andenService.asignarAnden(id, patente, contenedorId);
        return ResponseEntity.ok(asignacion);
    }
    
    /**
     * Marca a Anden en estado de mantenimiento
     */
    @PostMapping("/{id}/mantenimiento")
    @Operation(summary = "Marcar anden en mantenimiento")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Anden actualizado",
            content = @Content(schema = @Schema(implementation = Anden.class))),
        @ApiResponse(responseCode = "400", description = "Solicitud invalida",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "404", description = "Anden no encontrado",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<Anden> marcarAndenEnMantenimiento(@Parameter(description = "Id del anden", example = "1") @PathVariable Long id) {
        Anden anden = andenService.marcarAndenEnMantenimiento(id);
        return ResponseEntity.ok(anden);
    }
    
    /**
     * Habilita a un Anden que esta en mantenimiento
     */
    @PostMapping("/{id}/habilitar")
    @Operation(summary = "Habilitar anden en mantenimiento")
    @ApiResponses({
        @ApiResponse(responseCode = "200", description = "Anden actualizado",
            content = @Content(schema = @Schema(implementation = Anden.class))),
        @ApiResponse(responseCode = "400", description = "Solicitud invalida",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "404", description = "Anden no encontrado",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<Anden> habilitarAnden(@Parameter(description = "Id del anden", example = "1") @PathVariable Long id) {
        Anden anden = andenService.habilitarAnden(id);
        return ResponseEntity.ok(anden);
    }
    
    /**
     * Libera un Anden de su asignacion de anden
     */
    @PostMapping("/{id}/liberar")
    @Operation(summary = "Liberar anden")
    @ApiResponses({
        @ApiResponse(responseCode = "204", description = "Anden liberado"),
        @ApiResponse(responseCode = "400", description = "Solicitud invalida",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class))),
        @ApiResponse(responseCode = "404", description = "Anden no encontrado",
            content = @Content(schema = @Schema(implementation = ErrorResponse.class)))
    })
    public ResponseEntity<Void> liberarAnden(@Parameter(description = "Id del anden", example = "1") @PathVariable Long id) {
        andenService.liberarAnden(id);
        return ResponseEntity.noContent().build();
    }
}