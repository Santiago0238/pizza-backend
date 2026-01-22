package com.taller.proye01.repositoryPizza;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.taller.proye01.modelPizza.CategoriaProductoModel;



@Repository
public interface CategoriaProductoRepo extends JpaRepository<CategoriaProductoModel, Integer> {

    @Query(value = "SELECT * FROM categoriaProducto ORDER BY nombreCategoria ASC", nativeQuery = true)
    List<CategoriaProductoModel> listarOrdenado();
}