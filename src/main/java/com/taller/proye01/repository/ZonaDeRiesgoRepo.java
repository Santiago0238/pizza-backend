package com.taller.proye01.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.taller.proye01.model.ZonaRiesgo;

public interface ZonaDeRiesgoRepo extends JpaRepository<ZonaRiesgo, Integer> {

	List<ZonaRiesgo> findByNivelRiesgoIgnoreCase(String nivelRiesgo);

	boolean existsByNombreIgnoreCase(String nombre);

	@Query("""
			    SELECT z
			    FROM ZonaRiesgo z
			    WHERE LOWER(z.nombre) LIKE LOWER(CONCAT('%', :texto, '%'))
			       OR LOWER(z.descripcion) LIKE LOWER(CONCAT('%', :texto, '%'))
			       OR LOWER(z.fuenteDatos) LIKE LOWER(CONCAT('%', :texto, '%'))
			""")
	List<ZonaRiesgo> buscarPorTexto(@Param("texto") String texto);

}