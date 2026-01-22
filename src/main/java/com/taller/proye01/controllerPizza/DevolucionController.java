package com.taller.proye01.controllerPizza;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.taller.proye01.modelPizza.Devolucion;
import com.taller.proye01.serviciosPizza.DevolucionService;

@RestController
@RequestMapping("/devoluciones")
@CrossOrigin(origins = "http://localhost:4200")
public class DevolucionController {

    private final DevolucionService devolucionService;

    public DevolucionController(DevolucionService devolucionService) {
        this.devolucionService = devolucionService;
    }

    // POST /devoluciones  -> Guardar devolución (lo usa el modal)
    @PostMapping
    public ResponseEntity<Devolucion> registrar(@RequestBody Devolucion request) {
    	Devolucion response = devolucionService.registrarDevolucion(request);
        return ResponseEntity.ok(response);
    }

    // GET /devoluciones/venta/{idVenta} -> listar devoluciones de una venta
    @GetMapping("/venta/{idVenta}")
    public ResponseEntity<List<Devolucion>> listarPorVenta(@PathVariable Integer idVenta) {
        List<Devolucion> lista = devolucionService.listarPorVenta(idVenta);
        return ResponseEntity.ok(lista);
    }
}