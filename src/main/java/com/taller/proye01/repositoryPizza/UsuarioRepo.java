package com.taller.proye01.repositoryPizza;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.taller.proye01.modelPizza.UsuarioModel;



@Repository
public interface UsuarioRepo extends JpaRepository<UsuarioModel, Integer> {

    @Query(value = "SELECT * FROM usuario WHERE estado = 1", nativeQuery = true)
    List<UsuarioModel> usuariosActivos();

    @Query(value = "SELECT * FROM usuario WHERE nombreUsuario = :nom", nativeQuery = true)
    UsuarioModel buscarPorNombre(@Param("nom") String nombreUsuario);

    List<UsuarioModel> findByNombreUsuarioContainingIgnoreCase(String nombre);

    List<UsuarioModel> findByCorreoContainingIgnoreCase(String correo);
}