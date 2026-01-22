package com.taller.proye01.serviciosPizza;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.taller.proye01.modelPizza.VentaModel;
import com.taller.proye01.repositoryPizza.VentaRepo;

@Service
public class VentaService {

    @Autowired
    private VentaRepo ventaRepo;

    public List<VentaModel> listar() {
        return ventaRepo.findAll();
    }

    public VentaModel guardar(VentaModel venta) {
        return ventaRepo.save(venta);
    }

    public VentaModel buscarPorId(Integer id) {
        return ventaRepo.findById(id).orElse(null);
    }

    public List<VentaModel> listarPorCliente(Integer idCliente) {
        return ventaRepo.ventasPorCliente(idCliente);
    }

    public List<VentaModel> listarPorUsuario(Integer idUsuario) {
        return ventaRepo.ventasPorUsuario(idUsuario);
    }

    public List<VentaModel> listarPorFecha(Date fecha) {
        return ventaRepo.ventasPorFecha(fecha);
    }

    public List<VentaModel> listarPorEstado(String estado) {
        return ventaRepo.ventasPorEstado(estado);
    }

    public boolean eliminar(Integer id) {
        if (ventaRepo.existsById(id)) {
            ventaRepo.deleteById(id);
            return true;
        }
        return false;
    }
}