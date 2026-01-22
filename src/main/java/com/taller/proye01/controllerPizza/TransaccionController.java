package com.taller.proye01.controllerPizza;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import com.taller.proye01.modelPizza.TransaccionModel;
import com.taller.proye01.serviciosPizza.TransaccionService;

@RestController
@RequestMapping("/transacciones")
@CrossOrigin(origins = "http://localhost:4200")
public class TransaccionController {

    @Autowired
    private TransaccionService transaccionService;

    @GetMapping()
    public List<TransaccionModel> listar() {
        return transaccionService.listar();
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> buscar(@PathVariable Integer id) {
        TransaccionModel t = transaccionService.buscarPorId(id);
        if (t == null) {
            return ResponseEntity.status(404).body(Map.of("error", "Transacción no encontrada"));
        }
        return ResponseEntity.ok(t);
    }

    @GetMapping("/pedido/{idPedido}")
    public List<TransaccionModel> porPedido(@PathVariable Integer idPedido) {
        return transaccionService.listarPorPedido(idPedido);
    }

    @GetMapping("/usuario/{idUsuario}")
    public List<TransaccionModel> porUsuario(@PathVariable Integer idUsuario) {
        return transaccionService.listarPorUsuario(idUsuario);
    }

    @GetMapping("/fecha")
    public List<TransaccionModel> porFecha(@RequestParam Date fecha) {
        return transaccionService.listarPorFecha(fecha);
    }

    @PostMapping("/create")
    public ResponseEntity<?> crear(@RequestBody TransaccionModel trans) {
        try {
            TransaccionModel nuevo = transaccionService.guardar(trans);
            return ResponseEntity.ok(nuevo);
        } catch (Exception e) {
            return ResponseEntity.status(500)
                    .body(Map.of("error", "Error al crear transacción: " + e.getMessage()));
        }
    }
}