package com.taller.proye01.repositoryPizza;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.taller.proye01.modelPizza.ProductoModel;



@Repository
public interface ProductoRepo extends JpaRepository<ProductoModel, Integer> {

    @Query(value = "SELECT * FROM producto WHERE idCategoria = :idc", nativeQuery = true)
    List<ProductoModel> productosPorCategoria(@Param("idc") Integer idCategoria);

    @Query(value = "SELECT * FROM producto WHERE idProveedor = :idp", nativeQuery = true)
    List<ProductoModel> productosPorProveedor(@Param("idp") Integer idProveedor);

    @Query(value = "SELECT * FROM producto WHERE disponible = 1", nativeQuery = true)
    List<ProductoModel> productosDisponibles();

    List<ProductoModel> findByNombreContainingIgnoreCase(String nombre);
}