package com.taller.proye01.servicio;


import java.time.LocalDate;
import java.util.List;
import java.util.Map;

public interface FocoCalorService {

  
    List<Map<String, Object>> listarFocos();

    Map<String, Object> obtenerPorId(Integer id);

    List<Map<String, Object>> consultarFocosNasaSama();

    Map<String, Object> sincronizarFocosNasaSama();
    
    List<Map<String, Object>> consultarFocosNasaPorFecha(String producto, Integer dias, LocalDate fecha);
    Map<String, Object> sincronizarFocosNasaPorFecha(String producto, Integer dias, LocalDate fecha);

    List<Map<String, Object>> listarFocosSinIncendio();

    List<Map<String, Object>> listarFocosPorIncendio(Integer idIncendio);

    Map<String, Object> asociarFocoAIncendio(Integer idFoco, Integer idIncendio);

    
    Map<String, Object> quitarFocoDeIncendio(Integer idFoco);

   
    List<Map<String, Object>> buscarPorConfianza(String confianza);

   
    List<Map<String, Object>> buscarPorSatelite(String satelite);

   
    List<Map<String, Object>> buscarDentroDeArea(String wktArea);

   
    List<Map<String, Object>> buscarCercanos(Double lat, Double lon, Double radioMetros);

    Map<String, Object> listarGeoJSON();

	List<Map<String, Object>> consultarFocosNasaSamaPorProducto(String producto, Integer dias);

	Map<String, Object> sincronizarFocosNasaSamaPorProducto(String producto, Integer dias);
}