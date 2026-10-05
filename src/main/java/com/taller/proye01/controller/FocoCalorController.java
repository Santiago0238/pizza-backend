package com.taller.proye01.controller;


import java.time.LocalDate;
import java.util.List;
import java.util.Map;

import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.taller.proye01.servicio.FocoCalorService;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.PastOrPresent;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/focos-calor")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class FocoCalorController {

    private final FocoCalorService focoCalorService;


    @GetMapping
    public ResponseEntity<List<Map<String, Object>>> listarFocos() {
        return ResponseEntity.ok(focoCalorService.listarFocos());
    }

  
    @GetMapping("/{id}")
    public ResponseEntity<Map<String, Object>> obtenerPorId(@PathVariable Integer id) {
        return ResponseEntity.ok(focoCalorService.obtenerPorId(id));
    }

    @GetMapping("/nasa/sama")
    public ResponseEntity<List<Map<String, Object>>> consultarFocosNasaSama() {
        return ResponseEntity.ok(focoCalorService.consultarFocosNasaSama());
    }

    @PostMapping("/nasa/sama/sincronizar")
    public ResponseEntity<Map<String, Object>> sincronizarFocosNasaSama() {
        return ResponseEntity.ok(focoCalorService.sincronizarFocosNasaSama());
    }

   
    @GetMapping("/sin-incendio")
    public ResponseEntity<List<Map<String, Object>>> listarFocosSinIncendio() {
        return ResponseEntity.ok(focoCalorService.listarFocosSinIncendio());
    }

   
    @GetMapping("/incendio/{idIncendio}")
    public ResponseEntity<List<Map<String, Object>>> listarFocosPorIncendio(
            @PathVariable Integer idIncendio) {

        return ResponseEntity.ok(focoCalorService.listarFocosPorIncendio(idIncendio));
    }

   
    @PutMapping("/{idFoco}/asociar-incendio/{idIncendio}")
    public ResponseEntity<Map<String, Object>> asociarFocoAIncendio(
            @PathVariable Integer idFoco,
            @PathVariable Integer idIncendio) {

        return ResponseEntity.ok(
                focoCalorService.asociarFocoAIncendio(idFoco, idIncendio)
        );
    }

    
    @PutMapping("/{idFoco}/quitar-incendio")
    public ResponseEntity<Map<String, Object>> quitarFocoDeIncendio(
            @PathVariable Integer idFoco) {

        return ResponseEntity.ok(focoCalorService.quitarFocoDeIncendio(idFoco));
    }

   
    @GetMapping("/buscar/confianza")
    public ResponseEntity<List<Map<String, Object>>> buscarPorConfianza(
            @RequestParam String confianza) {

        return ResponseEntity.ok(focoCalorService.buscarPorConfianza(confianza));
    }

   
    @GetMapping("/buscar/satelite")
    public ResponseEntity<List<Map<String, Object>>> buscarPorSatelite(
            @RequestParam String satelite) {

        return ResponseEntity.ok(focoCalorService.buscarPorSatelite(satelite));
    }

   
    @GetMapping("/filtrar-area")
    public ResponseEntity<List<Map<String, Object>>> buscarDentroDeArea(
            @RequestParam String wktArea) {

        return ResponseEntity.ok(focoCalorService.buscarDentroDeArea(wktArea));
    }
    @GetMapping("/nasa/sama/producto")
    public ResponseEntity<List<Map<String, Object>>> consultarFocosNasaSamaPorProducto(
            @RequestParam String producto,
            @RequestParam(required = false, defaultValue = "2") Integer dias) {

        return ResponseEntity.ok(
                focoCalorService.consultarFocosNasaSamaPorProducto(producto, dias)
        );
    }

    @PostMapping("/nasa/sama/producto/sincronizar")
    public ResponseEntity<Map<String, Object>> sincronizarFocosNasaSamaPorProducto(
            @RequestParam String producto,
            @RequestParam(required = false, defaultValue = "2") Integer dias) {

        return ResponseEntity.ok(
                focoCalorService.sincronizarFocosNasaSamaPorProducto(producto, dias)
        );
    }
    

    
    @GetMapping("/cercanos")
    public ResponseEntity<List<Map<String, Object>>> buscarCercanos(
            @RequestParam Double lat,
            @RequestParam Double lon,
            @RequestParam Double radioMetros) {

        return ResponseEntity.ok(
                focoCalorService.buscarCercanos(lat, lon, radioMetros)
        );
    }
    
    @GetMapping("/nasa/sama/fecha")
    public ResponseEntity<List<Map<String, Object>>> consultarFocosPorFecha(
            @RequestParam(required = false, defaultValue = "TODOS") String producto,
            @RequestParam(required = false, defaultValue = "1") @Min(value = 1, message = "Los días deben ser al menos 1") @Max(value = 10, message = "Los días no pueden exceder 10") Integer dias,
            @RequestParam @NotNull(message = "La fecha es obligatoria") 
            @PastOrPresent(message = "La fecha no puede ser futura") 
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha) {

        return ResponseEntity.ok(
                focoCalorService.consultarFocosNasaPorFecha(producto, dias, fecha)
        );
    }

    @PostMapping("/nasa/sama/fecha/sincronizar")
    public ResponseEntity<Map<String, Object>> sincronizarFocosPorFecha(
            @RequestParam(required = false, defaultValue = "TODOS") String producto,
            @RequestParam(required = false, defaultValue = "1") @Min(value = 1, message = "Los días deben ser al menos 1") @Max(value = 10, message = "Los días no pueden exceder 10") Integer dias,
            @RequestParam @NotNull(message = "La fecha es obligatoria") 
            @PastOrPresent(message = "La fecha no puede ser futura") 
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha) {

        return ResponseEntity.ok(
                focoCalorService.sincronizarFocosNasaPorFecha(producto, dias, fecha)
        );
    }
    

    @GetMapping("/geojson")
    public ResponseEntity<Map<String, Object>> listarGeoJSON() {
        return ResponseEntity.ok(focoCalorService.listarGeoJSON());
    }
}