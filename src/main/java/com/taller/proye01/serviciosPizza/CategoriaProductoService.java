package com.taller.proye01.serviciosPizza;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.taller.proye01.modelPizza.CategoriaProductoModel;
import com.taller.proye01.repositoryPizza.CategoriaProductoRepo;


@Service
public class CategoriaProductoService {

    @Autowired
    private CategoriaProductoRepo categoriaRepo;

    public List<CategoriaProductoModel> listarCategorias() {
        return categoriaRepo.findAll();
    }

    public CategoriaProductoModel guardar(CategoriaProductoModel categoria) {
        return categoriaRepo.save(categoria);
    }

    public CategoriaProductoModel buscarPorId(Integer id) {
        return categoriaRepo.findById(id).orElse(null);
    }

    public List<CategoriaProductoModel> listarOrdenado() {
        return categoriaRepo.listarOrdenado();
    }

    public boolean eliminar(Integer id) {
        if (categoriaRepo.existsById(id)) {
            categoriaRepo.deleteById(id);
            return true;
        }
        return false;
    }
}