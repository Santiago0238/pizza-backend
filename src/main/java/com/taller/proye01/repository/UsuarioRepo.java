package com.taller.proye01.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.taller.proye01.model.Usuario;

@Repository
public interface UsuarioRepo extends JpaRepository<Usuario, Integer> {

    @Query(value = "SELECT * FROM usuario WHERE activo = true", nativeQuery = true)
    List<Usuario> usuariosActivos();

    @Query(value = "SELECT * FROM usuario WHERE correo = :correo", nativeQuery = true)
    Usuario buscarPorCorreo(@Param("correo") String correo);

    @Query(value = "SELECT * FROM usuario WHERE id_rol = :idr", nativeQuery = true)
    List<Usuario> usuariosPorRol(@Param("idr") Integer idRol);

    Optional<Usuario> findByCorreoIgnoreCase(String correo);

    boolean existsByCorreoIgnoreCase(String correo);

    List<Usuario> findByNombreContainingIgnoreCase(String nombre);

    List<Usuario> findByCorreoContainingIgnoreCase(String correo);

    List<Usuario> findByActivo(Boolean activo);

    @Query(value = "SELECT * FROM usuario WHERE id_rol = :idRol", nativeQuery = true)
    List<Usuario> buscarPorRol(Integer idRol);

    @Query(value = "SELECT COUNT(*) FROM usuario WHERE id_rol = :idRol", nativeQuery = true)
    Long contarPorRol(Integer idRol);
    
}