package com.taller.proye01.controllerPizza;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import com.taller.proye01.modelPizza.ClienteModel;
import com.taller.proye01.serviciosPizza.ClienteService;

@RestController
@RequestMapping("/clientes")
@CrossOrigin(origins = "http://localhost:4200")
public class ClienteController {

    @Autowired
    private ClienteService clienteService;

    @GetMapping()
    public List<ClienteModel> listarClientes() {
        return clienteService.listarClientes();
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> buscarPorId(@PathVariable Integer id) {
        ClienteModel cli = clienteService.buscarPorId(id);
        if (cli == null) {
            return ResponseEntity.status(404).body(Map.of("error", "Cliente no encontrado"));
        }
        return ResponseEntity.ok(cli);
    }

    @GetMapping("/buscar")
    public List<ClienteModel> buscarPorNombre(@RequestParam String nombre) {
        return clienteService.buscarPorNombre(nombre);
    }

    @PostMapping("/create")
    public ResponseEntity<?> crearCliente(@RequestBody ClienteModel cliente) {
        try {
            ClienteModel nuevo = clienteService.guardar(cliente);
            return ResponseEntity.ok(nuevo);
        } catch (Exception e) {
            return ResponseEntity.status(500)
                    .body(Map.of("error", "Error al crear cliente: " + e.getMessage()));
        }
    }

    @PutMapping("/modificar/{id}")
    public ResponseEntity<?> modificarCliente(@PathVariable Integer id,
                                              @RequestBody ClienteModel cliente) {
        ClienteModel existente = clienteService.buscarPorId(id);
        if (existente == null) {
            return ResponseEntity.status(404).body("Cliente no encontrado");
        }

        existente.setNombre(cliente.getNombre());
        existente.setApellido(cliente.getApellido());
        existente.setTelefono(cliente.getTelefono());
        existente.setEmail(cliente.getEmail());
        existente.setDirecciones(cliente.getDirecciones());
        existente.setActivo(cliente.getActivo());

        ClienteModel actualizado = clienteService.guardar(existente);
        return ResponseEntity.ok(actualizado);
    }

    @DeleteMapping("/eliminar/{id}")
    public ResponseEntity<?> eliminar(@PathVariable Integer id) {
        if (!clienteService.eliminar(id)) {
            return ResponseEntity.status(404)
                    .body(Map.of("error", "Cliente no encontrado"));
        }
        return ResponseEntity.ok(Map.of("mensaje", "Cliente eliminado"));
    }
    
    @PutMapping("/inactivar/{id}")
    public ResponseEntity<?> inactivarCliente(
            @PathVariable Integer id,
            @RequestBody Map<String, String> body) {

        String motivo = body.getOrDefault("motivo", "");

        ClienteModel cli = clienteService.buscarPorId(id);
        if (cli == null) {
            return ResponseEntity.status(404).body(Map.of("error", "Cliente no encontrado"));
        }

        cli.setActivo(0); // Inactivo
       

        clienteService.guardar(cli);

        return ResponseEntity.ok(Map.of(
                "mensaje", "Cliente inactivado",
                "id", id,
                "motivo", motivo
        ));
    }
    
    @PutMapping("/reactivar/{id}")
    public ResponseEntity<?> reactivarCliente(@PathVariable Integer id) {

        ClienteModel cli = clienteService.buscarPorId(id);
        if (cli == null) {
            return ResponseEntity.status(404).body(Map.of("error", "Cliente no encontrado"));
        }

        cli.setActivo(1);   // Activo
  

        clienteService.guardar(cli);

        return ResponseEntity.ok(Map.of(
                "mensaje", "Cliente reactivado",
                "id", id
        ));
    }

    
    
}