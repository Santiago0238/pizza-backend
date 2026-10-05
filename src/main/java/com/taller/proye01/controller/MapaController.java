package com.taller.proye01.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.taller.proye01.servicio.RutaService;

@RestController
@RequestMapping("/api/v1/mapa")
@CrossOrigin(origins = "*") 
public class MapaController {
    @Autowired
    private RutaService rutaService;

   
}