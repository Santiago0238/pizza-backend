package com.taller.proye01.repositoryPizza;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.taller.proye01.modelPizza.HistorialComprasModel;



@Repository
public interface HistorialComprasRepo extends JpaRepository<HistorialComprasModel, Integer> {

    @Query(value = "SELECT * FROM historialCompras WHERE idCliente = :idc", nativeQuery = true)
    List<HistorialComprasModel> historialPorCliente(@Param("idc") Integer idc);

    @Query(value = "SELECT * FROM historialCompras WHERE idPedido = :idp", nativeQuery = true)
    HistorialComprasModel historialPorPedido(@Param("idp") Integer idp);
    
    
}