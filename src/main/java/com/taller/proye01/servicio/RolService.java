package com.taller.proye01.servicio;

import java.util.List;

import com.taller.proye01.model.Rol;

public interface Rolservice {
		public List<Rol> listar();
	    public Rol buscarPorId(Integer id);
	    public Rol guardar(Rol rol);
	    public Rol actualizar(Integer id, Rol rol) ;
	    public void eliminar(Integer id);
	
}
