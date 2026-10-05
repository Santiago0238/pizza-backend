package com.taller.proye01.controller;

import java.util.List;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.taller.proye01.model.ZonaRiesgo;

import com.taller.proye01.servicio.ZonaRiesgoService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/zonas-riesgo")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class ZonaRiesgoController {

	private final ZonaRiesgoService zonaRiesgoService;

	@GetMapping
	public ResponseEntity<List<ZonaRiesgo>> listar() {
		return ResponseEntity.ok(zonaRiesgoService.listar());
	}

	@GetMapping("/{id}")
	public ResponseEntity<ZonaRiesgo> obtenerPorId(@PathVariable Integer id) {

		return ResponseEntity.ok(zonaRiesgoService.obtenerPorId(id));
	}

	@PostMapping
	public ResponseEntity<ZonaRiesgo> guardar(@RequestBody ZonaRiesgo zona) {

		return ResponseEntity.ok(zonaRiesgoService.guardar(zona));
	}


	@PutMapping("/{id}")
	public ResponseEntity<ZonaRiesgo> actualizar(@PathVariable Integer id, @RequestBody ZonaRiesgo zona) {

		return ResponseEntity.ok(zonaRiesgoService.actualizar(id, zona));
	}

	
	@DeleteMapping("/{id}")
	public ResponseEntity<String> eliminar(@PathVariable Integer id) {

		zonaRiesgoService.eliminar(id);

		return ResponseEntity.ok("Zona de riesgo eliminada correctamente");
	}


	@GetMapping("/buscar/nivel")
	public ResponseEntity<List<ZonaRiesgo>> buscarPorNivel(@RequestParam String nivelRiesgo) {

		return ResponseEntity.ok(zonaRiesgoService.buscarPorNivel(nivelRiesgo));
	}


	@GetMapping("/buscar")
	public ResponseEntity<List<ZonaRiesgo>> buscarPorTexto(@RequestParam(required = false) String texto) {

		return ResponseEntity.ok(zonaRiesgoService.buscarPorTexto(texto));
	}
	
	@PostMapping(value = "/upload/kml", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ZonaRiesgo> subirKml(
            @RequestParam("archivo") MultipartFile archivo,
            @RequestParam("nombre") String nombre,
            @RequestParam("nivelRiesgo") String nivelRiesgo,
            @RequestParam(value = "descripcion", required = false) String descripcion,
            @RequestParam(value = "fuenteDatos", required = false) String fuenteDatos,
            @RequestParam(value = "idTipoVegetacion", required = false) Integer idTipoVegetacion) {

        ZonaRiesgo guardada = zonaRiesgoService.guardarDesdeKml(
                archivo,
                nombre,
                nivelRiesgo,
                descripcion,
                fuenteDatos,
                idTipoVegetacion
        );

        return ResponseEntity.ok(guardada);
    }
	
	@PutMapping(value = "/{id}/kml", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
	public ResponseEntity<ZonaRiesgo> actualizarConKml(
	        @PathVariable Integer id,
	        @RequestParam(value = "archivo", required = false) MultipartFile archivo,
	        @RequestParam("nombre") String nombre,
	        @RequestParam("nivelRiesgo") String nivelRiesgo,
	        @RequestParam(value = "descripcion", required = false) String descripcion,
	        @RequestParam(value = "fuenteDatos", required = false) String fuenteDatos,
	        @RequestParam(value = "idTipoVegetacion", required = false) Integer idTipoVegetacion,
	        @RequestParam(value = "wkt", required = false) String wkt) {

	    ZonaRiesgo actualizada;
	    if (archivo != null && !archivo.isEmpty()) {
	        
	        actualizada = zonaRiesgoService.actualizarDesdeKml(
	                id, archivo, nombre, nivelRiesgo, descripcion, fuenteDatos, idTipoVegetacion
	        );
	    } else if (wkt != null && !wkt.isBlank()) {
	        
	        actualizada = zonaRiesgoService.actualizarConGeometriaWkt(
	                id, wkt, nombre, nivelRiesgo, descripcion, fuenteDatos, idTipoVegetacion
	        );
	    } else {
	    
	        ZonaRiesgo zonaDatos = new ZonaRiesgo();
	        zonaDatos.setNombre(nombre);
	        zonaDatos.setNivelRiesgo(nivelRiesgo);
	        zonaDatos.setDescripcion(descripcion);
	        zonaDatos.setFuenteDatos(fuenteDatos);
	        actualizada = zonaRiesgoService.actualizar(id, zonaDatos);
	    }

	    return ResponseEntity.ok(actualizada);
	}
	
	
}