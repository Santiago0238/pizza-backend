package com.taller.proye01.serviciosPizza;


import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.taller.proye01.modelPizza.Devolucion;
import com.taller.proye01.repositoryPizza.DevolucionRepo;



@Service
public class DevolucionService {

    @Autowired
    private DevolucionRepo devolucionRepo;

  
    public List<Devolucion> listar() {
        return devolucionRepo.findAll();
    }

    // Guardar / registrar devolución (lo usará el modal “Guardar devolución”)
    public Devolucion guardar(Devolucion devolucion) {
        return devolucionRepo.save(devolucion);
    }

    // Buscar una devolución por su id
    public Devolucion buscarPorId(Integer id) {
        return devolucionRepo.findById(id).orElse(null);
    }

    // Listar devoluciones por idVenta (todas las devoluciones de una venta)
    public List<Devolucion> listarPorVenta(Integer idVenta) {
        return devolucionRepo.findByVentaIdVenta(idVenta);
    }

    // Eliminar registro de devolución
    public boolean eliminar(Integer id) {
        if (devolucionRepo.existsById(id)) {
            devolucionRepo.deleteById(id);
            return true;
        }
        return false;
    }

	public Devolucion registrarDevolucion(Devolucion request) {
		// TODO Auto-generated method stub
		return null;
	}
}
