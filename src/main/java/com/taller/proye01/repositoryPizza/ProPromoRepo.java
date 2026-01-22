package com.taller.proye01.repositoryPizza;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.taller.proye01.modelPizza.ProPromoModel;
import com.taller.proye01.modelPizza.ProPromoPK;


@Repository
public interface ProPromoRepo extends JpaRepository<ProPromoModel, ProPromoPK> {

    @Query(value = "SELECT * FROM proPromo WHERE idPromocion = :idp", nativeQuery = true)
    List<ProPromoModel> productosPorPromocion(@Param("idp") Integer idPromocion);

    @Query(value = "SELECT * FROM proPromo WHERE idProducto = :idprod", nativeQuery = true)
    List<ProPromoModel> promocionesPorProducto(@Param("idprod") Integer idProducto);
}