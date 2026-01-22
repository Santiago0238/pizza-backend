package com.taller.proye01.controllerPizza;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import com.taller.proye01.modelPizza.RolUsuModel;
import com.taller.proye01.modelPizza.RolUsuPK;
import com.taller.proye01.serviciosPizza.RolUsuService;

@RestController
@RequestMapping("/rol-usuario")
@CrossOrigin(origins = "http://localhost:4200")
public class RolUsuController {

    @Autowired
    private RolUsuService rolUsuService;

    @GetMapping()
    public List<RolUsuModel> listar() {
        return rolUsuService.listar();
    }

    @GetMapping("/{idRol}/{idUsuario}")
    public ResponseEntity<?> buscar(@PathVariable Integer idRol, @PathVariable Integer idUsuario) {
        RolUsuPK pk = new RolUsuPK(idRol, idUsuario);

        RolUsuModel rel = rolUsuService.buscar(pk);

        if (rel == null) {
            return ResponseEntity.status(404).body(Map.of("error", "Relación no encontrada"));
        }

        return ResponseEntity.ok(rel);
    }

    @GetMapping("/usuario/{idUsuario}")
    public List<RolUsuModel> porUsuario(@PathVariable Integer idUsuario) {
        return rolUsuService.listarPorUsuario(idUsuario);
    }

    @GetMapping("/rol/{idRol}")
    public List<RolUsuModel> porRol(@PathVariable Integer idRol) {
        return rolUsuService.listarPorRol(idRol);
    }

    @PostMapping("/create")
    public ResponseEntity<?> crear(@RequestBody RolUsuModel model) {
        try {
            RolUsuModel nuevo = rolUsuService.guardar(model);
            return ResponseEntity.ok(nuevo);
        } catch (Exception e) {
            return ResponseEntity.status(500)
                    .body(Map.of("error", "Error al crear relación rol-usuario: " + e.getMessage()));
        }
    }

    @DeleteMapping("/eliminar/{idRol}/{idUsuario}")
    public ResponseEntity<?> eliminar(@PathVariable Integer idRol, @PathVariable Integer idUsuario) {
        RolUsuPK pk = new RolUsuPK(idRol, idUsuario);

        boolean eliminado = rolUsuService.eliminar(pk);

        if (!eliminado) {
            return ResponseEntity.status(404).body(Map.of("error", "Relación no encontrada"));
        }

        return ResponseEntity.ok(Map.of("mensaje", "Relación eliminada"));
    }
}