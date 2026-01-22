package com.taller.proye01.controllerPizza;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.taller.proye01.modelPizza.ReporteFiltroDTO;
import com.taller.proye01.modelPizza.ReporteResponseDTO;
import com.taller.proye01.serviciosPizza.ReporteService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/reportes")
@CrossOrigin(origins = "http://localhost:4200")
@RequiredArgsConstructor
public class ReporteController {

    private final ReporteService reporteService;

    @PostMapping("/generar")
    public ResponseEntity<?> generarReporte(@RequestBody ReporteFiltroDTO filtro) {
        try {
            ReporteResponseDTO resp = reporteService.generar(filtro);
            return ResponseEntity.ok(resp);
        } catch (IllegalArgumentException e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", e.getMessage()));
        } catch (Exception e) {
            return ResponseEntity.status(500)
                    .body(Map.of("error", "Error al generar reporte: " + e.getMessage()));
        }
    }
}
