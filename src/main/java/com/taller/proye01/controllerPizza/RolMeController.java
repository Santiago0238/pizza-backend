package com.taller.proye01.controllerPizza;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import com.taller.proye01.modelPizza.RolMeModel;
import com.taller.proye01.modelPizza.RolMePK;
import com.taller.proye01.serviciosPizza.RolMeService;

@RestController
@RequestMapping("/rol-menu")
@CrossOrigin(origins = "http://localhost:4200")
public class RolMeController {

    @Autowired
    private RolMeService rolMeService;

    @GetMapping()
    public List<RolMeModel> listar() {
        return rolMeService.listar();
    }

    @GetMapping("/{idRol}/{idMenu}")
    public ResponseEntity<?> buscar(@PathVariable Integer idRol, @PathVariable Integer idMenu) {
        RolMePK pk = new RolMePK(idRol, idMenu);

        RolMeModel rel = rolMeService.buscar(pk);

        if (rel == null) {
            return ResponseEntity.status(404)
                    .body(Map.of("error", "Relación Rol-Menu no encontrada"));
        }

        return ResponseEntity.ok(rel);
    }

    @GetMapping("/rol/{idRol}")
    public List<RolMeModel> porRol(@PathVariable Integer idRol) {
        return rolMeService.listarPorRol(idRol);
    }

    @GetMapping("/menu/{idMenu}")
    public List<RolMeModel> porMenu(@PathVariable Integer idMenu) {
        return rolMeService.listarPorMenu(idMenu);
    }

    @PostMapping("/create")
    public ResponseEntity<?> crear(@RequestBody RolMeModel model) {
        try {
            RolMeModel nuevo = rolMeService.guardar(model);
            return ResponseEntity.ok(nuevo);
        } catch (Exception e) {
            return ResponseEntity.status(500)
                    .body(Map.of("error", "Error al crear relación rol-menu: " + e.getMessage()));
        }
    }

    @DeleteMapping("/eliminar/{idRol}/{idMenu}")
    public ResponseEntity<?> eliminar(@PathVariable Integer idRol, @PathVariable Integer idMenu) {
        RolMePK pk = new RolMePK(idRol, idMenu);

        boolean eliminado = rolMeService.eliminar(pk);

        if (!eliminado) {
            return ResponseEntity.status(404)
                    .body(Map.of("error", "Relación no encontrada"));
        }

        return ResponseEntity.ok(Map.of("mensaje", "Relación eliminada"));
    }
}