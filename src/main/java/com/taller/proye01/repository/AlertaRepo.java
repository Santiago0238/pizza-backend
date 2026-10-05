package com.taller.proye01.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.taller.proye01.model.Alerta;

@Repository
public interface AlertaRepo extends JpaRepository<Alerta, Integer> {

	List<Alerta> findByEstadoIgnoreCase(String estado);

	
	List<Alerta> findByNivelIgnoreCase(String nivel);

	
	@Query("""
			    SELECT a
			    FROM Alerta a
			    WHERE LOWER(a.descripcion) LIKE LOWER(CONCAT('%', :texto, '%'))
			""")
	List<Alerta> buscarPorTexto(@Param("texto") String texto);

	
	@Query(" SELECT a  FROM Alerta a	    WHERE LOWER(a.estado) = LOWER('CERRADA')	    ORDER BY a.fechaCierre DESC	")
	List<Alerta> listarHistorial();


	@Query("""
			    SELECT a
			    FROM Alerta a
			    WHERE LOWER(a.estado) = LOWER('ACTIVA')
			    ORDER BY a.fechaEmision DESC
			""")
	List<Alerta> listarActivas();
}