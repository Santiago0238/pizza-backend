package com.taller.proye01.serviciosPizza;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.taller.proye01.modelPizza.DireccionEntregaModel;
import com.taller.proye01.repositoryPizza.DireccionEntregaRepo;

@Service
public class DireccionEntregaService {

    @Autowired
    private DireccionEntregaRepo direccionRepo;

    public List<DireccionEntregaModel> listar() {
        return direccionRepo.findAll();
    }

    public DireccionEntregaModel guardar(DireccionEntregaModel direccion) {
        return direccionRepo.save(direccion);
    }

    public DireccionEntregaModel buscarPorId(Integer id) {
        return direccionRepo.findById(id).orElse(null);
    }

    public List<DireccionEntregaModel> listarPorCliente(Integer idCliente) {
        return direccionRepo.direccionesPorCliente(idCliente);
    }

    public boolean eliminar(Integer id) {
        if (direccionRepo.existsById(id)) {
            direccionRepo.deleteById(id);
            return true;
        }
        return false;
    }
}