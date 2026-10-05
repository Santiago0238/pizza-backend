package com.taller.proye01.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.taller.proye01.model.Capa;



@Repository
public interface CapaRepo extends JpaRepository<Capa, Integer> {

	@Query(value = "SELECT * FROM capa WHERE publico = true ORDER BY id DESC", nativeQuery = true)
    List<Capa> capasPublicas();

    @Query(value = "SELECT * FROM capa WHERE tipo = :tip ORDER BY id DESC", nativeQuery = true)
    List<Capa> capasPorTipo(@Param("tip") String tipo);

    @Query(value = "SELECT * FROM capa WHERE id_usuario = :idu ORDER BY id DESC", nativeQuery = true)
    List<Capa> capasPorUsuario(@Param("idu") Integer idUsuario);

    List<Capa> findByNombreContainingIgnoreCase(String nombre);

    List<Capa> findAllByOrderByIdDesc();
}