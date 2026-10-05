package com.taller.proye01.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.taller.proye01.model.TipoVegetacion;


public interface TipoVegetacionRepo extends JpaRepository<TipoVegetacion, Integer> {

	List<TipoVegetacion> findByNombreContainingIgnoreCase(
            String nombre
    );
	
}
