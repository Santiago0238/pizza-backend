package com.taller.proye01.repositoryPizza;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.taller.proye01.modelPizza.PedidoModel;



@Repository
public interface PedidoRepo extends JpaRepository<PedidoModel, Integer> {

    @Query(value = "SELECT * FROM pedido WHERE idCliente = :idc", nativeQuery = true)
    List<PedidoModel> pedidosPorCliente(@Param("idc") Integer idc);

    @Query(value = "SELECT * FROM pedido WHERE estadoPedido = :estado", nativeQuery = true)
    List<PedidoModel> pedidosPorEstado(@Param("estado") String estado);

    @Query(value = "SELECT * FROM pedido WHERE DATE(fecha) = :fec", nativeQuery = true)
    List<PedidoModel> pedidosPorFecha(@Param("fec") Date fecha);
}