package com.taller.proye01.serviciosPizza;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.taller.proye01.modelPizza.PersonalizacionPizzaModel;
import com.taller.proye01.repositoryPizza.PersonalizacionPizzaRepo;

@Service
public class PersonalizacionPizzaService {

    @Autowired
    private PersonalizacionPizzaRepo personalizacionRepo;

    public List<PersonalizacionPizzaModel> listar() {
        return personalizacionRepo.findAll();
    }

    public PersonalizacionPizzaModel guardar(PersonalizacionPizzaModel modelo) {
        return personalizacionRepo.save(modelo);
    }

    public PersonalizacionPizzaModel buscarPorId(Integer id) {
        return personalizacionRepo.findById(id).orElse(null);
    }

    public List<PersonalizacionPizzaModel> porProducto(Integer idProducto) {
        return personalizacionRepo.personalizacionesPorProducto(idProducto);
    }

    public List<PersonalizacionPizzaModel> porTamano(String tamano) {
        return personalizacionRepo.findByTamano(tamano);
    }

    public List<PersonalizacionPizzaModel> porTipo(String tipo) {
        return personalizacionRepo.findByTipo(tipo);
    }

    public boolean eliminar(Integer id) {
        if (personalizacionRepo.existsById(id)) {
            personalizacionRepo.deleteById(id);
            return true;
        }
        return false;
    }
}