package com.taller.proye01.servicioIMP;


import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.PrecisionModel;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.taller.proye01.model.Comunidad;

import com.taller.proye01.model.ZonaRiesgo;

import com.taller.proye01.repository.ZonaDeRiesgoRepo;
import com.taller.proye01.repository.comunidadRepo;
import com.taller.proye01.servicio.ComunidadService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ComunidadServiceIMP implements ComunidadService {

    private final comunidadRepo comunidadRepository;
    private final ZonaDeRiesgoRepo zonaRiesgoRepository;
    

    private final GeometryFactory geometryFactory = new GeometryFactory(new PrecisionModel(), 4326);

    @Override
    @Transactional(readOnly = true)
    public List<Map<String, Object>> listar() {
        return comunidadRepository.findAll()
                .stream()
                .map(this::convertirARespuesta)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public Map<String, Object> obtenerPorId(Integer id) {
        Comunidad comunidad = comunidadRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Comunidad no encontrada"));

        return convertirARespuesta(comunidad);
    }

    @Override
    @Transactional
    public Map<String, Object> registrar(Map<String, Object> payload) {
        Comunidad comunidad = new Comunidad();

        cargarDatosDesdePayload(comunidad, payload);

        Comunidad guardada = comunidadRepository.save(comunidad);

        return convertirARespuesta(guardada);
    }

    @Override
    @Transactional
    public Map<String, Object> actualizar(Integer id, Map<String, Object> payload) {
        Comunidad comunidad = comunidadRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Comunidad no encontrada"));

        cargarDatosDesdePayload(comunidad, payload);

        Comunidad actualizada = comunidadRepository.save(comunidad);

        return convertirARespuesta(actualizada);
    }

    @Override
    @Transactional
    public Map<String, Object> eliminar(Integer id) {
        Comunidad comunidad = comunidadRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Comunidad no encontrada"));

        comunidadRepository.delete(comunidad);

        Map<String, Object> respuesta = new LinkedHashMap<>();
        respuesta.put("mensaje", "Comunidad eliminada correctamente");
        respuesta.put("id", id);

        return respuesta;
    }

    @Override
    @Transactional(readOnly = true)
    public Map<String, Object> listarGeoJSON() {
    	List<Comunidad> comunidades =
    	        comunidadRepository.findAll();

        List<Map<String, Object>> features = comunidades.stream()
                .filter(c -> c.getUbicacion() != null)
                .map(this::convertirAFeatureGeoJSON)
                .toList();

        Map<String, Object> geojson = new LinkedHashMap<>();
        geojson.put("type", "FeatureCollection");
        geojson.put("features", features);

        return geojson;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Map<String, Object>> filtrarPorNivelRiesgo(String nivelRiesgo) {
        if (nivelRiesgo == null || nivelRiesgo.trim().isEmpty()) {
            throw new RuntimeException("El nivel de riesgo es obligatorio");
        }

        return comunidadRepository.filtrarPorNivelRiesgo(nivelRiesgo.trim())
                .stream()
                .map(this::convertirARespuesta)
                .toList();
    }

    private void cargarDatosDesdePayload(Comunidad comunidad, Map<String, Object> payload) {
        String nombre = obtenerString(payload, "nombre");

        if (nombre == null || nombre.trim().isEmpty()) {
            throw new RuntimeException("El nombre de la comunidad es obligatorio");
        }

        comunidad.setNombre(nombre.trim());
        comunidad.setPoblacion(obtenerInteger(payload, "poblacion"));
        comunidad.setContactoLider(obtenerString(payload, "contactoLider"));
        comunidad.setTelefonoLider(obtenerString(payload, "telefonoLider"));
        comunidad.setNotas(obtenerString(payload, "notas"));

        Integer idZonaRiesgo = obtenerIntegerFlexible(
                payload,
                "idZonaRiesgo",
                "id_zona_riesgo",
                "idZona"
        );

       

        if (idZonaRiesgo != null) {
            ZonaRiesgo zonaRiesgo = zonaRiesgoRepository.findById(idZonaRiesgo)
                    .orElseThrow(() -> new RuntimeException("Zona de riesgo no encontrada"));

            comunidad.setZonaRiesgo(zonaRiesgo);
        } else {
            comunidad.setZonaRiesgo(null);
        }

       

        Point ubicacion = construirPunto(payload);

        comunidad.setUbicacion(ubicacion);
    }

    private Point construirPunto(Map<String, Object> payload) {
        Double lat = obtenerDoubleFlexible(payload, "lat", "latitude", "y");
        Double lon = obtenerDoubleFlexible(payload, "lon", "lng", "longitud", "x");

        if ((lat == null || lon == null) && payload.get("ubicacion") instanceof Map<?, ?> ubicacionMap) {
            Object coordenadasObj = ubicacionMap.get("coordinates");

            if (coordenadasObj instanceof List<?> coords && coords.size() >= 2) {
                lon = convertirDouble(coords.get(0));
                lat = convertirDouble(coords.get(1));
            }
        }

        if (lat == null || lon == null) {
            throw new RuntimeException("Debe ingresar latitud y longitud válidas");
        }

        if (!coordenadaValida(lat, lon)) {
            throw new RuntimeException("Las coordenadas ingresadas no son válidas");
        }

        Point punto = geometryFactory.createPoint(new Coordinate(lon, lat));
        punto.setSRID(4326);

        return punto;
    }

    private boolean coordenadaValida(Double lat, Double lon) {
        return lat >= -90 && lat <= 90 && lon >= -180 && lon <= 180;
    }

    private Map<String, Object> convertirARespuesta(Comunidad comunidad) {
        Map<String, Object> respuesta = new LinkedHashMap<>();

        respuesta.put("id", comunidad.getId());
        respuesta.put("nombre", comunidad.getNombre());
        respuesta.put("poblacion", comunidad.getPoblacion());
        respuesta.put("contactoLider", comunidad.getContactoLider());
        respuesta.put("telefonoLider", comunidad.getTelefonoLider());
        respuesta.put("notas", comunidad.getNotas());

        ZonaRiesgo zona = comunidad.getZonaRiesgo();

        if (zona != null) {
            respuesta.put("idZonaRiesgo", zona.getId());
            respuesta.put("zonaRiesgo", convertirZonaRiesgo(zona));
            respuesta.put("nivelRiesgo", zona.getNivelRiesgo());
        } else {
            respuesta.put("idZonaRiesgo", null);
            respuesta.put("zonaRiesgo", null);
            respuesta.put("nivelRiesgo", null);
        }

       
        

        Point punto = comunidad.getUbicacion();

        if (punto != null) {
            respuesta.put("lat", punto.getY());
            respuesta.put("lon", punto.getX());

            Map<String, Object> ubicacionGeoJson = new LinkedHashMap<>();
            ubicacionGeoJson.put("type", "Point");
            ubicacionGeoJson.put("coordinates", List.of(punto.getX(), punto.getY()));

            respuesta.put("ubicacion", ubicacionGeoJson);
        } else {
            respuesta.put("lat", null);
            respuesta.put("lon", null);
            respuesta.put("ubicacion", null);
        }

        return respuesta;
    }

    private Map<String, Object> convertirAFeatureGeoJSON(Comunidad comunidad) {
        Point punto = comunidad.getUbicacion();

        Map<String, Object> geometry = new LinkedHashMap<>();
        geometry.put("type", "Point");
        geometry.put("coordinates", List.of(punto.getX(), punto.getY()));

        Map<String, Object> properties = new LinkedHashMap<>();
        properties.put("id", comunidad.getId());
        properties.put("nombre", comunidad.getNombre());
        properties.put("poblacion", comunidad.getPoblacion());
        properties.put("contactoLider", comunidad.getContactoLider());
        properties.put("telefonoLider", comunidad.getTelefonoLider());
        properties.put("notas", comunidad.getNotas());

        if (comunidad.getZonaRiesgo() != null) {
            properties.put("idZonaRiesgo", comunidad.getZonaRiesgo().getId());
            properties.put("zonaRiesgo", comunidad.getZonaRiesgo().getNombre());
            properties.put("nivelRiesgo", comunidad.getZonaRiesgo().getNivelRiesgo());
        }


        Map<String, Object> feature = new LinkedHashMap<>();
        feature.put("type", "Feature");
        feature.put("id", comunidad.getId());
        feature.put("properties", properties);
        feature.put("geometry", geometry);

        return feature;
    }

    private Map<String, Object> convertirZonaRiesgo(ZonaRiesgo zona) {
        Map<String, Object> map = new LinkedHashMap<>();

        map.put("id", zona.getId());
        map.put("nombre", zona.getNombre());
        map.put("nivelRiesgo", zona.getNivelRiesgo());
        map.put("descripcion", zona.getDescripcion());

        return map;
    }



    private String obtenerString(Map<String, Object> payload, String key) {
        Object valor = payload.get(key);

        if (valor == null) {
            return null;
        }

        String texto = String.valueOf(valor).trim();

        return texto.isEmpty() ? null : texto;
    }

    private Integer obtenerInteger(Map<String, Object> payload, String key) {
        return convertirInteger(payload.get(key));
    }

    private Integer obtenerIntegerFlexible(Map<String, Object> payload, String... keys) {
        for (String key : keys) {
            Integer valor = convertirInteger(payload.get(key));

            if (valor != null) {
                return valor;
            }
        }

        return null;
    }

    private Double obtenerDoubleFlexible(Map<String, Object> payload, String... keys) {
        for (String key : keys) {
            Double valor = convertirDouble(payload.get(key));

            if (valor != null) {
                return valor;
            }
        }

        return null;
    }

    private Integer convertirInteger(Object valor) {
        if (valor == null) {
            return null;
        }

        if (valor instanceof Number number) {
            return number.intValue();
        }

        String texto = String.valueOf(valor).trim();

        if (texto.isEmpty() || texto.equalsIgnoreCase("null")) {
            return null;
        }

        try {
            return Integer.parseInt(texto);
        } catch (NumberFormatException e) {
            throw new RuntimeException("Valor numérico inválido: " + texto);
        }
    }

    private Double convertirDouble(Object valor) {
        if (valor == null) {
            return null;
        }

        if (valor instanceof Number number) {
            return number.doubleValue();
        }

        String texto = String.valueOf(valor).trim();

        if (texto.isEmpty() || texto.equalsIgnoreCase("null")) {
            return null;
        }

        try {
            return Double.parseDouble(texto);
        } catch (NumberFormatException e) {
            throw new RuntimeException("Coordenada inválida: " + texto);
        }
    }
}