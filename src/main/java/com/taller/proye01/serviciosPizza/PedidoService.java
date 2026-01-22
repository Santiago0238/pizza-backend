package com.taller.proye01.serviciosPizza;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.taller.proye01.modelPizza.PedidoModel;
import com.taller.proye01.repositoryPizza.PedidoRepo;

@Service
public class PedidoService {

    @Autowired
    private PedidoRepo pedidoRepo;

    public List<PedidoModel> listar() {
        return pedidoRepo.findAll();
    }

    public PedidoModel guardar(PedidoModel pedido) {
        return pedidoRepo.save(pedido);
    }

    public PedidoModel buscarPorId(Integer id) {
        return pedidoRepo.findById(id).orElse(null);
    }

    public List<PedidoModel> listarPorCliente(Integer idCliente) {
        return pedidoRepo.pedidosPorCliente(idCliente);
    }

    public List<PedidoModel> listarPorEstado(String estado) {
        return pedidoRepo.pedidosPorEstado(estado);
    }

    public List<PedidoModel> listarPorFecha(Date fecha) {
        return pedidoRepo.pedidosPorFecha(fecha);
    }

    public boolean eliminar(Integer id) {
        if (pedidoRepo.existsById(id)) {
            pedidoRepo.deleteById(id);
            return true;
        }
        return false;
    }
}