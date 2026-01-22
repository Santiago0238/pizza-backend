package com.taller.proye01.serviciosPizza;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.taller.proye01.modelPizza.HistorialComprasModel;
import com.taller.proye01.repositoryPizza.HistorialComprasRepo;



@Service
public class HistorialComprasService {

    @Autowired
    private HistorialComprasRepo historialRepo;

    public List<HistorialComprasModel> listar() {
        return historialRepo.findAll();
    }

    public HistorialComprasModel guardar(HistorialComprasModel historial) {
        return historialRepo.save(historial);
    }

    public HistorialComprasModel buscarPorId(Integer id) {
        return historialRepo.findById(id).orElse(null);
    }

    public List<HistorialComprasModel> historialPorCliente(Integer idCliente) {
        return historialRepo.historialPorCliente(idCliente);
    }

    public HistorialComprasModel historialPorPedido(Integer idPedido) {
        return historialRepo.historialPorPedido(idPedido);
    }
}