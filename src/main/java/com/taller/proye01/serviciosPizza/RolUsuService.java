package com.taller.proye01.serviciosPizza;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.taller.proye01.modelPizza.RolUsuModel;
import com.taller.proye01.modelPizza.RolUsuPK;
import com.taller.proye01.repositoryPizza.RolUsuRepo;

@Service
public class RolUsuService {

    @Autowired
    private RolUsuRepo rolUsuRepo;

    public List<RolUsuModel> listar() {
        return rolUsuRepo.findAll();
    }

    public RolUsuModel guardar(RolUsuModel rolUsu) {
        return rolUsuRepo.save(rolUsu);
    }

    public RolUsuModel buscar(RolUsuPK id) {
        return rolUsuRepo.findById(id).orElse(null);
    }

    public List<RolUsuModel> listarPorUsuario(Integer idUsuario) {
        return rolUsuRepo.rolesPorUsuario(idUsuario);
    }

    public List<RolUsuModel> listarPorRol(Integer idRol) {
        return rolUsuRepo.usuariosPorRol(idRol);
    }

    public boolean eliminar(RolUsuPK id) {
        if (rolUsuRepo.existsById(id)) {
            rolUsuRepo.deleteById(id);
            return true;
        }
        return false;
    }
}