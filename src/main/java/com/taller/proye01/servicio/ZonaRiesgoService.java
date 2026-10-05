package com.taller.proye01.servicio;


import java.util.List;

import org.springframework.web.multipart.MultipartFile;

import com.taller.proye01.model.ZonaRiesgo;

public interface ZonaRiesgoService {

    List<ZonaRiesgo> listar();

    ZonaRiesgo obtenerPorId(Integer id);

    ZonaRiesgo guardar(ZonaRiesgo zona);

    ZonaRiesgo actualizar(Integer id, ZonaRiesgo zona);

    void eliminar(Integer id);

    List<ZonaRiesgo> buscarPorNivel(String nivelRiesgo);

    List<ZonaRiesgo> buscarPorTexto(String texto);
    ZonaRiesgo guardarDesdeKml(
            MultipartFile file,
            String nombre,
            String nivelRiesgo,
            String descripcion,
            String fuenteDatos,
            Integer idTipoVegetacion
        );
    
    ZonaRiesgo actualizarDesdeKml(
            Integer id,
            MultipartFile file,
            String nombre,
            String nivelRiesgo,
            String descripcion,
            String fuenteDatos,
            Integer idTipoVegetacion
        );

        ZonaRiesgo actualizarConGeometriaWkt(
            Integer id,
            String wkt,
            String nombre,
            String nivelRiesgo,
            String descripcion,
            String fuenteDatos,
            Integer idTipoVegetacion
        );
        
    
}