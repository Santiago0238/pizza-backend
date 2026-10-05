package com.taller.proye01.repository;

import java.util.List;

import org.locationtech.jts.geom.Geometry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.taller.proye01.DTO.RutaDTO;
import com.taller.proye01.model.Ruta;

@Repository
public interface RutaRepo extends JpaRepository<Ruta, Integer> {


    
    @Query(value = """
            SELECT 
                id,
                nombre,
                descripcion,
                distancia_km AS distanciaKm,
                fecha_creacion AS fechaCreacion,
                usuario_id AS usuarioId,
                ST_AsText(ruta_linea) AS rutaLinea
            FROM ruta
            ORDER BY fecha_creacion DESC
            """, nativeQuery = true)
        List<RutaDTO> findAllSimplificado();

        @Query(value = """
            SELECT 
                id,
                nombre,
                descripcion,
                distancia_km AS distanciaKm,
                fecha_creacion AS fechaCreacion,
                usuario_id AS usuarioId,
                ST_AsText(ruta_linea) AS rutaLinea
            FROM ruta
            WHERE ST_Intersects(
                ruta_linea,
                ST_SetSRID(ST_GeomFromText(:wktArea), 4326)
            )
            ORDER BY fecha_creacion DESC
            """, nativeQuery = true)
        List<RutaDTO> filtrarPorAreaEspacial(String wktArea);
        
        
        
}