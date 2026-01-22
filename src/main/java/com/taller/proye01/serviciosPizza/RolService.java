package com.taller.proye01.serviciosPizza;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.taller.proye01.modelPizza.RolModel;
import com.taller.proye01.repositoryPizza.RolRepo;

@Service
public class RolService {

    @Autowired
    private RolRepo rolRepo;

    public List<RolModel> listar() {
        return rolRepo.findAll();
    }

    public RolModel guardar(RolModel rol) {
        return rolRepo.save(rol);
    }

    public RolModel buscarPorId(Integer id) {
        return rolRepo.findById(id).orElse(null);
    }

    public List<RolModel> buscarPorNombre(String nombre) {
        return rolRepo.findByNombreRolContainingIgnoreCase(nombre);
    }

    public boolean eliminar(Integer id) {
        if (rolRepo.existsById(id)) {
            rolRepo.deleteById(id);
            return true;
        }
        return false;
    }
}