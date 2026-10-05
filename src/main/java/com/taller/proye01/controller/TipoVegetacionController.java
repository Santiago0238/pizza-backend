package com.taller.proye01.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.taller.proye01.model.TipoVegetacion;
import com.taller.proye01.servicioIMP.TipoVegetacionServiceIMP;

@RestController
@RequestMapping("/api/tipos-vegetacion")
@CrossOrigin(origins = "*")
public class TipoVegetacionController {

    private final TipoVegetacionServiceIMP tipoVegetacionService;

    public TipoVegetacionController(
            TipoVegetacionServiceIMP tipoVegetacionService) {

        this.tipoVegetacionService = tipoVegetacionService;
    }

 
    @GetMapping
    public ResponseEntity<List<TipoVegetacion>> listar() {

        return ResponseEntity.ok(
            tipoVegetacionService.listar()
        );
    }

   
    @GetMapping("/{id}")
    public ResponseEntity<TipoVegetacion> obtenerPorId(
            @PathVariable Integer id) {

        return ResponseEntity.ok(
            tipoVegetacionService.obtenerPorId(id)
        );
    }

    @PostMapping
    public ResponseEntity<TipoVegetacion> guardar(
            @RequestBody TipoVegetacion tipoVegetacion) {

        return ResponseEntity.ok(
            tipoVegetacionService.guardar(
                tipoVegetacion
            )
        );
    }

    
    @PutMapping("/{id}")
    public ResponseEntity<TipoVegetacion> actualizar(
            @PathVariable Integer id,
            @RequestBody TipoVegetacion tipoVegetacion) {

        return ResponseEntity.ok(
            tipoVegetacionService.actualizar(
                id,
                tipoVegetacion
            )
        );
    }

   
    @DeleteMapping("/{id}")
    public ResponseEntity<String> eliminar(
            @PathVariable Integer id) {

        tipoVegetacionService.eliminar(id);

        return ResponseEntity.ok(
            "Tipo de vegetación eliminado correctamente"
        );
    }

  
    @GetMapping("/buscar")
    public ResponseEntity<List<TipoVegetacion>> buscarPorNombre(
            @RequestParam(required = false) String nombre) {

        return ResponseEntity.ok(
            tipoVegetacionService.buscarPorNombre(
                nombre
            )
        );
    }
}