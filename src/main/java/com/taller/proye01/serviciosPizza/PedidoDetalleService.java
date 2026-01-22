package com.taller.proye01.serviciosPizza;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.taller.proye01.modelPizza.PedidoDetalleModel;
import com.taller.proye01.repositoryPizza.PedidoDetalleRepo;

@Service
public class PedidoDetalleService {

    @Autowired
    private PedidoDetalleRepo pedidoDetalleRepo;

    public List<PedidoDetalleModel> listar() {
        return pedidoDetalleRepo.findAll();
    }

    public PedidoDetalleModel guardar(PedidoDetalleModel detalle) {
        return pedidoDetalleRepo.save(detalle);
    }

    public PedidoDetalleModel buscarPorId(Integer id) {
        return pedidoDetalleRepo.findById(id).orElse(null);
    }

    public List<PedidoDetalleModel> listarPorPedido(Integer idPedido) {
        return pedidoDetalleRepo.detallesPorPedido(idPedido);
    }

    public List<PedidoDetalleModel> listarPorProducto(Integer idProducto) {
        return pedidoDetalleRepo.detallesPorProducto(idProducto);
    }

    public boolean eliminar(Integer id) {
        if (pedidoDetalleRepo.existsById(id)) {
            pedidoDetalleRepo.deleteById(id);
            return true;
        }
        return false;
    }
}
