package com.taller.proye01.controllerPizza;

import java.util.Date;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import com.taller.proye01.modelPizza.VentaModel;
import com.taller.proye01.serviciosPizza.VentaService;

@RestController
@RequestMapping("/ventas")
@CrossOrigin(origins = "http://localhost:4200")
public class VentaController {

    @Autowired
    private VentaService ventaService;

    @GetMapping()
    public List<VentaModel> listar() {
        return ventaService.listar();
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> buscar(@PathVariable Integer id) {
        VentaModel v = ventaService.buscarPorId(id);
        if (v == null) {
            return ResponseEntity.status(404)
                    .body(Map.of("error", "Venta no encontrada"));
        }
        return ResponseEntity.ok(v);
    }

    @GetMapping("/cliente/{idCliente}")
    public List<VentaModel> ventasPorCliente(@PathVariable Integer idCliente) {
        return ventaService.listarPorCliente(idCliente);
    }

    @GetMapping("/usuario/{idUsuario}")
    public List<VentaModel> ventasPorUsuario(@PathVariable Integer idUsuario) {
        return ventaService.listarPorUsuario(idUsuario);
    }

    @GetMapping("/fecha")
    public List<VentaModel> ventasPorFecha(@RequestParam Date fecha) {
        return ventaService.listarPorFecha(fecha);
    }

    @PostMapping("/estado/{estado}")
    public List<VentaModel> ventasPorEstado(@PathVariable String estado) {
        return ventaService.listarPorEstado(estado);
    }

    @PostMapping("/create")
    public ResponseEntity<?> crear(@RequestBody VentaModel venta) {
        try {
            VentaModel nueva = ventaService.guardar(venta);
            return ResponseEntity.ok(nueva);
        } catch (Exception e) {
            return ResponseEntity.status(500)
                    .body(Map.of("error", "Error al registrar venta: " + e.getMessage()));
        }
    }
    
    @PutMapping("/modificar/{id}")
    public ResponseEntity<?> modificar(@PathVariable Integer id,
                                       @RequestBody VentaModel venta) {
        VentaModel existente = ventaService.buscarPorId(id);
        if (existente == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "Venta no encontrada"));
        }

        // Actualiza los campos que te interesen
        existente.setIdPedido(venta.getIdPedido());
        existente.setIdCliente(venta.getIdCliente());
        existente.setIdUsuario(venta.getIdUsuario());
        existente.setFecha(venta.getFecha());
        existente.setTotal(venta.getTotal());
        existente.setEstado(venta.getEstado());
        existente.setDetalle(venta.getDetalle());
        existente.setObservaciones(venta.getObservaciones());
        existente.setTipoDePago(venta.getTipoDePago());

        VentaModel actualizada = ventaService.guardar(existente);
        return ResponseEntity.ok(actualizada);
    }
    
}
