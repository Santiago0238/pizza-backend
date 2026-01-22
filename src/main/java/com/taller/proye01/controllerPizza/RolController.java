package com.taller.proye01.controllerPizza;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import com.taller.proye01.modelPizza.RolModel;
import com.taller.proye01.serviciosPizza.RolService;

@RestController
@RequestMapping("/roles")
@CrossOrigin(origins = "http://localhost:4200")
public class RolController {

    @Autowired
    private RolService rolService;

    @GetMapping()
    public List<RolModel> listar() {
        return rolService.listar();
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> buscar(@PathVariable Integer id) {
        RolModel r = rolService.buscarPorId(id);
        if (r == null) {
            return ResponseEntity.status(404)
                    .body(Map.of("error", "Rol no encontrado"));
        }
        return ResponseEntity.ok(r);
    }

    @GetMapping("/buscar")
    public List<RolModel> buscarPorNombre(@RequestParam String nombre) {
        return rolService.buscarPorNombre(nombre);
    }

    @PostMapping("/create")
    public ResponseEntity<?> crear(@RequestBody RolModel rol) {
        try {
            RolModel nuevo = rolService.guardar(rol);
            return ResponseEntity.ok(nuevo);
        } catch (Exception e) {
            return ResponseEntity.status(500)
                    .body(Map.of("error", "Error al crear rol: " + e.getMessage()));
        }
    }

    @PutMapping("/modificar/{id}")
    public ResponseEntity<?> modificar(@PathVariable Integer id,
                                       @RequestBody RolModel rol) {
        RolModel existente = rolService.buscarPorId(id);

        if (existente == null) {
            return ResponseEntity.status(404).body("Rol no encontrado");
        }

        existente.setNombreRol(rol.getNombreRol());
        existente.setDescripcion(rol.getDescripcion());

        return ResponseEntity.ok(rolService.guardar(existente));
    }

    @DeleteMapping("/eliminar/{id}")
    public ResponseEntity<?> eliminar(@PathVariable Integer id) {
        boolean eliminado = rolService.eliminar(id);

        if (!eliminado) {
            return ResponseEntity.status(404).body(Map.of("error", "Rol no encontrado"));
        }

        return ResponseEntity.ok(Map.of("mensaje", "Rol eliminado"));
    }
}