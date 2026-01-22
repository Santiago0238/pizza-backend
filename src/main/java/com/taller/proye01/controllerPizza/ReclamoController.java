package com.taller.proye01.controllerPizza;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import com.taller.proye01.modelPizza.ReclamoModel;
import com.taller.proye01.serviciosPizza.ReclamoService;

@RestController
@RequestMapping("/reclamos")
@CrossOrigin(origins = "http://localhost:4200")
public class ReclamoController {

    @Autowired
    private ReclamoService reclamoService;

    @GetMapping()
    public List<ReclamoModel> listar() {
        return reclamoService.listar();
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> buscar(@PathVariable Integer id) {
        ReclamoModel r = reclamoService.buscarPorId(id);
        if (r == null) {
            return ResponseEntity.status(404).body(Map.of("error", "Reclamo no encontrado"));
        }
        return ResponseEntity.ok(r);
    }

    @GetMapping("/pedido/{idPedido}")
    public List<ReclamoModel> porPedido(@PathVariable Integer idPedido) {
        return reclamoService.listarPorPedido(idPedido);
    }

    @GetMapping("/usuario/{idUsuario}")
    public List<ReclamoModel> porUsuario(@PathVariable Integer idUsuario) {
        return reclamoService.listarPorUsuario(idUsuario);
    }

    @GetMapping("/fecha")
    public List<ReclamoModel> porFecha(@RequestParam Date fecha) {
        return reclamoService.listarPorFecha(fecha);
    }

    @PostMapping("/create")
    public ResponseEntity<?> crear(@RequestBody ReclamoModel reclamo) {
        try {
            ReclamoModel nuevo = reclamoService.guardar(reclamo);
            return ResponseEntity.ok(nuevo);
        } catch (Exception e) {
            return ResponseEntity.status(500)
                    .body(Map.of("error", "Error al crear reclamo: " + e.getMessage()));
        }
    }

    @PutMapping("/modificar/{id}")
    public ResponseEntity<?> modificar(@PathVariable Integer id,
                                       @RequestBody ReclamoModel reclamo) {
        ReclamoModel existente = reclamoService.buscarPorId(id);
        if (existente == null) {
            return ResponseEntity.status(404).body("Reclamo no encontrado");
        }

        existente.setDescripcion(reclamo.getDescripcion());
        existente.setEstado(reclamo.getEstado());
        existente.setFecha(reclamo.getFecha());
        existente.setIdPedido(reclamo.getIdPedido());
        existente.setIdUsuario(reclamo.getIdUsuario());

        return ResponseEntity.ok(reclamoService.guardar(existente));
    }

    @DeleteMapping("/eliminar/{id}")
    public ResponseEntity<?> eliminar(@PathVariable Integer id) {
        boolean eliminado = reclamoService.eliminar(id);

        if (!eliminado) {
            return ResponseEntity.status(404).body(Map.of("error", "Reclamo no encontrado"));
        }

        return ResponseEntity.ok(Map.of("mensaje", "Reclamo eliminado"));
    }
}