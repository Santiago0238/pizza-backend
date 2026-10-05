package com.taller.proye01.servicioIMP;

import java.sql.Timestamp;
import java.util.List;

import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.taller.proye01.model.Rol;
import com.taller.proye01.model.Usuario;
import com.taller.proye01.repository.RolRepo;
import com.taller.proye01.repository.UsuarioRepo;
import com.taller.proye01.servicio.UsuarioService;

@Service
public class UsuarioServiceIMP implements UsuarioService {

    private final UsuarioRepo usuarioRepo;
    private final RolRepo rolRepo;
    private final PasswordEncoder passwordEncoder;

    public UsuarioServiceIMP(
            UsuarioRepo usuarioRepo,
            RolRepo rolRepo,
            PasswordEncoder passwordEncoder) {
        this.usuarioRepo = usuarioRepo;
        this.rolRepo = rolRepo;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Usuario> listarUsuarios() {
        return usuarioRepo.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Usuario buscarPorId(Integer id) {
        return usuarioRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));
    }

    @Override
    @Transactional(readOnly = true)
    public List<Usuario> buscarPorNombre(String nombre) {
        return usuarioRepo.findByNombreContainingIgnoreCase(nombre);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Usuario> buscarPorCorreo(String correo) {
        return usuarioRepo.findByCorreoContainingIgnoreCase(correo);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Usuario> buscarPorRol(Integer idRol) {
        return usuarioRepo.buscarPorRol(idRol);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Usuario> buscarPorEstado(Boolean activo) {
        return usuarioRepo.findByActivo(activo);
    }

    @Override
    @Transactional
    public Usuario registrarUsuario(Usuario usuario) {

        String correoNormalizado = usuario.getCorreo().trim().toLowerCase();

        if (usuarioRepo.existsByCorreoIgnoreCase(correoNormalizado)) {
            throw new RuntimeException("Ya existe un usuario registrado con ese correo electrónico");
        }

        Rol rol = rolRepo.findById(usuario.getRol().getId())
                .orElseThrow(() -> new RuntimeException("Rol no encontrado"));

        usuario.setNombre(usuario.getNombre().trim());
        usuario.setFechaCreacion(new Timestamp(System.currentTimeMillis()));
        usuario.setCorreo(correoNormalizado);
        usuario.setContra(passwordEncoder.encode(usuario.getContra()));
        usuario.setRol(rol);

        if (usuario.getActivo() == null) {
            usuario.setActivo(true);
        }

        return usuarioRepo.save(usuario);
    }

    @Override
    @Transactional
    public Usuario modificarUsuario(Integer id, Usuario usuario) {

        Usuario usuarioActual = usuarioRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        String correoNormalizado = usuario.getCorreo().trim().toLowerCase();

        Usuario correoExistente = usuarioRepo.findByCorreoIgnoreCase(correoNormalizado)
                .orElse(null);

        if (correoExistente != null && !correoExistente.getId().equals(id)) {
            throw new RuntimeException("Ya existe un usuario registrado con ese correo electrónico");
        }

        Rol rol = rolRepo.findById(usuario.getRol().getId())
                .orElseThrow(() -> new RuntimeException("Rol no encontrado"));

        usuarioActual.setNombre(usuario.getNombre().trim());
        usuarioActual.setCorreo(correoNormalizado);
        usuarioActual.setRol(rol);

        if (usuario.getActivo() != null) {
            usuarioActual.setActivo(usuario.getActivo());
        }

        if (usuario.getContra() != null && !usuario.getContra().isBlank()) {
            usuarioActual.setContra(passwordEncoder.encode(usuario.getContra()));
        }

        return usuarioRepo.save(usuarioActual);
    }

    @Override
    @Transactional
    public void eliminarUsuario(Integer id) {

        Usuario usuario = usuarioRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        usuario.setActivo(false);

        usuarioRepo.save(usuario);
    }
}