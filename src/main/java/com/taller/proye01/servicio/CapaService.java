package com.taller.proye01.servicio;

import java.util.List;
import java.util.Map;

import org.springframework.web.multipart.MultipartFile;

public interface CapaService {

    List<Map<String, Object>> listarCapas();

    Map<String, Object> obtenerPorId(Integer id);

    Map<String, Object> registrarCapa(Map<String, Object> payload);

    Map<String, Object> actualizarCapa(Integer id, Map<String, Object> payload);


    Map<String, Object> eliminarCapa(Integer id);

 
    List<Map<String, Object>> listarCapasPublicas();


    List<Map<String, Object>> listarCapasPorTipo(String tipo);


    List<Map<String, Object>> listarCapasPorUsuario(Integer idUsuario);

    List<Map<String, Object>> buscarPorNombre(String nombre);

    Map<String, Object> listarTodasComoGeoJSON();

    Map<String, Object> obtenerCapaComoGeoJSON(Integer idCapa);

    Map<String, Object> importarGeoJSON(Map<String, Object> payload);
    
    Map<String, Object> registrarCapaConKml(
            MultipartFile archivoKml,
            String nombre,
            String tipo,
            Boolean publico,
            Integer idUsuario,
            Integer idTipoVegetacion,
            String descripcion
    );
    
}
