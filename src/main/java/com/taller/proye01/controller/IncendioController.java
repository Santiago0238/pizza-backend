package com.taller.proye01.controller;


import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.*;
import com.taller.proye01.servicio.IncendioService;

import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;

@RestController
@RequestMapping("/api/incendios")
@CrossOrigin(origins = "*") 
public class IncendioController {
	 	
	 private final IncendioService incendioService;

	    public IncendioController(IncendioService incendioService) {
	        this.incendioService = incendioService;
	    }

	    @PostMapping("/desde-foco/{idFoco}")
	    public ResponseEntity<Map<String, Object>> registrarIncendioDesdeFoco(
	            @PathVariable Integer idFoco,
	            @RequestBody Map<String, Object> payload) {

	        return ResponseEntity.ok(
	                incendioService.registrarIncendioDesdeFoco(idFoco, payload)
	        );
	    }
	    
	    
	    @GetMapping
	    public ResponseEntity<List<Map<String, Object>>> listarIncendios() {
	        return ResponseEntity.ok(incendioService.listarIncendios());
	    }


	    @GetMapping("/{id}")
	    public ResponseEntity<Map<String, Object>> obtenerPorId(@PathVariable Integer id) {
	        return ResponseEntity.ok(incendioService.obtenerPorId(id));
	    }

	    @GetMapping("/activos")
	    public ResponseEntity<List<Map<String, Object>>> listarIncendiosActivos() {
	        return ResponseEntity.ok(incendioService.listarIncendiosActivos());
	    }


	    @GetMapping("/historial")
	    public ResponseEntity<List<Map<String, Object>>> listarHistorialIncendios() {
	        return ResponseEntity.ok(incendioService.listarHistorialIncendios());
	    }

	    /**
	     * CU: Registrar incendio
	     * POST /api/incendios
	     */
	    @PostMapping
	    public ResponseEntity<Map<String, Object>> registrarIncendio(
	            @RequestBody Map<String, Object> payload) {

	        return ResponseEntity.ok(incendioService.registrarIncendio(payload));
	    }

	    @PutMapping("/{id}")
	    public ResponseEntity<Map<String, Object>> actualizarIncendio(
	            @PathVariable Integer id,
	            @RequestBody Map<String, Object> payload) {

	        return ResponseEntity.ok(incendioService.actualizarIncendio(id, payload));
	    }


	    @PutMapping("/{id}/estado")
	    public ResponseEntity<Map<String, Object>> actualizarEstado(
	            @PathVariable Integer id,
	            @RequestBody Map<String, Object> payload) {

	        return ResponseEntity.ok(incendioService.actualizarEstado(id, payload));
	    }

	    @PutMapping("/{id}/metodo-control")
	    public ResponseEntity<Map<String, Object>> registrarMetodoControl(
	            @PathVariable Integer id,
	            @RequestBody Map<String, Object> payload) {

	        return ResponseEntity.ok(incendioService.registrarMetodoControl(id, payload));
	    }

	
	    @PutMapping("/{id}/superficie-afectada")
	    public ResponseEntity<Map<String, Object>> registrarSuperficieAfectada(
	            @PathVariable Integer id,
	            @RequestBody Map<String, Object> payload) {

	        return ResponseEntity.ok(incendioService.registrarSuperficieAfectada(id, payload));
	    }

	    @GetMapping("/buscar/estado")
	    public ResponseEntity<List<Map<String, Object>>> buscarPorEstado(
	            @RequestParam String estado) {

	        return ResponseEntity.ok(incendioService.buscarPorEstado(estado));
	    }

	    @GetMapping("/buscar/gravedad")
	    public ResponseEntity<List<Map<String, Object>>> buscarPorNivelGravedad(
	            @RequestParam String nivelGravedad) {

	        return ResponseEntity.ok(incendioService.buscarPorNivelGravedad(nivelGravedad));
	    }

	   
	    @GetMapping("/buscar")
	    public ResponseEntity<List<Map<String, Object>>> buscarPorTexto(
	            @RequestParam(required = false) String texto) {

	        return ResponseEntity.ok(incendioService.buscarPorTexto(texto));
	    }

	    @DeleteMapping("/{id}")
	    public ResponseEntity<Map<String, Object>> eliminarIncendio(@PathVariable Integer id) {
	        return ResponseEntity.ok(incendioService.eliminarIncendio(id));
	    }
	    
}