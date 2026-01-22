package com.taller.proye01.repositoryPizza;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.taller.proye01.modelPizza.ProveedorModel;



@Repository
public interface ProveedorRepo extends JpaRepository<ProveedorModel, Integer> {

    @Query(value = "SELECT * FROM proveedor WHERE tipoProducto = :tipo", nativeQuery = true)
    List<ProveedorModel> proveedoresPorTipo(@Param("tipo") String tipoProducto);

    List<ProveedorModel> findByNombreContainingIgnoreCase(String nombre);
}