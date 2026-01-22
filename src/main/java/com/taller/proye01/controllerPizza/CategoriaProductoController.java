package com.taller.proye01.controllerPizza;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.taller.proye01.modelPizza.CategoriaProductoModel;
import com.taller.proye01.serviciosPizza.CategoriaProductoService;

@RestController
@RequestMapping("/categoria")
@CrossOrigin(origins = "http://localhost:4200")
public class CategoriaProductoController {

    @Autowired
    private CategoriaProductoService categoriaService;

    @GetMapping()
    public List<CategoriaProductoModel> listarCategorias() {
        return categoriaService.listarCategorias();
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> buscarPorId(@PathVariable Integer id) {
        CategoriaProductoModel cat = categoriaService.buscarPorId(id);
        if (cat == null) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body(Map.of("error", "Categoría no encontrada"));
        }
        return ResponseEntity.ok(cat);
    }

    @PostMapping("/create")
    public ResponseEntity<?> crearCategoria(@RequestBody CategoriaProductoModel categoria) {
        try {
            CategoriaProductoModel nueva = categoriaService.guardar(categoria);
            return ResponseEntity.ok(nueva);
        } catch (Exception e) {
            return ResponseEntity.status(500)
                    .body(Map.of("error", "Error al crear categoría: " + e.getMessage()));
        }
    }

    @PutMapping("/modificar/{id}")
    public ResponseEntity<?> modificarCategoria(@PathVariable Integer id,
                                                @RequestBody CategoriaProductoModel categoria) {
        try {
            CategoriaProductoModel actual = categoriaService.buscarPorId(id);
            if (actual == null) {
                return ResponseEntity.status(404).body("Categoría no encontrada");
            }

            actual.setNombreCategoria(categoria.getNombreCategoria());
            actual.setDescripcion(categoria.getDescripcion());

            CategoriaProductoModel actualizado = categoriaService.guardar(actual);
            return ResponseEntity.ok(actualizado);

        } catch (Exception e) {
            return ResponseEntity.status(500)
                    .body(Map.of("error", "Error al modificar categoría: " + e.getMessage()));
        }
    }

    @DeleteMapping("/eliminar/{id}")
    public ResponseEntity<?> eliminarCategoria(@PathVariable Integer id) {
        boolean eliminado = categoriaService.eliminar(id);

        if (!eliminado) {
            return ResponseEntity.status(404)
                    .body(Map.of("error", "Categoría no encontrada"));
        }

        return ResponseEntity.ok(Map.of("mensaje", "Categoría eliminada"));
    }
}