package com.taller.proye01.serviciosPizza;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.taller.proye01.modelPizza.TransaccionModel;
import com.taller.proye01.repositoryPizza.TransaccionRepo;

@Service
public class TransaccionService {

    @Autowired
    private TransaccionRepo transaccionRepo;

    public List<TransaccionModel> listar() {
        return transaccionRepo.findAll();
    }

    public TransaccionModel guardar(TransaccionModel transaccion) {
        return transaccionRepo.save(transaccion);
    }

    public TransaccionModel buscarPorId(Integer id) {
        return transaccionRepo.findById(id).orElse(null);
    }

    public List<TransaccionModel> listarPorPedido(Integer idPedido) {
        return transaccionRepo.transaccionesPorPedido(idPedido);
    }

    public List<TransaccionModel> listarPorUsuario(Integer idUsuario) {
        return transaccionRepo.transaccionesPorUsuario(idUsuario);
    }

    public List<TransaccionModel> listarPorFecha(Date fecha) {
        return transaccionRepo.transaccionesPorFecha(fecha);
    }

    public boolean eliminar(Integer id) {
        if (transaccionRepo.existsById(id)) {
            transaccionRepo.deleteById(id);
            return true;
        }
        return false;
    }
}