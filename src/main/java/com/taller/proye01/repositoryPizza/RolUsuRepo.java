package com.taller.proye01.repositoryPizza;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.taller.proye01.modelPizza.RolUsuModel;
import com.taller.proye01.modelPizza.RolUsuPK;


@Repository
public interface RolUsuRepo extends JpaRepository<RolUsuModel, RolUsuPK> {

    @Query(value = "SELECT * FROM rolUsu WHERE idUsuario = :idu", nativeQuery = true)
    List<RolUsuModel> rolesPorUsuario(@Param("idu") Integer idUsuario);

    @Query(value = "SELECT * FROM rolUsu WHERE idPol = :idrol", nativeQuery = true)
    List<RolUsuModel> usuariosPorRol(@Param("idrol") Integer idRol);
}