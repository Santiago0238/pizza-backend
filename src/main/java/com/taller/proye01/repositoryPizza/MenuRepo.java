package com.taller.proye01.repositoryPizza;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import com.taller.proye01.modelPizza.MenuModel;



@Repository
public interface MenuRepo extends JpaRepository<MenuModel, Integer> {

    @Query(value = "SELECT * FROM menu ORDER BY nombre ASC", nativeQuery = true)
    List<MenuModel> listarOrdenado();

    List<MenuModel> findByEstado(Integer estado);
}