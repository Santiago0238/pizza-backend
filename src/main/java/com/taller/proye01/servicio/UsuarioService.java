package com.taller.proye01.servicio;

import java.util.List;

import com.taller.proye01.model.Usuario;

public interface UsuarioService {
	 List<Usuario> listarUsuarios();

	Usuario buscarPorId(Integer id);

	List<Usuario> buscarPorNombre(String nombre);

	List<Usuario> buscarPorCorreo(String correo);

	List<Usuario> buscarPorRol(Integer idRol);

	List<Usuario> buscarPorEstado(Boolean activo);

	Usuario registrarUsuario(Usuario usuario);

	Usuario modificarUsuario(Integer id, Usuario usuario);

	void eliminarUsuario(Integer id);
}
