package com.taller.proye01.servicio;

import java.util.List;

import com.taller.proye01.model.TipoVegetacion;

public interface tipoVegetacionService {

	List<TipoVegetacion> listar();

	TipoVegetacion obtenerPorId(Integer id);

	TipoVegetacion guardar(TipoVegetacion tipoVegetacion);

	TipoVegetacion actualizar(Integer id, TipoVegetacion tipoVegetacion);

	void eliminar(Integer id);

	List<TipoVegetacion> buscarPorNombre(String nombre);
}
