package com.taller.proye01.repositoryPizza;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.taller.proye01.modelPizza.ClienteModel;


@Repository
public interface ClienteRepo extends JpaRepository<ClienteModel, Integer> {

    @Query(value = "SELECT * FROM cliente WHERE activo = 1", nativeQuery = true)
    List<ClienteModel> clientesActivos();

    @Query(value = "SELECT * FROM cliente WHERE idCliente = :idc", nativeQuery = true)
    ClienteModel buscarPorId(@Param("idc") Integer idc);

    List<ClienteModel> findByNombreContainingIgnoreCase(String nombre);
    
    @Query(value = "SELECT * FROM cliente WHERE telefono = :tel LIMIT 1", nativeQuery = true)
    ClienteModel buscarPorTelefono(@Param("tel") String telefono);

    
}