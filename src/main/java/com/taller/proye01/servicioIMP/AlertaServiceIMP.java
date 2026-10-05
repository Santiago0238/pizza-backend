
package com.taller.proye01.servicioIMP;

import java.sql.Timestamp;
import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.taller.proye01.model.Alerta;
import com.taller.proye01.repository.AlertaRepo;
import com.taller.proye01.servicio.AlertaService;

@Service
public class AlertaServiceIMP implements AlertaService {

	private final AlertaRepo alertaRepo;

	public AlertaServiceIMP(AlertaRepo alertaRepo) {
		this.alertaRepo = alertaRepo;
	}

	// =========================================================
	// LISTAR TODAS LAS ALERTAS
	// =========================================================

	@Override
	@Transactional(readOnly = true)
	public List<Alerta> listar() {
		return alertaRepo.findAll();
	}

	// =========================================================
	// OBTENER ALERTA POR ID
	// =========================================================

	@Override
	@Transactional(readOnly = true)
	public Alerta obtenerPorId(Integer id) {

		if (id == null) {
			throw new RuntimeException("El ID de la alerta es obligatorio");
		}

		return alertaRepo.findById(id).orElseThrow(() -> new RuntimeException("Alerta no encontrada"));
	}

	// =========================================================
	// LISTAR ALERTAS ACTIVAS
	// =========================================================

	@Override
	@Transactional(readOnly = true)
	public List<Alerta> listarActivas() {
		return alertaRepo.listarActivas();
	}

	// =========================================================
	// LISTAR HISTORIAL
	// =========================================================

	@Override
	@Transactional(readOnly = true)
	public List<Alerta> listarHistorial() {
		return alertaRepo.listarHistorial();
	}

	// =========================================================
	// GUARDAR ALERTA
	// =========================================================

	@Override
	@Transactional
	public Alerta guardar(Alerta alerta) {

		if (alerta == null) {
			throw new RuntimeException("La alerta no puede ser nula");
		}

		// Validar nivel
		if (alerta.getNivel() == null || alerta.getNivel().isBlank()) {
			throw new RuntimeException("El nivel de la alerta es obligatorio");
		}

		// Validar mensaje
		if (alerta.getDescripcion() == null || alerta.getDescripcion().isBlank()) {
			throw new RuntimeException("El mensaje de la alerta es obligatorio");
		}

		if (alerta.getIncendio() != null && alerta.getIncendio().getId() == null) {

			alerta.setIncendio(null);
		}

		if (alerta.getUsuario() != null && alerta.getUsuario().getId() == null) {

			alerta.setUsuario(null);
		}

		// -----------------------------------------------------
		// Normalizar información
		// -----------------------------------------------------

		alerta.setNivel(alerta.getNivel().trim().toUpperCase());

		alerta.setDescripcion(alerta.getDescripcion().trim());

		// -----------------------------------------------------
		// Estado por defecto
		// -----------------------------------------------------

		if (alerta.getEstado() == null || alerta.getEstado().isBlank()) {

			alerta.setEstado("ACTIVA");

		} else {

			alerta.setEstado(alerta.getEstado().trim().toUpperCase());
		}

		// -----------------------------------------------------
		// Fecha de emisión
		// -----------------------------------------------------

		if (alerta.getFechaEmision() == null) {

			alerta.setFechaEmision(new Timestamp(System.currentTimeMillis()));
		}

		// -----------------------------------------------------
		// Una alerta nueva comienza sin fecha de cierre
		// -----------------------------------------------------

		alerta.setFechaCierre(null);

		return alertaRepo.save(alerta);
	}

	@Override
	@Transactional
	public Alerta actualizar(Integer id, Alerta alerta) {

		if (id == null) {
			throw new RuntimeException("El ID de la alerta es obligatorio");
		}

		if (alerta == null) {
			throw new RuntimeException("La información de la alerta es obligatoria");
		}

		Alerta alertaActual = alertaRepo.findById(id).orElseThrow(() -> new RuntimeException("Alerta no encontrada"));

		if (alerta.getNivel() == null || alerta.getNivel().isBlank()) {

			throw new RuntimeException("El nivel de la alerta es obligatorio");
		}

		if (alerta.getDescripcion() == null || alerta.getDescripcion().isBlank()) {

			throw new RuntimeException("El mensaje de la alerta es obligatorio");
		}

		alertaActual.setNivel(alerta.getNivel().trim().toUpperCase());

		alertaActual.setDescripcion(alerta.getDescripcion().trim());

		if (alerta.getEstado() != null && !alerta.getEstado().isBlank()) {

			String nuevoEstado = alerta.getEstado().trim().toUpperCase();

			alertaActual.setEstado(nuevoEstado);

			// Si vuelve a ACTIVA, quitar fecha de cierre
			if ("ACTIVA".equals(nuevoEstado)) {

				alertaActual.setFechaCierre(null);
			}

			if ("CERRADA".equals(nuevoEstado) && alertaActual.getFechaCierre() == null) {

				alertaActual.setFechaCierre(new Timestamp(System.currentTimeMillis()));
			}
		}

		if (alerta.getIncendio() != null && alerta.getIncendio().getId() != null) {

			alertaActual.setIncendio(alerta.getIncendio());
		}

		if (alerta.getUsuario() != null && alerta.getUsuario().getId() != null) {

			alertaActual.setUsuario(alerta.getUsuario());
		}

		return alertaRepo.save(alertaActual);
	}

	@Override
	@Transactional
	public Alerta cambiarEstado(Integer id, String estado) {

		if (id == null) {
			throw new RuntimeException("El ID de la alerta es obligatorio");
		}

		if (estado == null || estado.isBlank()) {
			throw new RuntimeException("El estado es obligatorio");
		}

		Alerta alerta = alertaRepo.findById(id).orElseThrow(() -> new RuntimeException("Alerta no encontrada"));

		String nuevoEstado = estado.trim().toUpperCase();

		// -----------------------------------------------------
		// Validar estados permitidos
		// -----------------------------------------------------

		if (!nuevoEstado.equals("ACTIVA") && !nuevoEstado.equals("EN_PROCESO") && !nuevoEstado.equals("CERRADA")) {

			throw new RuntimeException("Estado no válido. Estados permitidos: " + "ACTIVA, EN_PROCESO, CERRADA");
		}

		alerta.setEstado(nuevoEstado);

		// -----------------------------------------------------
		// Si está activa o en proceso
		// no debe tener fecha de cierre
		// -----------------------------------------------------

		if ("ACTIVA".equals(nuevoEstado) || "EN_PROCESO".equals(nuevoEstado)) {

			alerta.setFechaCierre(null);
		}

		// -----------------------------------------------------
		// Si se cierra, registrar fecha
		// -----------------------------------------------------

		if ("CERRADA".equals(nuevoEstado) && alerta.getFechaCierre() == null) {

			alerta.setFechaCierre(new Timestamp(System.currentTimeMillis()));
		}

		return alertaRepo.save(alerta);
	}

	// =========================================================
	// CERRAR ALERTA
	// =========================================================

	@Override
	@Transactional
	public Alerta cerrar(Integer id) {

		if (id == null) {
			throw new RuntimeException("El ID de la alerta es obligatorio");
		}

		Alerta alerta = alertaRepo.findById(id).orElseThrow(() -> new RuntimeException("Alerta no encontrada"));

		alerta.setEstado("CERRADA");

		// Solo establecer fecha si todavía no existe
		if (alerta.getFechaCierre() == null) {

			alerta.setFechaCierre(new Timestamp(System.currentTimeMillis()));
		}

		return alertaRepo.save(alerta);
	}

	// =========================================================
	// ELIMINAR ALERTA
	// =========================================================

	@Override
	@Transactional
	public void eliminar(Integer id) {

		if (id == null) {
			throw new RuntimeException("El ID de la alerta es obligatorio");
		}

		Alerta alerta = alertaRepo.findById(id).orElseThrow(() -> new RuntimeException("Alerta no encontrada"));

		alertaRepo.delete(alerta);
	}

	// =========================================================
	// BUSCAR POR NIVEL / SEVERIDAD
	// =========================================================

	@Override
	@Transactional(readOnly = true)
	public List<Alerta> buscarPorSeveridad(String severidad) {

		if (severidad == null || severidad.isBlank()) {
			return alertaRepo.findAll();
		}

		return alertaRepo.findByNivelIgnoreCase(severidad.trim());
	}

	// =========================================================
	// BUSCAR POR ESTADO
	// =========================================================

	@Override
	@Transactional(readOnly = true)
	public List<Alerta> buscarPorEstado(String estado) {

		if (estado == null || estado.isBlank()) {
			return alertaRepo.findAll();
		}

		return alertaRepo.findByEstadoIgnoreCase(estado.trim());
	}

	// =========================================================
	// BUSCAR POR TEXTO
	// =========================================================

	@Override
	@Transactional(readOnly = true)
	public List<Alerta> buscarPorTexto(String texto) {

		if (texto == null || texto.isBlank()) {
			return alertaRepo.findAll();
		}

		return alertaRepo.buscarPorTexto(texto.trim());
	}

}
