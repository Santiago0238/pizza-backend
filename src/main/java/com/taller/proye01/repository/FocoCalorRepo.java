package com.taller.proye01.repository;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;

import org.locationtech.jts.geom.Point;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.taller.proye01.model.FocoCalor;

@Repository
public interface FocoCalorRepo extends JpaRepository<FocoCalor, Integer> {

    @Query(value = "SELECT * FROM foco_calor WHERE satelite = :sat", nativeQuery = true)
    List<FocoCalor> focosPorSatelite(@Param("sat") String satelite);

    @Query(value = """
        SELECT * FROM foco_calor
        WHERE confianza >= :conf
        """, nativeQuery = true)
    List<FocoCalor> focosPorConfianza(@Param("conf") String confianza);

    @Query(value = """
        SELECT * FROM foco_calor
        WHERE fecha BETWEEN :inicio AND :fin
        """, nativeQuery = true)
    List<FocoCalor> focosPorFechas(
            @Param("inicio") Timestamp inicio,
            @Param("fin") Timestamp fin
    );

    @Query(value = """
        SELECT * FROM foco_calor
        WHERE ST_DWithin(
            geom::geography,
            :punto::geography,
            :distancia
        )
        """, nativeQuery = true)
    List<FocoCalor> focosCercanos(
            @Param("punto") Point punto,
            @Param("distancia") Double distancia
    );
    
    @Query("""
            SELECT f
            FROM FocoCalor f
            LEFT JOIN FETCH f.incendio i
            ORDER BY f.fecha DESC
        """)
        List<FocoCalor> listarConIncendio();

        @Query("""
            SELECT f
            FROM FocoCalor f
            LEFT JOIN FETCH f.incendio i
            WHERE f.incendio IS NULL
            ORDER BY f.fecha DESC
        """)
        List<FocoCalor> listarSinIncendio();

        @Query("""
            SELECT f
            FROM FocoCalor f
            LEFT JOIN FETCH f.incendio i
            WHERE f.incendio.id = :idIncendio
            ORDER BY f.fecha DESC
        """)
        List<FocoCalor> listarPorIncendio(Integer idIncendio);

        @Query("""
            SELECT f
            FROM FocoCalor f
            LEFT JOIN FETCH f.incendio i
            WHERE f.fecha BETWEEN :desde AND :hasta
            ORDER BY f.fecha DESC
        """)
        List<FocoCalor> buscarPorRangoFechas(Timestamp desde, Timestamp hasta);

        @Query("""
            SELECT f
            FROM FocoCalor f
            LEFT JOIN FETCH f.incendio i
            WHERE LOWER(f.satelite) = LOWER(:satelite)
            ORDER BY f.fecha DESC
        """)
        List<FocoCalor> buscarPorSatelite(String satelite);

        @Query("""
            SELECT f
            FROM FocoCalor f
            LEFT JOIN FETCH f.incendio i
            WHERE LOWER(f.confianza) = LOWER(:confianza)
            ORDER BY f.fecha DESC
        """)
        List<FocoCalor> buscarPorConfianza(String confianza);

        @Query(value = """
            SELECT *
            FROM foco_calor f
            WHERE ST_Intersects(
                f.punto,
                ST_SetSRID(ST_GeomFromText(:wktArea), 4326)
            )
            ORDER BY f.fecha DESC
        """, nativeQuery = true)
        List<FocoCalor> buscarDentroDeArea(String wktArea);

        @Query(value = """
            SELECT *
            FROM foco_calor f
            WHERE ST_DWithin(
                f.punto::geography,
                ST_SetSRID(ST_MakePoint(:lon, :lat), 4326)::geography,
                :radioMetros
            )
            ORDER BY f.fecha DESC
        """, nativeQuery = true)
        List<FocoCalor> buscarCercanos(Double lat, Double lon, Double radioMetros);

        @Query(value = """
        	    SELECT *
        	    FROM foco_calor f
        	    WHERE f.fecha = :fecha
        	      AND LOWER(f.satelite) = LOWER(:satelite)
        	      AND LOWER(f.fuente) = LOWER(:fuente)
        	      AND ST_DWithin(
        	          f.punto::geography,
        	          ST_SetSRID(ST_MakePoint(:lon, :lat), 4326)::geography,
        	          30
        	      )
        	    LIMIT 1
        	""", nativeQuery = true)
        	Optional<FocoCalor> buscarPosibleDuplicado(
        	        Timestamp fecha,
        	        String satelite,
        	        String fuente,
        	        Double lat,
        	        Double lon
        	);
        
}