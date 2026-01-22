package com.taller.proye01.serviciosPizza;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.taller.proye01.modelPizza.ClienteModel;
import com.taller.proye01.repositoryPizza.ClienteRepo;


@Service
public class ClienteService {

    @Autowired
    private ClienteRepo clienteRepo;

    public List<ClienteModel> listarClientes() {
        return clienteRepo.findAll();
    }

    public ClienteModel guardar(ClienteModel cliente) {
        return clienteRepo.save(cliente);
    }

    public ClienteModel buscarPorId(Integer id) {
        return clienteRepo.findById(id).orElse(null);
    }

    public List<ClienteModel> buscarPorNombre(String nombre) {
        return clienteRepo.findByNombreContainingIgnoreCase(nombre);
    }

    public List<ClienteModel> clientesActivos() {
        return clienteRepo.clientesActivos();
    }

    public boolean eliminar(Integer id) {
        if (clienteRepo.existsById(id)) {
            clienteRepo.deleteById(id);
            return true;
        }
        return false;
    }
    
    public ClienteModel buscarPorTelefono(String telefono) {
        return clienteRepo.buscarPorTelefono(telefono);
    }

    
}