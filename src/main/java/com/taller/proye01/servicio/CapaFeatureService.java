package com.taller.proye01.servicio;

import java.util.List;
import java.util.Map;

public interface CapaFeatureService {

    List<Map<String, Object>> listarFeaturesPorCapa(Integer idCapa);


    Map<String, Object> obtenerFeaturePorId(Integer idFeature);

    Map<String, Object> registrarFeature(Integer idCapa, Map<String, Object> payload);

    Map<String, Object> actualizarFeature(Integer idFeature, Map<String, Object> payload);


    Map<String, Object> eliminarFeature(Integer idFeature);

    List<Map<String, Object>> listarFeaturesPorTipoVegetacion(Integer idTipoVegetacion);

    List<Map<String, Object>> listarFeaturesIntersectados(String wktArea);

 
    Map<String, Object> listarFeaturesComoGeoJSON(Integer idCapa);
    
}
