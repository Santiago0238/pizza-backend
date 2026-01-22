package com.taller.proye01.repositoryPizza;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.taller.proye01.modelPizza.RolMeModel;
import com.taller.proye01.modelPizza.RolMePK;


@Repository
public interface RolMeRepo extends JpaRepository<RolMeModel, RolMePK> {

    @Query(value = "SELECT * FROM rolMe WHERE idRol = :idrol", nativeQuery = true)
    List<RolMeModel> menusPorRol(@Param("idrol") Integer idRol);

    @Query(value = "SELECT * FROM rolMe WHERE idMenu = :idm", nativeQuery = true)
    List<RolMeModel> rolesPorMenu(@Param("idm") Integer idMenu);
}