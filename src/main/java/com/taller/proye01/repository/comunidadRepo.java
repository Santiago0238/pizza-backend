package com.taller.proye01.repository;


import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.taller.proye01.model.Comunidad;

public interface comunidadRepo extends JpaRepository<Comunidad, Integer> {

	 @Query("""
		        SELECT c
		        FROM Comunidad c
		        LEFT JOIN FETCH c.zonaRiesgo zr
		        WHERE LOWER(zr.nivelRiesgo) = LOWER(:nivelRiesgo)
		        ORDER BY c.id DESC
		    """)
		    List<Comunidad> filtrarPorNivelRiesgo(
		            @Param("nivelRiesgo") String nivelRiesgo
		    );    
}