package com.taller.proye01.controllerPizza;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import com.taller.proye01.modelPizza.DireccionEntregaModel;
import com.taller.proye01.serviciosPizza.DireccionEntregaService;

@RestController
@RequestMapping("/direcciones")
@CrossOrigin(origins = "http://localhost:4200")
public class DireccionEntregaController {

    @Autowired
    private DireccionEntregaService direccionService;

    @GetMapping()
    public List<DireccionEntregaModel> listar() {
        return direccionService.listar();
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> buscar(@PathVariable Integer id) {
        DireccionEntregaModel dir = direccionService.buscarPorId(id);
        if (dir == null) {
            return ResponseEntity.status(404)
                    .body(Map.of("error", "Dirección no encontrada"));
        }
        return ResponseEntity.ok(dir);
    }

    @GetMapping("/cliente/{idCliente}")
    public List<DireccionEntregaModel> listarPorCliente(@PathVariable Integer idCliente) {
        return direccionService.listarPorCliente(idCliente);
    }

    @PostMapping("/create")
    public ResponseEntity<?> crear(@RequestBody DireccionEntregaModel direccion) {
        try {
            DireccionEntregaModel nueva = direccionService.guardar(direccion);
            return ResponseEntity.ok(nueva);
        } catch (Exception e) {
            return ResponseEntity.status(500)
                    .body(Map.of("error", "Error al crear dirección: " + e.getMessage()));
        }
    }

    @PutMapping("/modificar/{id}")
    public ResponseEntity<?> modificar(@PathVariable Integer id,
                                       @RequestBody DireccionEntregaModel direccion) {

        DireccionEntregaModel existente = direccionService.buscarPorId(id);
        if (existente == null) {
            return ResponseEntity.status(404).body("Dirección no encontrada");
        }

        existente.setCalle(direccion.getCalle());
        existente.setCiudad(direccion.getCiudad());
        existente.setZona(direccion.getZona());
        existente.setNumero(direccion.getNumero());
        existente.setReferencia(direccion.getReferencia());
        existente.setUbicacionGps(direccion.getUbicacionGps());

        DireccionEntregaModel actualizado = direccionService.guardar(existente);
        return ResponseEntity.ok(actualizado);
    }

    @DeleteMapping("/eliminar/{id}")
    public ResponseEntity<?> eliminar(@PathVariable Integer id) {
        if (!direccionService.eliminar(id)) {
            return ResponseEntity.status(404).body(Map.of("error", "Dirección no encontrada"));
        }
        return ResponseEntity.ok(Map.of("mensaje", "Dirección eliminada"));
    }
}