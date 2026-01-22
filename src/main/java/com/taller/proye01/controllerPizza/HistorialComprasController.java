package com.taller.proye01.controllerPizza;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import com.taller.proye01.modelPizza.HistorialComprasModel;
import com.taller.proye01.serviciosPizza.HistorialComprasService;

@RestController
@RequestMapping("/historial")
@CrossOrigin(origins = "http://localhost:4200")
public class HistorialComprasController {

    @Autowired
    private HistorialComprasService historialService;

    @GetMapping()
    public List<HistorialComprasModel> listar() {
        return historialService.listar();
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> buscar(@PathVariable Integer id) {
        HistorialComprasModel h = historialService.buscarPorId(id);
        if (h == null) {
            return ResponseEntity.status(404).body(Map.of("error", "Historial no encontrado"));
        }
        return ResponseEntity.ok(h);
    }

    @GetMapping("/cliente/{idCliente}")
    public List<HistorialComprasModel> historialPorCliente(@PathVariable Integer idCliente) {
        return historialService.historialPorCliente(idCliente);
    }

    @GetMapping("/pedido/{idPedido}")
    public HistorialComprasModel historialPorPedido(@PathVariable Integer idPedido) {
        return historialService.historialPorPedido(idPedido);
    }

    @PostMapping("/create")
    public ResponseEntity<?> crear(@RequestBody HistorialComprasModel historial) {
        try {
            HistorialComprasModel nuevo = historialService.guardar(historial);
            return ResponseEntity.ok(nuevo);
        } catch (Exception e) {
            return ResponseEntity.status(500)
                    .body(Map.of("error", "Error al crear historial: " + e.getMessage()));
        }
    }
}