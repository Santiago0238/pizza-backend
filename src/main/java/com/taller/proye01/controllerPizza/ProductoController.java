package com.taller.proye01.controllerPizza;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import com.taller.proye01.modelPizza.ProductoModel;
import com.taller.proye01.serviciosPizza.ProductoService;

@RestController
@RequestMapping("/productos")
@CrossOrigin(origins = "http://localhost:4200")
public class ProductoController {

    @Autowired
    private ProductoService productoService;

    @GetMapping()
    public List<ProductoModel> listar() {
        return productoService.listar();
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> buscar(@PathVariable Integer id) {
        ProductoModel p = productoService.buscarPorId(id);
        if (p == null) {
            return ResponseEntity.status(404).body(Map.of("error", "Producto no encontrado"));
        }
        return ResponseEntity.ok(p);
    }

    @GetMapping("/categoria/{idCategoria}")
    public List<ProductoModel> productosPorCategoria(@PathVariable Integer idCategoria) {
        return productoService.listarPorCategoria(idCategoria);
    }

    @GetMapping("/proveedor/{idProveedor}")
    public List<ProductoModel> productosPorProveedor(@PathVariable Integer idProveedor) {
        return productoService.listarPorProveedor(idProveedor);
    }

    @GetMapping("/disponibles")
    public List<ProductoModel> disponibles() {
        return productoService.disponibles();
    }

    @GetMapping("/buscar")
    public List<ProductoModel> buscarPorNombre(@RequestParam String nombre) {
        return productoService.buscarPorNombre(nombre);
    }

    @PostMapping("/create")
    public ResponseEntity<?> crear(@RequestBody ProductoModel producto) {
        try {
            ProductoModel nuevo = productoService.guardar(producto);
            return ResponseEntity.ok(nuevo);
        } catch (Exception e) {
            return ResponseEntity.status(500)
                    .body(Map.of("error", "Error al crear producto: " + e.getMessage()));
        }
    }

    @PutMapping("/modificar/{id}")
    public ResponseEntity<?> modificar(@PathVariable Integer id, @RequestBody ProductoModel producto) {
        ProductoModel existente = productoService.buscarPorId(id);
        if (existente == null) {
            return ResponseEntity.status(404).body("Producto no encontrado");
        }

        existente.setNombre(producto.getNombre());
        existente.setPrecio(producto.getPrecio());
        existente.setDisponible(producto.getDisponible());
        existente.setImagenUrl(producto.getImagenUrl());
        existente.setIdProveedor(producto.getIdProveedor());
        existente.setIdCategoria(producto.getIdCategoria());

        return ResponseEntity.ok(productoService.guardar(existente));
    }

    @DeleteMapping("/eliminar/{id}")
    public ResponseEntity<?> eliminar(@PathVariable Integer id) {
        boolean eliminado = productoService.eliminar(id);

        if (!eliminado) {
            return ResponseEntity.status(404)
                    .body(Map.of("error", "Producto no encontrado"));
        }

        return ResponseEntity.ok(Map.of("mensaje", "Producto eliminado"));
    }
}