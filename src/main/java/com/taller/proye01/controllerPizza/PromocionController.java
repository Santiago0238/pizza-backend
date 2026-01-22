package com.taller.proye01.controllerPizza;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import com.taller.proye01.modelPizza.PromocionModel;
import com.taller.proye01.serviciosPizza.PromocionService;

@RestController
@RequestMapping("/promociones")
@CrossOrigin(origins = "http://localhost:4200")
public class PromocionController {

    @Autowired
    private PromocionService promocionService;

    @GetMapping()
    public List<PromocionModel> listar() {
        return promocionService.listar();
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> buscar(@PathVariable Integer id) {
        PromocionModel p = promocionService.buscarPorId(id);
        if (p == null) {
            return ResponseEntity.status(404).body(Map.of("error", "Promoción no encontrada"));
        }
        return ResponseEntity.ok(p);
    }

    @GetMapping("/estado/{estado}")
    public List<PromocionModel> porEstado(@PathVariable String estado) {
        return promocionService.porEstado(estado);
    }

    @GetMapping("/vigentes")
    public List<PromocionModel> vigentes() {
        return promocionService.vigentes(new Date());
    }

    @GetMapping("/buscar")
    public List<PromocionModel> buscarPorNombre(@RequestParam String nombre) {
        return promocionService.buscarPorNombre(nombre);
    }

    @PostMapping("/create")
    public ResponseEntity<?> crear(@RequestBody PromocionModel promo) {
        try {
            PromocionModel nueva = promocionService.guardar(promo);
            return ResponseEntity.ok(nueva);
        } catch (Exception e) {
            return ResponseEntity.status(500)
                    .body(Map.of("error", "Error al crear promoción: " + e.getMessage()));
        }
    }

    @PutMapping("/modificar/{id}")
    public ResponseEntity<?> modificar(@PathVariable Integer id, @RequestBody PromocionModel promo) {
        PromocionModel existente = promocionService.buscarPorId(id);

        if (existente == null) {
            return ResponseEntity.status(404).body("Promoción no encontrada");
        }

        existente.setNombre(promo.getNombre());
        existente.setEstado(promo.getEstado());
        existente.setTipo(promo.getTipo());
        existente.setValor(promo.getValor());
        existente.setDescripcion(promo.getDescripcion());
        existente.setDescuentoPorcentaje(promo.getDescuentoPorcentaje());
        existente.setFechaInicio(promo.getFechaInicio());
        existente.setFechaFin(promo.getFechaFin());

        return ResponseEntity.ok(promocionService.guardar(existente));
    }

    @DeleteMapping("/eliminar/{id}")
    public ResponseEntity<?> eliminar(@PathVariable Integer id) {
        boolean eliminado = promocionService.eliminar(id);

        if (!eliminado) {
            return ResponseEntity.status(404).body(Map.of("error", "Promoción no encontrada"));
        }
        return ResponseEntity.ok(Map.of("mensaje", "Promoción eliminada"));
    }
}