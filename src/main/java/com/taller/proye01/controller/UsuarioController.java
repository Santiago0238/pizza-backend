package com.taller.proye01.controller;


import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import com.taller.proye01.model.Usuario;
import com.taller.proye01.servicio.UsuarioService;



@RestController
@RequestMapping("/api/usuarios")
@CrossOrigin("*")
public class UsuarioController {

    @Autowired
    private UsuarioService usuarioService;

    @GetMapping()
    public List<Usuario> listar() {
        return usuarioService.listarUsuarios();
    }

    

    @GetMapping("/{id}")
    public Usuario buscarPorId(
            @PathVariable Integer id) {

        return usuarioService.buscarPorId(id);
    }

    @GetMapping("/nombre/{nombre}")
    public List<Usuario> buscarPorNombre(
            @PathVariable String nombre) {

        return usuarioService.buscarPorNombre(nombre);
    }

    @GetMapping("/correo/{correo}")
    public List<Usuario> buscarPorCorreo(
            @PathVariable String correo) {

        return usuarioService.buscarPorCorreo(correo);
    }


    @GetMapping("/rol/{idRol}")
    public List<Usuario> buscarPorRol(
            @PathVariable Integer idRol) {

        return usuarioService.buscarPorRol(idRol);
    }


    @GetMapping("/estado/{activo}")
    public List<Usuario> buscarPorEstado(
            @PathVariable Boolean activo) {

        return usuarioService.buscarPorEstado(activo);
    }


    @PostMapping
    public Usuario registrarUsuario(
            @RequestBody Usuario usuario) {
        return usuarioService.registrarUsuario(usuario);
    }

  
    @PutMapping("/{id}")
    public Usuario modificarUsuario(
            @PathVariable Integer id,
            @RequestBody Usuario usuario) {
   

        return usuarioService.modificarUsuario(id, usuario);
    }

  
    
    @DeleteMapping("/{id}")
    public String eliminarUsuario( @PathVariable Integer id) {
        usuarioService.eliminarUsuario(id);
        return "Usuario eliminado correctamente";
    }

}