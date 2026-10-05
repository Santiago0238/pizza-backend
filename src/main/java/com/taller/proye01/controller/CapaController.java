package com.taller.proye01.controller;

import java.util.List;
import java.util.Map;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import com.taller.proye01.servicio.CapaFeatureService;
import com.taller.proye01.servicio.CapaService;
import org.springframework.http.MediaType;
@RestController
@RequestMapping("/api/capas")
@CrossOrigin(origins = "*")
public class CapaController {

    private final CapaService capaService;
    private final CapaFeatureService capaFeatureService;

    public CapaController(CapaService capaService, CapaFeatureService capaFeatureService) {
        this.capaService = capaService;
        this.capaFeatureService = capaFeatureService;
    }

    @GetMapping
    public ResponseEntity<List<Map<String, Object>>> listarCapas() {
        return ResponseEntity.ok(capaService.listarCapas());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> obtenerPorId(@PathVariable Integer id) {
        return ResponseEntity.ok(capaService.obtenerPorId(id));
    }

    @PostMapping
    public ResponseEntity<Map<String, Object>> registrarCapa(@RequestBody Map<String, Object> payload) {
        return ResponseEntity.status(HttpStatus.CREATED).body(capaService.registrarCapa(payload));
    }

   
    @PutMapping("/{id}")
    public ResponseEntity<Map<String, Object>> actualizarCapa(
            @PathVariable Integer id,
            @RequestBody Map<String, Object> payload) {
        return ResponseEntity.ok(capaService.actualizarCapa(id, payload));
    }

   
    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> eliminarCapa(@PathVariable Integer id) {
        return ResponseEntity.ok(capaService.eliminarCapa(id));
    }

    
    @GetMapping("/publicas")
    public ResponseEntity<List<Map<String, Object>>> listarCapasPublicas() {
        return ResponseEntity.ok(capaService.listarCapasPublicas());
    }

    
    @GetMapping("/tipo/{tipo}")
    public ResponseEntity<List<Map<String, Object>>> listarCapasPorTipo(@PathVariable String tipo) {
        return ResponseEntity.ok(capaService.listarCapasPorTipo(tipo));
    }

    
    @GetMapping("/usuario/{idUsuario}")
    public ResponseEntity<List<Map<String, Object>>> listarCapasPorUsuario(@PathVariable Integer idUsuario) {
        return ResponseEntity.ok(capaService.listarCapasPorUsuario(idUsuario));
    }

    
    @GetMapping("/buscar")
    public ResponseEntity<List<Map<String, Object>>> buscarPorNombre(@RequestParam(required = false) String nombre) {
        return ResponseEntity.ok(capaService.buscarPorNombre(nombre));
    }

   
    @GetMapping("/geojson")
    public ResponseEntity<Map<String, Object>> listarTodasComoGeoJSON() {
        return ResponseEntity.ok(capaService.listarTodasComoGeoJSON());
    }

    
    @GetMapping("/{idCapa}/geojson")
    public ResponseEntity<Map<String, Object>> obtenerCapaComoGeoJSON(@PathVariable Integer idCapa) {
        return ResponseEntity.ok(capaService.obtenerCapaComoGeoJSON(idCapa));
    }

   
    @PostMapping("/importar-geojson")
    public ResponseEntity<Map<String, Object>> importarGeoJSON(@RequestBody Map<String, Object> payload) {
        return ResponseEntity.status(HttpStatus.CREATED).body(capaService.importarGeoJSON(payload));
    }

    @GetMapping("/{idCapa}/features")
    public ResponseEntity<List<Map<String, Object>>> listarFeaturesPorCapa(@PathVariable Integer idCapa) {
        return ResponseEntity.ok(capaFeatureService.listarFeaturesPorCapa(idCapa));
    }

    @GetMapping("/features/{idFeature}")
    public ResponseEntity<Map<String, Object>> obtenerFeaturePorId(@PathVariable Integer idFeature) {
        return ResponseEntity.ok(capaFeatureService.obtenerFeaturePorId(idFeature));
    }

    @PostMapping("/{idCapa}/features")
    public ResponseEntity<Map<String, Object>> registrarFeature(
            @PathVariable Integer idCapa,
            @RequestBody Map<String, Object> payload) {
        return ResponseEntity.status(HttpStatus.CREATED).body(capaFeatureService.registrarFeature(idCapa, payload));
    }

    @PutMapping("/features/{idFeature}")
    public ResponseEntity<Map<String, Object>> actualizarFeature(
            @PathVariable Integer idFeature,
            @RequestBody Map<String, Object> payload) {
        return ResponseEntity.ok(capaFeatureService.actualizarFeature(idFeature, payload));
    }

    @DeleteMapping("/features/{idFeature}")
    public ResponseEntity<Map<String, Object>> eliminarFeature(@PathVariable Integer idFeature) {
        return ResponseEntity.ok(capaFeatureService.eliminarFeature(idFeature));
    }

    @GetMapping("/features/tipo-vegetacion/{idTipoVegetacion}")
    public ResponseEntity<List<Map<String, Object>>> listarFeaturesPorTipoVegetacion(
            @PathVariable Integer idTipoVegetacion) {
        return ResponseEntity.ok(capaFeatureService.listarFeaturesPorTipoVegetacion(idTipoVegetacion));
    }

   
    @GetMapping("/features/intersectar")
    public ResponseEntity<List<Map<String, Object>>> listarFeaturesIntersectados(@RequestParam String wktArea) {
        return ResponseEntity.ok(capaFeatureService.listarFeaturesIntersectados(wktArea));
    }

    @GetMapping("/{idCapa}/features/geojson")
    public ResponseEntity<Map<String, Object>> listarFeaturesComoGeoJSON(@PathVariable Integer idCapa) {
        return ResponseEntity.ok(capaFeatureService.listarFeaturesComoGeoJSON(idCapa));
    }
    
    @PostMapping(value = "/upload/kml", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Map<String, Object>> registrarCapaConKml(
            @RequestParam("archivo") MultipartFile archivo,
            @RequestParam("nombre") String nombre,
            @RequestParam(value = "tipo", defaultValue = "GENERAL") String tipo,
            @RequestParam(value = "publico", defaultValue = "false") Boolean publico,
            @RequestParam("idUsuario") Integer idUsuario,
            @RequestParam(value = "idTipoVegetacion", required = false) Integer idTipoVegetacion,
            @RequestParam(value = "descripcion", required = false) String descripcion) {

        Map<String, Object> resultado = capaService.registrarCapaConKml(
                archivo,
                nombre,
                tipo,
                publico,
                idUsuario,
                idTipoVegetacion,
                descripcion
        );

        return ResponseEntity.status(HttpStatus.CREATED).body(resultado);
    }
    
}