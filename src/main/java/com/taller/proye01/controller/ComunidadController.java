package com.taller.proye01.controller;


import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.taller.proye01.servicio.ComunidadService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/comunidad")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class ComunidadController {

    private final ComunidadService comunidadService;

    @GetMapping
    public ResponseEntity<List<Map<String, Object>>> listar() {
        return ResponseEntity.ok(comunidadService.listar());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> obtenerPorId(@PathVariable Integer id) {
        return ResponseEntity.ok(comunidadService.obtenerPorId(id));
    }

    @PostMapping
    public ResponseEntity<Map<String, Object>> registrar(@RequestBody Map<String, Object> payload) {
        return ResponseEntity.ok(comunidadService.registrar(payload));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Map<String, Object>> actualizar(
            @PathVariable Integer id,
            @RequestBody Map<String, Object> payload) {

        return ResponseEntity.ok(comunidadService.actualizar(id, payload));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> eliminar(@PathVariable Integer id) {
        return ResponseEntity.ok(comunidadService.eliminar(id));
    }

    @GetMapping("/geojson")
    public ResponseEntity<Map<String, Object>> listarGeoJSON() {
        return ResponseEntity.ok(comunidadService.listarGeoJSON());
    }

    @GetMapping("/filtrar")
    public ResponseEntity<List<Map<String, Object>>> filtrarPorNivelRiesgo(
            @RequestParam String nivelRiesgo) {

        return ResponseEntity.ok(comunidadService.filtrarPorNivelRiesgo(nivelRiesgo));
    }
}