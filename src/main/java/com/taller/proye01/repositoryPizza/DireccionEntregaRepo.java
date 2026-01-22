package com.taller.proye01.repositoryPizza;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.taller.proye01.modelPizza.DireccionEntregaModel;


@Repository
public interface DireccionEntregaRepo extends JpaRepository<DireccionEntregaModel, Integer> {

    @Query(value = "SELECT * FROM direccionEntrega WHERE idCliente = :idc", nativeQuery = true)
    List<DireccionEntregaModel> direccionesPorCliente(@Param("idc") Integer idc);
}