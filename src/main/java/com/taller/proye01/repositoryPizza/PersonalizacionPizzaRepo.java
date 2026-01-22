package com.taller.proye01.repositoryPizza;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.taller.proye01.modelPizza.PersonalizacionPizzaModel;


@Repository
public interface PersonalizacionPizzaRepo extends JpaRepository<PersonalizacionPizzaModel, Integer> {

    @Query(value = "SELECT * FROM personalizacionPizza WHERE idProducto = :idp", nativeQuery = true)
    List<PersonalizacionPizzaModel> personalizacionesPorProducto(@Param("idp") Integer idProducto);

    List<PersonalizacionPizzaModel> findByTipo(String tipo);

    List<PersonalizacionPizzaModel> findByTamano(String tamano);
}