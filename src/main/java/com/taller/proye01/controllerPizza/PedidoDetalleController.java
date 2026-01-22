package com.taller.proye01.controllerPizza;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import com.taller.proye01.modelPizza.PedidoDetalleModel;
import com.taller.proye01.serviciosPizza.PedidoDetalleService;

@RestController
@RequestMapping("/detalle-pedido")
@CrossOrigin(origins = "http://localhost:4200")
public class PedidoDetalleController {

    @Autowired
    private PedidoDetalleService detalleService;

    @GetMapping()
    public List<PedidoDetalleModel> listar() {
        return detalleService.listar();
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> buscar(@PathVariable Integer id) {
        PedidoDetalleModel d = detalleService.buscarPorId(id);
        if (d == null) {
            return ResponseEntity.status(404)
                    .body(Map.of("error", "Detalle no encontrado"));
        }
        return ResponseEntity.ok(d);
    }

    @GetMapping("/pedido/{idPedido}")
    public List<PedidoDetalleModel> porPedido(@PathVariable Integer idPedido) {
        return detalleService.listarPorPedido(idPedido);
    }

    @GetMapping("/producto/{idProducto}")
    public List<PedidoDetalleModel> porProducto(@PathVariable Integer idProducto) {
        return detalleService.listarPorProducto(idProducto);
    }

    @PostMapping("/create")
    public ResponseEntity<?> crear(@RequestBody PedidoDetalleModel detalle) {
        try {
            PedidoDetalleModel nuevo = detalleService.guardar(detalle);
            return ResponseEntity.ok(nuevo);
        } catch (Exception e) {
            return ResponseEntity.status(500)
                    .body(Map.of("error", "Error al crear detalle: " + e.getMessage()));
        }
    }

    @PutMapping("/modificar/{id}")
    public ResponseEntity<?> modificar(@PathVariable Integer id,
                                       @RequestBody PedidoDetalleModel detalle) {
        PedidoDetalleModel existente = detalleService.buscarPorId(id);
        if (existente == null) {
            return ResponseEntity.status(404).body("Detalle no encontrado");
        }

        existente.setCantidad(detalle.getCantidad());
        existente.setPrecioUnitario(detalle.getPrecioUnitario());

        return ResponseEntity.ok(detalleService.guardar(existente));
    }

    @DeleteMapping("/eliminar/{id}")
    public ResponseEntity<?> eliminar(@PathVariable Integer id) {
        boolean eliminado = detalleService.eliminar(id);
        if (!eliminado) {
            return ResponseEntity.status(404).body(Map.of("error", "Detalle no encontrado"));
        }
        return ResponseEntity.ok(Map.of("mensaje", "Detalle eliminado"));
    }
}