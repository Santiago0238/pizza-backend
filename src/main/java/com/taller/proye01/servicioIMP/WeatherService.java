package com.taller.proye01.servicioIMP;
import java.util.Locale;

import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class WeatherService {

    private final RestTemplate restTemplate;

    public WeatherService() {
        this.restTemplate = new RestTemplate();
    }

    public String getForecast(double latitude, double longitude) {
        // Usamos Locale.US para asegurar que los decimales usen punto (.) y no coma (,)
        String url = String.format(
            Locale.US, 
            "https://api.open-meteo.com/v1/forecast?latitude=%f&longitude=%f&hourly=temperature_2m",
            latitude, longitude
        );
        
        return restTemplate.getForObject(url, String.class);
    }
}