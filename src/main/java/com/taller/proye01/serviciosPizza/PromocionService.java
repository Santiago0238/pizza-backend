package com.taller.proye01.serviciosPizza;

import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.taller.proye01.modelPizza.PromocionModel;
import com.taller.proye01.repositoryPizza.PromocionRepo;

@Service
public class PromocionService {

    @Autowired
    private PromocionRepo promocionRepo;

    public List<PromocionModel> listar() {
        return promocionRepo.findAll();
    }

    public PromocionModel guardar(PromocionModel promo) {
        return promocionRepo.save(promo);
    }

    public PromocionModel buscarPorId(Integer id) {
        return promocionRepo.findById(id).orElse(null);
    }

    public List<PromocionModel> porEstado(String estado) {
        return promocionRepo.promocionesPorEstado(estado);
    }

    public List<PromocionModel> vigentes(Date hoy) {
        return promocionRepo.promocionesVigentes(hoy);
    }

    public List<PromocionModel> buscarPorNombre(String nombre) {
        return promocionRepo.findByNombreContainingIgnoreCase(nombre);
    }

    public boolean eliminar(Integer id) {
        if (promocionRepo.existsById(id)) {
            promocionRepo.deleteById(id);
            return true;
        }
        return false;
    }
}