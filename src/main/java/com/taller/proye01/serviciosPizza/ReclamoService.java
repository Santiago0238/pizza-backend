package com.taller.proye01.serviciosPizza;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.taller.proye01.modelPizza.ReclamoModel;
import com.taller.proye01.repositoryPizza.ReclamoRepo;

@Service
public class ReclamoService {

    @Autowired
    private ReclamoRepo reclamoRepo;

    public List<ReclamoModel> listar() {
        return reclamoRepo.findAll();
    }

    public ReclamoModel guardar(ReclamoModel reclamo) {
        return reclamoRepo.save(reclamo);
    }

    public ReclamoModel buscarPorId(Integer id) {
        return reclamoRepo.findById(id).orElse(null);
    }

    public List<ReclamoModel> listarPorPedido(Integer idPedido) {
        return reclamoRepo.reclamosPorPedido(idPedido);
    }

    public List<ReclamoModel> listarPorUsuario(Integer idUsuario) {
        return reclamoRepo.reclamosPorUsuario(idUsuario);
    }

    public List<ReclamoModel> listarPorFecha(Date fecha) {
        return reclamoRepo.reclamosPorFecha(fecha);
    }

    public boolean eliminar(Integer id) {
        if (reclamoRepo.existsById(id)) {
            reclamoRepo.deleteById(id);
            return true;
        }
        return false;
    }
}