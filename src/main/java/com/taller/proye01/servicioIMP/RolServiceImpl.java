package com.taller.proye01.servicioIMP;

import java.util.List;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.taller.proye01.model.Rol;
import com.taller.proye01.repository.RolRepo;
import com.taller.proye01.repository.UsuarioRepo;
import com.taller.proye01.servicio.Rolservice;

@Service
public class RolServiceImpl implements Rolservice {

    private final RolRepo rolRepository;
    private final UsuarioRepo usuarioRepo;

    public RolServiceImpl(
            RolRepo rolRepository,
            UsuarioRepo usuarioRepo) {
        this.rolRepository = rolRepository;
        this.usuarioRepo = usuarioRepo;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Rol> listar() {
        return rolRepository.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public Rol buscarPorId(Integer id) {
        return rolRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Rol no encontrado"));
    }

    @Override
    @Transactional
    public Rol guardar(Rol rol) {

        String nombreNormalizado = rol.getNombre().trim().toUpperCase();

        if (rolRepository.existsByNombreIgnoreCase(nombreNormalizado)) {
            throw new RuntimeException("Ya existe un rol con ese nombre");
        }

        rol.setNombre(nombreNormalizado);

        return rolRepository.save(rol);
    }

    @Override
    @Transactional
    public Rol actualizar(Integer id, Rol rol) {

        Rol rolExistente = rolRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Rol no encontrado"));

        String nombreNormalizado = rol.getNombre().trim().toUpperCase();

        Rol rolDuplicado = rolRepository.findByNombreIgnoreCase(nombreNormalizado)
                .orElse(null);

        if (rolDuplicado != null && !rolDuplicado.getId().equals(id)) {
            throw new RuntimeException("Ya existe un rol con ese nombre");
        }

        rolExistente.setNombre(nombreNormalizado);

        return rolRepository.save(rolExistente);
    }

    @Override
    @Transactional
    public void eliminar(Integer id) {

        Rol rolExistente = rolRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Rol no encontrado"));

        Long cantidadUsuarios = usuarioRepo.contarPorRol(id);

        if (cantidadUsuarios > 0) {
            throw new RuntimeException("No se puede eliminar el rol porque tiene usuarios asignados");
        }

        rolRepository.delete(rolExistente);
    }
}