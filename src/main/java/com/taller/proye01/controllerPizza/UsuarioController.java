package com.taller.proye01.controllerPizza;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import com.taller.proye01.modelPizza.UsuarioModel;
import com.taller.proye01.serviciosPizza.UsuarioService;

@RestController
@RequestMapping("/usuarios")
@CrossOrigin(origins = "http://localhost:4200")
public class UsuarioController {

    @Autowired
    private UsuarioService usuarioService;

    @GetMapping()
    public List<UsuarioModel> listar() {
        return usuarioService.listar();
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> buscar(@PathVariable Integer id) {
        UsuarioModel u = usuarioService.buscarPorId(id);
        if (u == null) {
            return ResponseEntity.status(404)
                    .body(Map.of("error", "Usuario no encontrado"));
        }
        return ResponseEntity.ok(u);
    }

    @PostMapping("/create")
    public ResponseEntity<?> crear(@RequestBody UsuarioModel usuario) {
        try {
            UsuarioModel nuevo = usuarioService.guardar(usuario);
            return ResponseEntity.ok(nuevo);
        } catch (Exception e) {
            return ResponseEntity.status(500)
                    .body(Map.of("error", "Error al crear usuario: " + e.getMessage()));
        }
    }

    @PutMapping("/modificar/{id}")
    public ResponseEntity<?> modificar(@PathVariable Integer id, @RequestBody UsuarioModel usuario) {
        UsuarioModel existente = usuarioService.buscarPorId(id);

        if (existente == null) {
            return ResponseEntity.status(404).body("Usuario no encontrado");
        }

        existente.setNombreUsuario(usuario.getNombreUsuario());
        existente.setTelefono(usuario.getTelefono());
        existente.setCorreo(usuario.getCorreo());
        existente.setEstado(usuario.getEstado());
        existente.setIdRol(usuario.getIdRol());

        return ResponseEntity.ok(usuarioService.guardar(existente));
    }

    @DeleteMapping("/eliminar/{id}")
    public ResponseEntity<?> eliminar(@PathVariable Integer id) {
        boolean eliminado = usuarioService.eliminar(id);

        if (!eliminado) {
            return ResponseEntity.status(404).body(Map.of("error", "Usuario no encontrado"));
        }

        return ResponseEntity.ok(Map.of("mensaje", "Usuario eliminado"));
    }
}