package com.taller.proye01.serviciosPizza;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.taller.proye01.modelPizza.RolMeModel;
import com.taller.proye01.modelPizza.RolMePK;
import com.taller.proye01.repositoryPizza.RolMeRepo;

@Service
public class RolMeService {

    @Autowired
    private RolMeRepo rolMeRepo;

    public List<RolMeModel> listar() {
        return rolMeRepo.findAll();
    }

    public RolMeModel guardar(RolMeModel rolMe) {
        return rolMeRepo.save(rolMe);
    }

    public RolMeModel buscar(RolMePK id) {
        return rolMeRepo.findById(id).orElse(null);
    }

    public List<RolMeModel> listarPorRol(Integer idRol) {
        return rolMeRepo.menusPorRol(idRol);
    }

    public List<RolMeModel> listarPorMenu(Integer idMenu) {
        return rolMeRepo.rolesPorMenu(idMenu);
    }

    public boolean eliminar(RolMePK id) {
        if (rolMeRepo.existsById(id)) {
            rolMeRepo.deleteById(id);
            return true;
        }
        return false;
    }
}