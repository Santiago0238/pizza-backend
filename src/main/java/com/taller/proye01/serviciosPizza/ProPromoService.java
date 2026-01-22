package com.taller.proye01.serviciosPizza;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.taller.proye01.modelPizza.ProPromoModel;
import com.taller.proye01.modelPizza.ProPromoPK;
import com.taller.proye01.repositoryPizza.ProPromoRepo;

@Service
public class ProPromoService {

    @Autowired
    private ProPromoRepo proPromoRepo;

    public List<ProPromoModel> listar() {
        return proPromoRepo.findAll();
    }

    public ProPromoModel guardar(ProPromoModel proPromo) {
        return proPromoRepo.save(proPromo);
    }

    public ProPromoModel buscar(ProPromoPK id) {
        return proPromoRepo.findById(id).orElse(null);
    }

    public List<ProPromoModel> listarPorPromocion(Integer idPromocion) {
        return proPromoRepo.productosPorPromocion(idPromocion);
    }

    public List<ProPromoModel> listarPorProducto(Integer idProducto) {
        return proPromoRepo.promocionesPorProducto(idProducto);
    }

    public boolean eliminar(ProPromoPK id) {
        if (proPromoRepo.existsById(id)) {
            proPromoRepo.deleteById(id);
            return true;
        }
        return false;
    }
}