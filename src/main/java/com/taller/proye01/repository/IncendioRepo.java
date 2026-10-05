package com.taller.proye01.repository;

import java.sql.Timestamp;
import java.util.List;

import org.locationtech.jts.geom.Point;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.taller.proye01.model.Incendio;

@Repository
public interface IncendioRepo extends JpaRepository<Incendio, Integer> {

	@Query(value = "SELECT * FROM incendio WHERE estado = :est", nativeQuery = true)
	List<Incendio> incendiosPorEstado(@Param("est") String estado);

	@Query(value = """
			SELECT * FROM incendio
			WHERE fecha_inicio BETWEEN :inicio AND :fin
			""", nativeQuery = true)
	List<Incendio> incendiosPorFechas(@Param("inicio") Timestamp inicio, @Param("fin") Timestamp fin);

	@Query(value = "SELECT * FROM incendio WHERE ST_Contains(geom, :punto)", nativeQuery = true)
	List<Incendio> incendiosQueContienenPunto(@Param("punto") Point punto);

	List<Incendio> findByNombreContainingIgnoreCase(String nombre);

	@Query(" SELECT i FROM Incendio i LEFT JOIN FETCH i.zonaRiesgo zr ORDER BY i.fechaRegistro DESC")
	List<Incendio> listarConZonaRiesgo();

	@Query(" SELECT i FROM Incendio i LEFT JOIN FETCH i.zonaRiesgo zr  WHERE i.activo = true  ORDER BY i.fechaInicio DESC	")
	List<Incendio> listarActivos();

	@Query(" SELECT i  FROM Incendio i LEFT JOIN FETCH i.zonaRiesgo zr  WHERE i.historico = true   ORDER BY i.fechaInicio DESC")
	List<Incendio> listarHistorial();

	@Query("""
			    SELECT i
			    FROM Incendio i
			    LEFT JOIN FETCH i.zonaRiesgo zr
			    WHERE LOWER(i.estado) = LOWER(:estado)
			    ORDER BY i.fechaInicio DESC
			""")
	List<Incendio> buscarPorEstado(String estado);

	@Query("""
			    SELECT i
			    FROM Incendio i
			    LEFT JOIN FETCH i.zonaRiesgo zr
			    WHERE LOWER(i.nivelGravedad) = LOWER(:nivelGravedad)
			    ORDER BY i.fechaInicio DESC
			""")
	List<Incendio> buscarPorNivelGravedad(String nivelGravedad);

	@Query("""
			    SELECT i
			    FROM Incendio i
			    LEFT JOIN FETCH i.zonaRiesgo zr
			    WHERE i.fechaInicio BETWEEN :desde AND :hasta
			    ORDER BY i.fechaInicio DESC
			""")
	List<Incendio> buscarPorRangoFechas(Timestamp desde, Timestamp hasta);

	@Query("""
			    SELECT i
			    FROM Incendio i
			    LEFT JOIN FETCH i.zonaRiesgo zr
			    WHERE LOWER(i.nombre) LIKE LOWER(CONCAT('%', :texto, '%'))
			       OR LOWER(i.causa) LIKE LOWER(CONCAT('%', :texto, '%'))
			       OR LOWER(i.origen) LIKE LOWER(CONCAT('%', :texto, '%'))
			       OR LOWER(i.estado) LIKE LOWER(CONCAT('%', :texto, '%'))
			    ORDER BY i.fechaRegistro DESC
			""")
	List<Incendio> buscarPorTexto(String texto);

	boolean existsByNombreIgnoreCase(String nombre);
}