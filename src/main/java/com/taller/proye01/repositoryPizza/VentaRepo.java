package com.taller.proye01.repositoryPizza;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.taller.proye01.modelPizza.VentaModel;



@Repository
public interface VentaRepo extends JpaRepository<VentaModel, Integer> {

    @Query(value = "SELECT * FROM venta WHERE idCliente = :idc", nativeQuery = true)
    List<VentaModel> ventasPorCliente(@Param("idc") Integer idCliente);

    @Query(value = "SELECT * FROM venta WHERE idUsuario = :idu", nativeQuery = true)
    List<VentaModel> ventasPorUsuario(@Param("idu") Integer idUsuario);

    @Query(value = "SELECT * FROM venta WHERE fecha = :fec", nativeQuery = true)
    List<VentaModel> ventasPorFecha(@Param("fec") Date fecha);

    @Query(value = "SELECT * FROM venta WHERE estado = :est", nativeQuery = true)
    List<VentaModel> ventasPorEstado(@Param("est") String estado);
    
    @Query("SELECT v FROM VentaModel v WHERE v.fecha BETWEEN :desde AND :hasta")
    List<VentaModel> ventasPorRangoFecha(
            @Param("desde") java.sql.Date desde,
            @Param("hasta") java.sql.Date hasta
    );
    
}