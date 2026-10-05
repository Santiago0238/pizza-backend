package com.taller.proye01.servicioIMP;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.taller.proye01.model.TipoVegetacion;
import com.taller.proye01.repository.TipoVegetacionRepo;
import com.taller.proye01.servicio.tipoVegetacionService;

@Service
public class TipoVegetacionServiceIMP implements tipoVegetacionService {

	private final TipoVegetacionRepo tipoVegetacionRepo;

	public TipoVegetacionServiceIMP(TipoVegetacionRepo tipoVegetacionRepo) {

		this.tipoVegetacionRepo = tipoVegetacionRepo;
	}

	

	@Override
	@Transactional(readOnly = true)
	public List<TipoVegetacion> listar() {

		return tipoVegetacionRepo.findAll();
	}

	

	@Override
	@Transactional(readOnly = true)
	public TipoVegetacion obtenerPorId(Integer id) {

		return tipoVegetacionRepo.findById(id)
				.orElseThrow(() -> new RuntimeException("Tipo de vegetación no encontrado"));
	}

	

	@Override
	@Transactional
	public TipoVegetacion guardar(TipoVegetacion tipoVegetacion) {

		if (tipoVegetacion == null) {
			throw new RuntimeException("Los datos del tipo de vegetación son obligatorios");
		}

		if (tipoVegetacion.getNombre() == null || tipoVegetacion.getNombre().isBlank()) {

			throw new RuntimeException("El nombre del tipo de vegetación es obligatorio");
		}

		String nombreNormalizado = tipoVegetacion.getNombre().trim().toUpperCase();

		
		List<TipoVegetacion> existentes = tipoVegetacionRepo.findByNombreContainingIgnoreCase(nombreNormalizado);

		for (TipoVegetacion existente : existentes) {

			if (existente.getNombre().trim().equalsIgnoreCase(nombreNormalizado)) {

				throw new RuntimeException("Ya existe un tipo de vegetación con ese nombre");
			}
		}

		tipoVegetacion.setNombre(nombreNormalizado);

		return tipoVegetacionRepo.save(tipoVegetacion);
	}



	@Override
	@Transactional
	public TipoVegetacion actualizar(Integer id, TipoVegetacion tipoVegetacion) {

		TipoVegetacion existente = tipoVegetacionRepo.findById(id)
				.orElseThrow(() -> new RuntimeException("Tipo de vegetación no encontrado"));

		if (tipoVegetacion == null || tipoVegetacion.getNombre() == null || tipoVegetacion.getNombre().isBlank()) {

			throw new RuntimeException("El nombre del tipo de vegetación es obligatorio");
		}

		String nombreNormalizado = tipoVegetacion.getNombre().trim().toUpperCase();

	
		List<TipoVegetacion> encontrados = tipoVegetacionRepo.findByNombreContainingIgnoreCase(nombreNormalizado);

		for (TipoVegetacion otro : encontrados) {

			if (!otro.getId().equals(id) && otro.getNombre().trim().equalsIgnoreCase(nombreNormalizado)) {

				throw new RuntimeException("Ya existe otro tipo de vegetación con ese nombre");
			}
		}

		existente.setNombre(nombreNormalizado);

		return tipoVegetacionRepo.save(existente);
	}

	

	@Override
	@Transactional
	public void eliminar(Integer id) {

		TipoVegetacion existente = tipoVegetacionRepo.findById(id)
				.orElseThrow(() -> new RuntimeException("Tipo de vegetación no encontrado"));

		tipoVegetacionRepo.delete(existente);
	}

	

	@Override
	@Transactional(readOnly = true)
	public List<TipoVegetacion> buscarPorNombre(String nombre) {

		if (nombre == null || nombre.isBlank()) {

			return tipoVegetacionRepo.findAll();
		}

		return tipoVegetacionRepo.findByNombreContainingIgnoreCase(nombre.trim());
	}
}