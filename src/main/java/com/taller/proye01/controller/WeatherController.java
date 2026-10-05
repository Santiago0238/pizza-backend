package com.taller.proye01.controller;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.taller.proye01.servicioIMP.WeatherService;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;

@RestController
@RequestMapping("/api/weather")
@Validated // <-- IMPORTANTE: Activa la validación en los parámetros del método
public class WeatherController {

    private final WeatherService weatherService;

    public WeatherController(WeatherService weatherService) {
      this.weatherService = weatherService;
    }

    @GetMapping(produces = MediaType.APPLICATION_JSON_VALUE)
   
    public ResponseEntity<String> getWeather(@RequestParam double latitude, @RequestParam double longitude) {
        System.out.println("VALOR RECIBIDO LATITUD: " + latitude); // <-- Revisa qué imprime la consola
        String weatherJson = weatherService.getForecast(latitude, longitude);
        return ResponseEntity.ok(weatherJson);
    }
}