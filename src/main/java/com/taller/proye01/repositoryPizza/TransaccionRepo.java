package com.taller.proye01.repositoryPizza;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.taller.proye01.modelPizza.TransaccionModel;



@Repository
public interface TransaccionRepo extends JpaRepository<TransaccionModel, Integer> {

    @Query(value = "SELECT * FROM transaccion WHERE idPedido = :idp", nativeQuery = true)
    List<TransaccionModel> transaccionesPorPedido(@Param("idp") Integer idPedido);

    @Query(value = "SELECT * FROM transaccion WHERE idUsuario = :idu", nativeQuery = true)
    List<TransaccionModel> transaccionesPorUsuario(@Param("idu") Integer idUsuario);

    @Query(value = "SELECT * FROM transaccion WHERE fecha = :fec", nativeQuery = true)
    List<TransaccionModel> transaccionesPorFecha(@Param("fec") Date fecha);
    
    @Query("SELECT t FROM TransaccionModel t WHERE t.fecha BETWEEN :desde AND :hasta")
    List<TransaccionModel> transaccionesPorRangoFecha(
            @Param("desde") java.sql.Date desde,
            @Param("hasta") java.sql.Date hasta
    );
    
}
