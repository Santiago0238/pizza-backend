package com.taller.proye01.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.taller.proye01.model.Alerta;
import com.taller.proye01.servicio.AlertaService;

@RestController
@RequestMapping("/api/alertas")
@CrossOrigin(origins = "*")
public class AlertaController {

	private final AlertaService alertaService;

	public AlertaController(AlertaService alertaService) {
		this.alertaService = alertaService;
	}


	@GetMapping
	public ResponseEntity<List<Alerta>> listar() {

		return ResponseEntity.ok(alertaService.listar());
	}

	
	@GetMapping("/{id}")
	public ResponseEntity<Alerta> obtenerPorId(@PathVariable Integer id) {

		return ResponseEntity.ok(alertaService.obtenerPorId(id));
	}


	@GetMapping("/activas")
	public ResponseEntity<List<Alerta>> listarActivas() {

		return ResponseEntity.ok(alertaService.listarActivas());
	}


	@GetMapping("/historial")
	public ResponseEntity<List<Alerta>> listarHistorial() {

		return ResponseEntity.ok(alertaService.listarHistorial());
	}


	@PostMapping({ "", "/guardar" })
	public ResponseEntity<?> guardar(@RequestBody Alerta alerta) {
	    Alerta guardada = alertaService.guardar(alerta);
	    
	    Map<String, Object> response = new HashMap<>();
	    response.put("id", guardada.getId());
	    response.put("estado", guardada.getEstado());
	    response.put("mensaje", "Alerta guardada exitosamente");
	    
	    return ResponseEntity.ok(response);
	}

	
	@PutMapping("/{id}")
	public ResponseEntity<Alerta> actualizar(@PathVariable Integer id, @RequestBody Alerta alerta) {

		return ResponseEntity.ok(alertaService.actualizar(id, alerta));
	}

	
	@PutMapping("/{id}/estado")
	public ResponseEntity<Alerta> cambiarEstado(@PathVariable Integer id, @RequestBody Map<String, Object> payload) {

		Object estadoObject = payload.get("estado");

		if (estadoObject == null) {
			throw new RuntimeException("El campo estado es obligatorio");
		}

		String estado = estadoObject.toString();

		return ResponseEntity.ok(alertaService.cambiarEstado(id, estado));
	}


	@PutMapping("/{id}/cerrar")
	public ResponseEntity<Alerta> cerrar(@PathVariable Integer id,
			@RequestBody(required = false) Map<String, Object> payload) {

		return ResponseEntity.ok(alertaService.cerrar(id));
	}

	@DeleteMapping("/{id}")
	public ResponseEntity<String> eliminar(@PathVariable Integer id) {

		alertaService.eliminar(id);

		return ResponseEntity.ok("Alerta eliminada correctamente");
	}

	
	@GetMapping("/buscar/severidad")
	public ResponseEntity<List<Alerta>> buscarPorSeveridad(@RequestParam String severidad) {

		return ResponseEntity.ok(alertaService.buscarPorSeveridad(severidad));
	}

	
	@GetMapping("/buscar/estado")
	public ResponseEntity<List<Alerta>> buscarPorEstado(@RequestParam String estado) {

		return ResponseEntity.ok(alertaService.buscarPorEstado(estado));
	}

	@GetMapping("/buscar")
	public ResponseEntity<List<Alerta>> buscarPorTexto(@RequestParam(required = false) String texto) {

		return ResponseEntity.ok(alertaService.buscarPorTexto(texto));
	}
}