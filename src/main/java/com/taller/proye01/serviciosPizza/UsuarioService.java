package com.taller.proye01.serviciosPizza;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.taller.proye01.modelPizza.UsuarioModel;
import com.taller.proye01.repositoryPizza.UsuarioRepo;

@Service
public class UsuarioService {

    @Autowired
    private UsuarioRepo usuarioRepo;

    public List<UsuarioModel> listar() {
        return usuarioRepo.findAll();
    }

    public UsuarioModel guardar(UsuarioModel usuario) {
        return usuarioRepo.save(usuario);
    }

    public UsuarioModel buscarPorId(Integer id) {
        return usuarioRepo.findById(id).orElse(null);
    }

    public List<UsuarioModel> buscarPorNombreUsuario(String nombre) {
        return usuarioRepo.findByNombreUsuarioContainingIgnoreCase(nombre);
    }

    public List<UsuarioModel> buscarPorCorreo(String correo) {
        return usuarioRepo.findByCorreoContainingIgnoreCase(correo);
    }

    public List<UsuarioModel> activos() {
        return usuarioRepo.usuariosActivos();
    }

    public UsuarioModel loginBasico(String nombreUsuario) {
        // Solo devuelve el usuario; no compara contraseña
        return usuarioRepo.buscarPorNombre(nombreUsuario);
    }

    public boolean eliminar(Integer id) {
        if (usuarioRepo.existsById(id)) {
            usuarioRepo.deleteById(id);
            return true;
        }
        return false;
    }
}