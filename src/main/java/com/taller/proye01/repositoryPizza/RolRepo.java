package com.taller.proye01.repositoryPizza;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.taller.proye01.modelPizza.RolModel;



@Repository
public interface RolRepo extends JpaRepository<RolModel, Integer> {

    List<RolModel> findByNombreRolContainingIgnoreCase(String nombre);
}