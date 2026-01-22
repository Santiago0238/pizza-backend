package com.taller.proye01.serviciosPizza;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.taller.proye01.modelPizza.MenuModel;
import com.taller.proye01.repositoryPizza.MenuRepo;

@Service
public class MenuService {

    @Autowired
    private MenuRepo menuRepo;

    public List<MenuModel> listar() {
        return menuRepo.findAll();
    }

    public MenuModel guardar(MenuModel menu) {
        return menuRepo.save(menu);
    }

    public MenuModel buscarPorId(Integer id) {
        return menuRepo.findById(id).orElse(null);
    }

    public List<MenuModel> listarOrdenado() {
        return menuRepo.listarOrdenado();
    }

    public boolean eliminar(Integer id) {
        if (menuRepo.existsById(id)) {
            menuRepo.deleteById(id);
            return true;
        }
        return false;
    }
}