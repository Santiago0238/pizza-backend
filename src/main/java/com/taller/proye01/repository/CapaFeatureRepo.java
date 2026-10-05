package com.taller.proye01.repository;

import java.util.List;

import org.locationtech.jts.geom.Geometry;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.taller.proye01.model.CapaCaracteristica;

@Repository
public interface CapaFeatureRepo extends JpaRepository<CapaCaracteristica, Integer> {

	@Query(value = """
	        SELECT *
	        FROM capa_caracteristica
	        WHERE id_capa = :idc
	        ORDER BY id ASC
	        """, nativeQuery = true)
	    List<CapaCaracteristica> featuresPorCapa(@Param("idc") Integer idCapa);

	    @Query(value = """
	        SELECT *
	        FROM capa_caracteristica
	        WHERE ST_Intersects(
	            geometria,
	            ST_SetSRID(ST_GeomFromText(:wkt), 4326)
	        )
	        ORDER BY id ASC
	        """, nativeQuery = true)
	    List<CapaCaracteristica> featuresIntersectados(@Param("wkt") String wkt);

	    @Query(value = """
	        SELECT *
	        FROM capa_caracteristica
	        WHERE id_tipo_vegetacion = :idTipoVegetacion
	        ORDER BY id ASC
	        """, nativeQuery = true)
	    List<CapaCaracteristica> featuresPorTipoVegetacion(
	            @Param("idTipoVegetacion") Integer idTipoVegetacion
	    );

	    void deleteByCapaId(Integer idCapa);
}