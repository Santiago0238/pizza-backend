package com.taller.proye01.servicio;


import java.util.List;

import com.taller.proye01.model.Alerta;

public interface AlertaService {


    List<Alerta> listar();

    Alerta obtenerPorId(Integer id);

    List<Alerta> listarActivas();

    List<Alerta> listarHistorial();

    Alerta guardar(Alerta alerta);

    Alerta actualizar(Integer id, Alerta alerta);

    Alerta cambiarEstado(Integer id, String estado);

    Alerta cerrar(Integer id);

    void eliminar(Integer id);

    List<Alerta> buscarPorSeveridad(String severidad);

    List<Alerta> buscarPorEstado(String estado);

    List<Alerta> buscarPorTexto(String texto);
    
}
