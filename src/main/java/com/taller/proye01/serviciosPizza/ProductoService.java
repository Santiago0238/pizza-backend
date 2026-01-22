package com.taller.proye01.serviciosPizza;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.taller.proye01.modelPizza.ProductoModel;
import com.taller.proye01.repositoryPizza.ProductoRepo;

@Service
public class ProductoService {

    @Autowired
    private ProductoRepo productoRepo;

    public List<ProductoModel> listar() {
        return productoRepo.findAll();
    }

    public ProductoModel guardar(ProductoModel producto) {
        return productoRepo.save(producto);
    }

    public ProductoModel buscarPorId(Integer id) {
        return productoRepo.findById(id).orElse(null);
    }

    public List<ProductoModel> listarPorCategoria(Integer idCategoria) {
        return productoRepo.productosPorCategoria(idCategoria);
    }

    public List<ProductoModel> listarPorProveedor(Integer idProveedor) {
        return productoRepo.productosPorProveedor(idProveedor);
    }

    public List<ProductoModel> disponibles() {
        return productoRepo.productosDisponibles();
    }

    public List<ProductoModel> buscarPorNombre(String nombre) {
        return productoRepo.findByNombreContainingIgnoreCase(nombre);
    }

    public boolean eliminar(Integer id) {
        if (productoRepo.existsById(id)) {
            productoRepo.deleteById(id);
            return true;
        }
        return false;
    }
}