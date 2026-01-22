package com.taller.proye01.controllerPizza;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import com.taller.proye01.modelPizza.ProPromoModel;
import com.taller.proye01.modelPizza.ProPromoPK;
import com.taller.proye01.serviciosPizza.ProPromoService;

@RestController
@RequestMapping("/producto-promocion")
@CrossOrigin(origins = "http://localhost:4200")
public class ProPromoController {

    @Autowired
    private ProPromoService proPromoService;

    @GetMapping()
    public List<ProPromoModel> listar() {
        return proPromoService.listar();
    }

    @GetMapping("/{idPromo}/{idProd}")
    public ResponseEntity<?> buscar(@PathVariable Integer idPromo, @PathVariable Integer idProd) {
        ProPromoPK pk = new ProPromoPK(idPromo, idProd);

        ProPromoModel rel = proPromoService.buscar(pk);

        if (rel == null) {
            return ResponseEntity.status(404).body(Map.of("error", "Relación no encontrada"));
        }

        return ResponseEntity.ok(rel);
    }

    @GetMapping("/promocion/{idPromocion}")
    public List<ProPromoModel> porPromocion(@PathVariable Integer idPromocion) {
        return proPromoService.listarPorPromocion(idPromocion);
    }

    @GetMapping("/producto/{idProducto}")
    public List<ProPromoModel> porProducto(@PathVariable Integer idProducto) {
        return proPromoService.listarPorProducto(idProducto);
    }

    @PostMapping("/create")
    public ResponseEntity<?> crear(@RequestBody ProPromoModel model) {
        try {
            ProPromoModel nuevo = proPromoService.guardar(model);
            return ResponseEntity.ok(nuevo);
        } catch (Exception e) {
            return ResponseEntity.status(500)
                    .body(Map.of("error", "Error al crear relación: " + e.getMessage()));
        }
    }

    @DeleteMapping("/eliminar/{idPromo}/{idProd}")
    public ResponseEntity<?> eliminar(@PathVariable Integer idPromo, @PathVariable Integer idProd) {
        ProPromoPK pk = new ProPromoPK(idPromo, idProd);

        boolean eliminado = proPromoService.eliminar(pk);

        if (!eliminado) {
            return ResponseEntity.status(404)
                    .body(Map.of("error", "Relación no encontrada"));
        }

        return ResponseEntity.ok(Map.of("mensaje", "Relación eliminada"));
    }
}