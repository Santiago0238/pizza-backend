package com.taller.proye01.controller;

import java.util.List;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.http.MediaType;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.taller.proye01.DTO.RutaDTO;
import com.taller.proye01.model.PuntoRuta;
import com.taller.proye01.model.Ruta;
import com.taller.proye01.repository.PuntoRutaRepo;
import com.taller.proye01.repository.RutaRepo;
import com.taller.proye01.servicio.RutaService;

@RestController
@RequestMapping("/api/rutas")
@CrossOrigin("*")
public class RutaController {

    private final RutaService rutaService;
    private final RutaRepo rutaRepo;
    private final PuntoRutaRepo prutaRepo;

    public RutaController(RutaService rutaService, RutaRepo rutaRepo, PuntoRutaRepo prutaRepo) {
        this.rutaService = rutaService;
        this.rutaRepo = rutaRepo;
        this.prutaRepo = prutaRepo;
    }

    @GetMapping
    public List<Ruta> listarRutasCompletas() {
       
        List<Ruta> rutas = rutaRepo.findAll();
        for (Ruta ruta : rutas) {
            List<PuntoRuta> puntos = prutaRepo.findByRutaId(ruta.getId());
            ruta.setPuntos(puntos);
        }
        return rutas;
    }
    @PostMapping(
    	    value = "/importar/kml",
    	    consumes = MediaType.MULTIPART_FORM_DATA_VALUE
    	)
    	public ResponseEntity<Map<String, Object>> importarKml(

    	        @RequestParam("archivo") MultipartFile archivo,

    	        @RequestParam(
    	            value = "usuarioId",
    	            required = false
    	        ) Integer usuarioId,

    	        @RequestParam(
    	            value = "nombre",
    	            required = false
    	        ) String nombre,

    	        @RequestParam(
    	            value = "descripcion",
    	            required = false
    	        ) String descripcion

    	) {

    	    try {

    	        if (archivo.isEmpty()) {
    	            return ResponseEntity
    	                    .badRequest()
    	                    .body(Map.of(
    	                        "error",
    	                        "El archivo KML está vacío"
    	                    ));
    	        }

    	        Map<String, Object> resultado =
    	                rutaService.registrarRutaDesdeKml(
    	                    archivo.getInputStream(),
    	                    usuarioId,
    	                    nombre,
    	                    descripcion
    	                );

    	        return ResponseEntity.ok(resultado);

    	    } catch (Exception e) {

    	        return ResponseEntity
    	                .badRequest()
    	                .body(Map.of(
    	                    "error",
    	                    e.getMessage()
    	                ));
    	    }
    	}

    @GetMapping("/geojson")
    public ResponseEntity<Map<String, Object>> listarComoGeoJSON() {
        return ResponseEntity.ok(rutaService.listarComoGeoJSON());
    }

    @PostMapping
    public ResponseEntity<Map<String, Object>> registrarRuta(
            @RequestBody Map<String, Object> payload) {

        return ResponseEntity.ok(rutaService.registrarRuta(payload));
    }

    @GetMapping(
        value = "/{id}/kml",
        produces = "application/vnd.google-earth.kml+xml"
    )
    public ResponseEntity<byte[]> exportarKml(
            @PathVariable Integer id) {

        return rutaService.exportarKml(id);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Map<String, Object>> editarRuta(
            @PathVariable Integer id,
            @RequestBody Ruta datosActualizados) {

        return ResponseEntity.ok(rutaService.editarRuta(id, datosActualizados));
    }

    @GetMapping("/filtrar")
    public ResponseEntity<List<RutaDTO>> filtrarRutas(
            @RequestParam String wktArea) {

        return ResponseEntity.ok(rutaService.filtrarRutas(wktArea));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Map<String, Object>> eliminarRuta(
            @PathVariable Integer id) {

        return ResponseEntity.ok(rutaService.eliminarRuta(id));
    }
}