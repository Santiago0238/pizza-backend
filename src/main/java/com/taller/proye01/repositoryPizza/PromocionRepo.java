package com.taller.proye01.repositoryPizza;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.taller.proye01.modelPizza.PromocionModel;



@Repository
public interface PromocionRepo extends JpaRepository<PromocionModel, Integer> {

    @Query(value = "SELECT * FROM promocion WHERE estado = :est", nativeQuery = true)
    List<PromocionModel> promocionesPorEstado(@Param("est") String estado);

    @Query(value = "SELECT * FROM promocion WHERE fechaInicio <= :hoy AND fechaFin >= :hoy", nativeQuery = true)
    List<PromocionModel> promocionesVigentes(@Param("hoy") Date fecha);

    List<PromocionModel> findByNombreContainingIgnoreCase(String nombre);
}