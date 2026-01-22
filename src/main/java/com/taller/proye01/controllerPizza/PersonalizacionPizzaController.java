package com.taller.proye01.controllerPizza;

import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import com.taller.proye01.modelPizza.PersonalizacionPizzaModel;
import com.taller.proye01.serviciosPizza.PersonalizacionPizzaService;

@RestController
@RequestMapping("/personalizacion")
@CrossOrigin(origins = "http://localhost:4200")
public class PersonalizacionPizzaController {

    @Autowired
    private PersonalizacionPizzaService personalizacionService;

    @GetMapping()
    public List<PersonalizacionPizzaModel> listar() {
        return personalizacionService.listar();
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> buscar(@PathVariable Integer id) {
        PersonalizacionPizzaModel p = personalizacionService.buscarPorId(id);
        if (p == null) {
            return ResponseEntity.status(404)
                    .body(Map.of("error", "Personalización no encontrada"));
        }
        return ResponseEntity.ok(p);
    }

    @GetMapping("/producto/{idProducto}")
    public List<PersonalizacionPizzaModel> porProducto(@PathVariable Integer idProducto) {
        return personalizacionService.porProducto(idProducto);
    }

    @PostMapping("/create")
    public ResponseEntity<?> crear(@RequestBody PersonalizacionPizzaModel model) {
        try {
            PersonalizacionPizzaModel nuevo = personalizacionService.guardar(model);
            return ResponseEntity.ok(nuevo);
        } catch (Exception e) {
            return ResponseEntity.status(500)
                    .body(Map.of("error", "Error al crear personalización: " + e.getMessage()));
        }
    }

    @PutMapping("/modificar/{id}")
    public ResponseEntity<?> modificar(@PathVariable Integer id,
                                       @RequestBody PersonalizacionPizzaModel model) {
        PersonalizacionPizzaModel existente = personalizacionService.buscarPorId(id);
        if (existente == null) {
            return ResponseEntity.status(404).body("Personalización no encontrada");
        }

        existente.setTamano(model.getTamano());
        existente.setMasa(model.getMasa());
        existente.setTipo(model.getTipo());
        existente.setCostoAdicional(model.getCostoAdicional());

        return ResponseEntity.ok(personalizacionService.guardar(existente));
    }

    @DeleteMapping("/eliminar/{id}")
    public ResponseEntity<?> eliminar(@PathVariable Integer id) {
        if (!personalizacionService.eliminar(id)) {
            return ResponseEntity.status(404)
                    .body(Map.of("error", "Personalización no encontrada"));
        }

        return ResponseEntity.ok(Map.of("mensaje", "Personalización eliminada"));
    }
}