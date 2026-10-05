package com.taller.proye01.servicio;

import java.io.InputStream;
import java.util.List;
import java.util.Map;

import org.springframework.http.ResponseEntity;

import com.taller.proye01.DTO.RutaDTO;
import com.taller.proye01.model.Ruta;

public interface RutaService {

    List<RutaDTO> listarRutas();

    Map<String, Object> listarComoGeoJSON();

    Map<String, Object> registrarRuta(Map<String, Object> payload);

    Map<String, Object> editarRuta(Integer id, Ruta datosActualizados);

    List<RutaDTO> filtrarRutas(String wktArea);

    Map<String, Object> eliminarRuta(Integer id);

	ResponseEntity<byte[]> exportarKml(Integer id);
	Map<String, Object> registrarRutaDesdeKml(
		    InputStream kmlInputStream,
		    Integer usuarioId,
		    String nombreRuta,
		    String descripcionRuta
		);
}