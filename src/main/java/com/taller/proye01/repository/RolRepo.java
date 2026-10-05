package com.taller.proye01.repository;
import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.taller.proye01.model.Rol;

@Repository
public interface RolRepo extends JpaRepository<Rol, Integer> {

    @Query(value = "SELECT * FROM rol WHERE nombre = :nom", nativeQuery = true)
    Rol buscarPorNombre(@Param("nom") String nombre);

    List<Rol> findByNombreContainingIgnoreCase(String nombre);
    Optional<Rol> findByNombreIgnoreCase(String nombre);

    boolean existsByNombreIgnoreCase(String nombre);
    
}