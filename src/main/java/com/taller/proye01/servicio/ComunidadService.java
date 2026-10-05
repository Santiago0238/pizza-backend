package com.taller.proye01.servicio;


import java.util.List;
import java.util.Map;

public interface ComunidadService {

    List<Map<String, Object>> listar();

    Map<String, Object> obtenerPorId(Integer id);

    Map<String, Object> registrar(Map<String, Object> payload);

    Map<String, Object> actualizar(Integer id, Map<String, Object> payload);

    Map<String, Object> eliminar(Integer id);

    Map<String, Object> listarGeoJSON();

    List<Map<String, Object>> filtrarPorNivelRiesgo(String nivelRiesgo);
}