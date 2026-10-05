package com.taller.proye01.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.taller.proye01.model.PuntoRuta;

@Repository
public interface PuntoRutaRepo extends JpaRepository<PuntoRuta, Integer> {

	List<PuntoRuta> findByRutaId(Integer rutaId);

	void deleteByRutaId(Integer rutaId);

	List<PuntoRuta> findByRutaIdOrderByOrdenAsc(Integer rutaId);

}