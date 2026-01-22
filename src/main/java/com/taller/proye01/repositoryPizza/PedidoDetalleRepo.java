package com.taller.proye01.repositoryPizza;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.taller.proye01.modelPizza.PedidoDetalleModel;


@Repository
public interface PedidoDetalleRepo extends JpaRepository<PedidoDetalleModel, Integer> {

    @Query(value = "SELECT * FROM pedidoDetalle WHERE idPedido = :idp", nativeQuery = true)
    List<PedidoDetalleModel> detallesPorPedido(@Param("idp") Integer idPedido);

    @Query(value = "SELECT * FROM pedidoDetalle WHERE idProducto = :idprod", nativeQuery = true)
    List<PedidoDetalleModel> detallesPorProducto(@Param("idprod") Integer idProducto);
}