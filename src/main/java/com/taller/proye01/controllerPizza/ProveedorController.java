package com.taller.proye01.controllerPizza;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import com.taller.proye01.modelPizza.ProveedorModel;
import com.taller.proye01.serviciosPizza.ProveedorService;

@RestController
@RequestMapping("/proveedores")
@CrossOrigin(origins = "http://localhost:4200")
public class ProveedorController {

    @Autowired
    private ProveedorService proveedorService;

    @GetMapping()
    public List<ProveedorModel> listar() {
        return proveedorService.listar();
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> buscar(@PathVariable Integer id) {
        ProveedorModel p = proveedorService.buscarPorId(id);
        if (p == null) {
            return ResponseEntity.status(404).body(Map.of("error", "Proveedor no encontrado"));
        }
        return ResponseEntity.ok(p);
    }

    @GetMapping("/tipo/{tipo}")
    public List<ProveedorModel> proveedoresPorTipo(@PathVariable String tipo) {
        return proveedorService.porTipo(tipo);
    }

    @PostMapping("/create")
    public ResponseEntity<?> crear(@RequestBody ProveedorModel proveedor) {
        try {
            ProveedorModel nuevo = proveedorService.guardar(proveedor);
            return ResponseEntity.ok(nuevo);
        } catch (Exception e) {
            return ResponseEntity.status(500)
                    .body(Map.of("error", "Error al crear proveedor: " + e.getMessage()));
        }
    }

    @PutMapping("/modificar/{id}")
    public ResponseEntity<?> modificar(@PathVariable Integer id, @RequestBody ProveedorModel proveedor) {
        ProveedorModel existente = proveedorService.buscarPorId(id);

        if (existente == null) {
            return ResponseEntity.status(404).body("Proveedor no encontrado");
        }

        existente.setNombre(proveedor.getNombre());
        existente.setTelefono(proveedor.getTelefono());
        existente.setEmail(proveedor.getEmail());
        existente.setTipoProducto(proveedor.getTipoProducto());
        existente.setDireccion(proveedor.getDireccion());
        existente.setPlazoPago(proveedor.getPlazoPago());

        return ResponseEntity.ok(proveedorService.guardar(existente));
    }

    @DeleteMapping("/eliminar/{id}")
    public ResponseEntity<?> eliminar(@PathVariable Integer id) {
        boolean eliminado = proveedorService.eliminar(id);

        if (!eliminado) {
            return ResponseEntity.status(404).body(Map.of("error", "Proveedor no encontrado"));
        }

        return ResponseEntity.ok(Map.of("mensaje", "Proveedor eliminado"));
    }
}