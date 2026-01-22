package com.taller.proye01.controllerPizza;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import com.taller.proye01.modelPizza.MenuModel;
import com.taller.proye01.serviciosPizza.MenuService;

@RestController
@RequestMapping("/menus")
@CrossOrigin(origins = "http://localhost:4200")
public class MenuController {

    @Autowired
    private MenuService menuService;

    @GetMapping()
    public List<MenuModel> listar() {
        return menuService.listar();
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> buscar(@PathVariable Integer id) {
        MenuModel m = menuService.buscarPorId(id);
        if (m == null) {
            return ResponseEntity.status(404).body(Map.of("error", "Menú no encontrado"));
        }
        return ResponseEntity.ok(m);
    }

    @PostMapping("/create")
    public ResponseEntity<?> crear(@RequestBody MenuModel menu) {
        try {
            MenuModel nuevo = menuService.guardar(menu);
            return ResponseEntity.ok(nuevo);
        } catch (Exception e) {
            return ResponseEntity.status(500).body(Map.of("error", "Error al crear menú: " + e.getMessage()));
        }
    }

    @PutMapping("/modificar/{id}")
    public ResponseEntity<?> modificar(@PathVariable Integer id, @RequestBody MenuModel menu) {
        MenuModel existente = menuService.buscarPorId(id);
        if (existente == null) {
            return ResponseEntity.status(404).body("Menú no encontrado");
        }

        existente.setNombre(menu.getNombre());
        existente.setEstado(menu.getEstado());

        return ResponseEntity.ok(menuService.guardar(existente));
    }

    @DeleteMapping("/eliminar/{id}")
    public ResponseEntity<?> eliminar(@PathVariable Integer id) {
        boolean eliminado = menuService.eliminar(id);
        if (!eliminado) {
            return ResponseEntity.status(404).body(Map.of("error", "Menú no encontrado"));
        }
        return ResponseEntity.ok(Map.of("mensaje", "Menú eliminado"));
    }
}