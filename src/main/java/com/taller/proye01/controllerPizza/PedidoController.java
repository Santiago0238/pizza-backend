package com.taller.proye01.controllerPizza;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import com.taller.proye01.modelPizza.PedidoModel;
import com.taller.proye01.serviciosPizza.PedidoService;

@RestController
@RequestMapping("/pedidos")
@CrossOrigin(origins = "http://localhost:4200")
public class PedidoController {

    @Autowired
    private PedidoService pedidoService;

    @GetMapping()
    public List<PedidoModel> listar() {
        return pedidoService.listar();
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> buscar(@PathVariable Integer id) {
        PedidoModel p = pedidoService.buscarPorId(id);
        if (p == null) {
            return ResponseEntity.status(404).body(Map.of("error", "Pedido no encontrado"));
        }
        return ResponseEntity.ok(p);
    }

    @GetMapping("/cliente/{idCliente}")
    public List<PedidoModel> pedidosPorCliente(@PathVariable Integer idCliente) {
        return pedidoService.listarPorCliente(idCliente);
    }

    @GetMapping("/estado/{estado}")
    public List<PedidoModel> pedidosPorEstado(@PathVariable String estado) {
        return pedidoService.listarPorEstado(estado);
    }

    @GetMapping("/fecha")
    public List<PedidoModel> pedidosPorFecha(@RequestParam Date fecha) {
        return pedidoService.listarPorFecha(fecha);
    }

    @PostMapping("/create")
    public ResponseEntity<?> crear(@RequestBody PedidoModel pedido) {
        try {
            PedidoModel nuevo = pedidoService.guardar(pedido);
            return ResponseEntity.ok(nuevo);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", "Error al crear pedido: " + e.getMessage()));
        }
    }

    @PutMapping("/modificar/{id}")
    public ResponseEntity<?> modificar(@PathVariable Integer id, @RequestBody PedidoModel pedido) {
        PedidoModel existente = pedidoService.buscarPorId(id);

        if (existente == null) {
            return ResponseEntity.status(404).body("Pedido no encontrado");
        }

        existente.setEstadoPedido(pedido.getEstadoPedido());
        existente.setTipo(pedido.getTipo());
        existente.setFecha(pedido.getFecha());
        existente.setTiempoEstimadoEntrega(pedido.getTiempoEstimadoEntrega());
        existente.setTotal(pedido.getTotal());
        existente.setIdUsuario(pedido.getIdUsuario());
        existente.setIdDireccion(pedido.getIdDireccion());

        return ResponseEntity.ok(pedidoService.guardar(existente));
    }

    @DeleteMapping("/eliminar/{id}")
    public ResponseEntity<?> eliminar(@PathVariable Integer id) {
        boolean eliminado = pedidoService.eliminar(id);
        if (!eliminado) {
            return ResponseEntity.status(404).body(Map.of("error", "Pedido no encontrado"));
        }
        return ResponseEntity.ok(Map.of("mensaje", "Pedido eliminado"));
    }
}