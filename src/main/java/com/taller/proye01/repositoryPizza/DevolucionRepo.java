package com.taller.proye01.repositoryPizza;



import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.taller.proye01.modelPizza.Devolucion;



@Repository
public interface DevolucionRepo extends JpaRepository<Devolucion, Integer> {

    @Query(value = "SELECT * FROM devolucion WHERE idVenta = :idv", nativeQuery = true)
    List<Devolucion> devolucionesPorVenta(@Param("idv") Integer idVenta);

    @Query(value = "SELECT * FROM devolucion WHERE idUsuario = :idu", nativeQuery = true)
    List<Devolucion> devolucionesPorUsuario(@Param("idu") Integer idUsuario);

   
    @Query(value = "SELECT * FROM devolucion WHERE fecha::date = :fec", nativeQuery = true)
    List<Devolucion> devolucionesPorFecha(@Param("fec") Date fecha);

	List<Devolucion> findByVentaIdVenta(Integer idVenta);
	
	 @Query("SELECT d FROM Devolucion d WHERE d.fecha BETWEEN :desde AND :hasta")
	    List<Devolucion> devolucionesPorRangoFecha(
	            @Param("desde") java.sql.Date desde,
	            @Param("hasta") java.sql.Date hasta
	    );
	 
}

