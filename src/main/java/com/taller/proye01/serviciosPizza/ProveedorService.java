package com.taller.proye01.serviciosPizza;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.taller.proye01.modelPizza.ProveedorModel;
import com.taller.proye01.repositoryPizza.ProveedorRepo;

@Service
public class ProveedorService {

    @Autowired
    private ProveedorRepo proveedorRepo;

    public List<ProveedorModel> listar() {
        return proveedorRepo.findAll();
    }

    public ProveedorModel guardar(ProveedorModel proveedor) {
        return proveedorRepo.save(proveedor);
    }

    public ProveedorModel buscarPorId(Integer id) {
        return proveedorRepo.findById(id).orElse(null);
    }

    public List<ProveedorModel> porTipo(String tipoProducto) {
        return proveedorRepo.proveedoresPorTipo(tipoProducto);
    }

    public List<ProveedorModel> buscarPorNombre(String nombre) {
        return proveedorRepo.findByNombreContainingIgnoreCase(nombre);
    }

    public boolean eliminar(Integer id) {
        if (proveedorRepo.existsById(id)) {
            proveedorRepo.deleteById(id);
            return true;
        }
        return false;
    }
}