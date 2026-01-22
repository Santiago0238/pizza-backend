package com.taller.proye01.repositoryPizza;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.taller.proye01.modelPizza.ReclamoModel;


@Repository
public interface ReclamoRepo extends JpaRepository<ReclamoModel, Integer> {

    @Query(value = "SELECT * FROM reclamo WHERE idPedido = :idp", nativeQuery = true)
    List<ReclamoModel> reclamosPorPedido(@Param("idp") Integer idPedido);

    @Query(value = "SELECT * FROM reclamo WHERE idUsuario = :idu", nativeQuery = true)
    List<ReclamoModel> reclamosPorUsuario(@Param("idu") Integer idUsuario);

    @Query(value = "SELECT * FROM reclamo WHERE fecha = :fec", nativeQuery = true)
    List<ReclamoModel> reclamosPorFecha(@Param("fec") Date fecha);
}