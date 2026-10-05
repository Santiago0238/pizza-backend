package com.taller.proye01.servicioIMP;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.locationtech.jts.geom.Geometry;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.taller.proye01.model.Capa;
import com.taller.proye01.model.CapaCaracteristica;
import com.taller.proye01.model.TipoVegetacion;
import com.taller.proye01.repository.CapaFeatureRepo;
import com.taller.proye01.repository.CapaRepo;
import com.taller.proye01.servicio.CapaFeatureService;

import jakarta.persistence.EntityManager;

@Service
public class CapaFeatureServiceImp implements CapaFeatureService {

    private final CapaFeatureRepo capaFeatureRepo;
    private final CapaRepo capaRepo;
    private final EntityManager entityManager;
    private final ObjectMapper objectMapper;

    public CapaFeatureServiceImp(
            CapaFeatureRepo capaFeatureRepo,
            CapaRepo capaRepo,
            EntityManager entityManager,
            ObjectMapper objectMapper) {

        this.capaFeatureRepo = capaFeatureRepo;
        this.capaRepo = capaRepo;
        this.entityManager = entityManager;
        this.objectMapper = objectMapper;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Map<String, Object>> listarFeaturesPorCapa(Integer idCapa) {
        if (idCapa == null) {
            throw new RuntimeException("El id de la capa es obligatorio");
        }

        return capaFeatureRepo.featuresPorCapa(idCapa)
                .stream()
                .map(this::convertirFeatureARespuesta)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public Map<String, Object> obtenerFeaturePorId(Integer idFeature) {
        CapaCaracteristica feature = capaFeatureRepo.findById(idFeature)
                .orElseThrow(() -> new RuntimeException("Característica no encontrada"));

        return convertirFeatureARespuesta(feature);
    }

    @Override
    @Transactional
    @SuppressWarnings("unchecked")
    public Map<String, Object> registrarFeature(Integer idCapa, Map<String, Object> payload) {
        Capa capa = capaRepo.findById(idCapa)
                .orElseThrow(() -> new RuntimeException("Capa no encontrada"));

        CapaCaracteristica feature = new CapaCaracteristica();
        feature.setCapa(capa);

        cargarDatosFeature(feature, payload);

        CapaCaracteristica guardada = capaFeatureRepo.save(feature);

        return convertirFeatureARespuesta(guardada);
    }

    @Override
    @Transactional
    public Map<String, Object> actualizarFeature(Integer idFeature, Map<String, Object> payload) {
        CapaCaracteristica feature = capaFeatureRepo.findById(idFeature)
                .orElseThrow(() -> new RuntimeException("Característica no encontrada"));

        cargarDatosFeature(feature, payload);

        CapaCaracteristica actualizada = capaFeatureRepo.save(feature);

        return convertirFeatureARespuesta(actualizada);
    }

    @Override
    @Transactional
    public Map<String, Object> eliminarFeature(Integer idFeature) {
        CapaCaracteristica feature = capaFeatureRepo.findById(idFeature)
                .orElseThrow(() -> new RuntimeException("Característica no encontrada"));

        capaFeatureRepo.delete(feature);

        Map<String, Object> respuesta = new LinkedHashMap<>();
        respuesta.put("mensaje", "Característica eliminada correctamente");
        respuesta.put("id", idFeature);

        return respuesta;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Map<String, Object>> listarFeaturesPorTipoVegetacion(Integer idTipoVegetacion) {
        if (idTipoVegetacion == null) {
            throw new RuntimeException("El id del tipo de vegetación es obligatorio");
        }

        return capaFeatureRepo.featuresPorTipoVegetacion(idTipoVegetacion)
                .stream()
                .map(this::convertirFeatureARespuesta)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Map<String, Object>> listarFeaturesIntersectados(String wktArea) {
        if (wktArea == null || wktArea.isBlank()) {
            throw new RuntimeException("El área WKT es obligatoria");
        }

        return capaFeatureRepo.featuresIntersectados(wktArea.trim())
                .stream()
                .map(this::convertirFeatureARespuesta)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public Map<String, Object> listarFeaturesComoGeoJSON(Integer idCapa) {
        Capa capa = capaRepo.findById(idCapa)
                .orElseThrow(() -> new RuntimeException("Capa no encontrada"));

        List<Map<String, Object>> features = capaFeatureRepo.featuresPorCapa(idCapa)
                .stream()
                .map(this::convertirFeatureAGeoJSON)
                .toList();

        Map<String, Object> geojson = new LinkedHashMap<>();
        geojson.put("type", "FeatureCollection");

        Map<String, Object> capaInfo = new LinkedHashMap<>();
        capaInfo.put("id", capa.getId());
        capaInfo.put("nombre", capa.getNombre());
        capaInfo.put("tipo", capa.getTipo());
        capaInfo.put("formato", capa.getFormato());

        geojson.put("capa", capaInfo);
        geojson.put("features", features);

        return geojson;
    }

    @SuppressWarnings("unchecked")
    private void cargarDatosFeature(CapaCaracteristica feature, Map<String, Object> payload) {
        Integer idTipoVegetacion = obtenerIntegerFlexible(
                payload,
                "idTipoVegetacion",
                "id_tipo_vegetacion"
        );

        if (idTipoVegetacion != null) {
            TipoVegetacion tipoVegetacion = entityManager.find(TipoVegetacion.class, idTipoVegetacion);

            if (tipoVegetacion == null) {
                throw new RuntimeException("Tipo de vegetación no encontrado");
            }

            feature.setTipoVegetacion(tipoVegetacion);
        } else {
            feature.setTipoVegetacion(null);
        }

        Object propiedadesObj = payload.get("propiedades");

        if (propiedadesObj == null) {
            feature.setPropiedades("{}");
        } else if (propiedadesObj instanceof String texto) {
            feature.setPropiedades(texto.isBlank() ? "{}" : texto);
        } else {
            try {
                feature.setPropiedades(objectMapper.writeValueAsString(propiedadesObj));
            } catch (Exception e) {
                throw new RuntimeException("Las propiedades no tienen formato JSON válido");
            }
        }

        Object geometriaObj = payload.get("geometria");

        if (geometriaObj == null) {
            geometriaObj = payload.get("geometry");
        }

        if (geometriaObj == null) {
            throw new RuntimeException("La geometría es obligatoria");
        }

        Geometry geometria;

        if (geometriaObj instanceof Map<?, ?> geometriaMap) {
            geometria = GeoJsonGeometryHelper.convertirGeoJsonAGeometry(
                    (Map<String, Object>) geometriaMap
            );
        } else if (geometriaObj instanceof String wkt) {
            geometria = GeoJsonGeometryHelper.convertirWktAGeometry(wkt);
        } else {
            throw new RuntimeException("Formato de geometría no soportado");
        }

        feature.setGeometria(geometria);
    }

    private Map<String, Object> convertirFeatureARespuesta(CapaCaracteristica feature) {
        Map<String, Object> map = new LinkedHashMap<>();

        map.put("id", feature.getId());

        if (feature.getCapa() != null) {
            map.put("idCapa", feature.getCapa().getId());
            map.put("capa", feature.getCapa().getNombre());
        } else {
            map.put("idCapa", null);
            map.put("capa", null);
        }

        if (feature.getTipoVegetacion() != null) {
            map.put("idTipoVegetacion", feature.getTipoVegetacion().getId());
            map.put("tipoVegetacion", feature.getTipoVegetacion().getNombre());
        } else {
            map.put("idTipoVegetacion", null);
            map.put("tipoVegetacion", null);
        }

        map.put("propiedades", convertirJsonStringAMap(feature.getPropiedades()));
        map.put("geometria", GeoJsonGeometryHelper.convertirGeometryAGeoJson(feature.getGeometria()));

        return map;
    }

    private Map<String, Object> convertirFeatureAGeoJSON(CapaCaracteristica feature) {
        Map<String, Object> geojson = new LinkedHashMap<>();

        geojson.put("type", "Feature");
        geojson.put("id", feature.getId());

        Map<String, Object> properties = new LinkedHashMap<>();

        if (feature.getCapa() != null) {
            properties.put("idCapa", feature.getCapa().getId());
            properties.put("capa", feature.getCapa().getNombre());
        }

        if (feature.getTipoVegetacion() != null) {
            properties.put("idTipoVegetacion", feature.getTipoVegetacion().getId());
            properties.put("tipoVegetacion", feature.getTipoVegetacion().getNombre());
        }

        properties.put("propiedades", convertirJsonStringAMap(feature.getPropiedades()));

        geojson.put("properties", properties);
        geojson.put("geometry", GeoJsonGeometryHelper.convertirGeometryAGeoJson(feature.getGeometria()));

        return geojson;
    }

    private Map<String, Object> convertirJsonStringAMap(String json) {
        if (json == null || json.isBlank()) {
            return new LinkedHashMap<>();
        }

        try {
            return objectMapper.readValue(json, Map.class);
        } catch (Exception e) {
            return new LinkedHashMap<>();
        }
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

    private Integer convertirInteger(Object valor) {
        if (valor == null) return null;

        if (valor instanceof Number number) return number.intValue();

        String texto = String.valueOf(valor).trim();

        if (texto.isEmpty() || texto.equalsIgnoreCase("null")) return null;

        try {
            return Integer.parseInt(texto);
        } catch (NumberFormatException e) {
            throw new RuntimeException("Valor entero inválido: " + texto);
        }
    }
}