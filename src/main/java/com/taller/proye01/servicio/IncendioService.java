package com.taller.proye01.servicio;


import java.util.List;
import java.util.Map;

public interface IncendioService {

   
    List<Map<String, Object>> listarIncendios();

    Map<String, Object> registrarIncendioDesdeFoco(Integer idFoco, Map<String, Object> payload);
    
    Map<String, Object> obtenerPorId(Integer id);

   
    List<Map<String, Object>> listarIncendiosActivos();

    
    List<Map<String, Object>> listarHistorialIncendios();

   
    Map<String, Object> registrarIncendio(Map<String, Object> payload);

    
    Map<String, Object> actualizarIncendio(Integer id, Map<String, Object> payload);

    
    Map<String, Object> actualizarEstado(Integer id, Map<String, Object> payload);

   
    Map<String, Object> registrarMetodoControl(Integer id, Map<String, Object> payload);

    Map<String, Object> registrarSuperficieAfectada(Integer id, Map<String, Object> payload);

    List<Map<String, Object>> buscarPorEstado(String estado);

    
    List<Map<String, Object>> buscarPorNivelGravedad(String nivelGravedad);

    List<Map<String, Object>> buscarPorTexto(String texto);

    Map<String, Object> eliminarIncendio(Integer id);
}