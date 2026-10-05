package com.taller.proye01.servicioIMP;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.Geometry;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.LinearRing;
import org.locationtech.jts.geom.PrecisionModel;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.InputStream;

import org.locationtech.jts.geom.Geometry;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.taller.proye01.model.Capa;
import com.taller.proye01.model.CapaCaracteristica;
import com.taller.proye01.model.TipoVegetacion;
import com.taller.proye01.model.Usuario;
import com.taller.proye01.repository.CapaFeatureRepo;
import com.taller.proye01.repository.CapaRepo;
import com.taller.proye01.servicio.CapaService;

import jakarta.persistence.EntityManager;

@Service
public class CapaServiceImp implements CapaService {

    private final CapaRepo capaRepo;
    private final CapaFeatureRepo capaFeatureRepo;
    private final EntityManager entityManager;
    private final ObjectMapper objectMapper;
    private final GeometryFactory geometryFactory = new GeometryFactory(new PrecisionModel(), 4326);
    public CapaServiceImp(
            CapaRepo capaRepo,
            CapaFeatureRepo capaFeatureRepo,
            EntityManager entityManager,
            ObjectMapper objectMapper) {

        this.capaRepo = capaRepo;
        this.capaFeatureRepo = capaFeatureRepo;
        this.entityManager = entityManager;
        this.objectMapper = objectMapper;
    }

    // ============================================================
    // REGISTRO DE CAPA CON SUS CARACTERÍSTICAS
    // ============================================================
    @Override
    @Transactional
    @SuppressWarnings("unchecked")
    public Map<String, Object> registrarCapa(Map<String, Object> payload) {
        // 1. Guardar metadatos base de la capa
        Capa capa = new Capa();
        cargarDatosCapa(capa, payload);
        Capa guardada = capaRepo.save(capa);

        // 2. Extraer lista de características (puede venir como 'caracteristicas' o 'features')
        Object featuresRaw = payload.get("caracteristicas");
        if (featuresRaw == null) {
            featuresRaw = payload.get("features");
        }

        int featuresGuardados = 0;
        if (featuresRaw instanceof List<?> lista) {
            for (Object item : lista) {
                if (item instanceof Map<?, ?> itemMap) {
                    procesarYGuardarCaracteristica(guardada, (Map<String, Object>) itemMap);
                    featuresGuardados++;
                }
            }
        }

        Map<String, Object> respuesta = convertirCapaARespuesta(guardada);
        respuesta.put("mensaje", "Capa y características registradas correctamente");
        respuesta.put("totalCaracteristicas", featuresGuardados);
        return respuesta;
    }

    @SuppressWarnings("unchecked")
    private void procesarYGuardarCaracteristica(Capa capa, Map<String, Object> itemMap) {
        CapaCaracteristica feature = new CapaCaracteristica();
        feature.setCapa(capa);

        // A. Tipo de vegetación (opcional según el tipo de capa)
        Integer idTipoVegetacion = obtenerIntegerFlexible(itemMap, "idTipoVegetacion", "id_tipo_vegetacion");
        if (idTipoVegetacion != null && idTipoVegetacion > 0) {
            TipoVegetacion tipoVeg = entityManager.find(TipoVegetacion.class, idTipoVegetacion);
            feature.setTipoVegetacion(tipoVeg);
        } else {
            feature.setTipoVegetacion(null);
        }

        // B. Propiedades descriptivas (JSONB)
        Object propObj = itemMap.get("propiedades");
        if (propObj == null) {
            propObj = itemMap.get("properties");
        }

        if (propObj == null) {
            feature.setPropiedades("{}");
        } else if (propObj instanceof String txt) {
            feature.setPropiedades(txt.isBlank() ? "{}" : txt);
        } else {
            try {
                feature.setPropiedades(objectMapper.writeValueAsString(propObj));
            } catch (JsonProcessingException e) {
                feature.setPropiedades("{}");
            }
        }

        // C. Geometría (WKT o GeoJSON Map)
        Object geomObj = itemMap.get("geometria");
        if (geomObj == null) {
            geomObj = itemMap.get("geometry");
        }

        if (geomObj != null) {
            Geometry geometria;
            if (geomObj instanceof Map<?, ?> geomMap) {
                geometria = GeoJsonGeometryHelper.convertirGeoJsonAGeometry((Map<String, Object>) geomMap);
            } else if (geomObj instanceof String wkt) {
                geometria = GeoJsonGeometryHelper.convertirWktAGeometry(wkt);
            } else {
                throw new RuntimeException("Formato de geometría incompatible en característica");
            }
            feature.setGeometria(geometria);
            capaFeatureRepo.save(feature);
        }
    }

    @Override
    @Transactional(readOnly = true)
    public List<Map<String, Object>> listarCapas() {
        return capaRepo.findAllByOrderByIdDesc()
                .stream()
                .map(this::convertirCapaARespuesta)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public Map<String, Object> obtenerPorId(Integer id) {
        Capa capa = capaRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Capa no encontrada"));
        return convertirCapaARespuesta(capa);
    }

    @Override
    @Transactional
    public Map<String, Object> actualizarCapa(Integer id, Map<String, Object> payload) {
        Capa capa = capaRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Capa no encontrada"));
        cargarDatosCapa(capa, payload);
        Capa actualizada = capaRepo.save(capa);
        return convertirCapaARespuesta(actualizada);
    }

    @Override
    @Transactional
    public Map<String, Object> eliminarCapa(Integer id) {
        Capa capa = capaRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Capa no encontrada"));
        capaFeatureRepo.deleteByCapaId(id);
        capaRepo.delete(capa);

        Map<String, Object> respuesta = new LinkedHashMap<>();
        respuesta.put("mensaje", "Capa eliminada correctamente");
        respuesta.put("id", id);
        return respuesta;
    }

    @Override
    @Transactional(readOnly = true)
    public List<Map<String, Object>> listarCapasPublicas() {
        return capaRepo.capasPublicas()
                .stream()
                .map(this::convertirCapaARespuesta)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Map<String, Object>> listarCapasPorTipo(String tipo) {
        if (tipo == null || tipo.isBlank()) {
            throw new RuntimeException("El tipo de capa es obligatorio");
        }
        return capaRepo.capasPorTipo(tipo.trim())
                .stream()
                .map(this::convertirCapaARespuesta)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Map<String, Object>> listarCapasPorUsuario(Integer idUsuario) {
        if (idUsuario == null) {
            throw new RuntimeException("El id del usuario es obligatorio");
        }
        return capaRepo.capasPorUsuario(idUsuario)
                .stream()
                .map(this::convertirCapaARespuesta)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Map<String, Object>> buscarPorNombre(String nombre) {
        if (nombre == null || nombre.isBlank()) {
            return listarCapas();
        }
        return capaRepo.findByNombreContainingIgnoreCase(nombre.trim())
                .stream()
                .map(this::convertirCapaARespuesta)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public Map<String, Object> listarTodasComoGeoJSON() {
        List<Map<String, Object>> features = new ArrayList<>();
        List<Capa> capas = capaRepo.findAllByOrderByIdDesc();

        for (Capa capa : capas) {
            List<CapaCaracteristica> caracteristicas = capaFeatureRepo.featuresPorCapa(capa.getId());
            for (CapaCaracteristica feature : caracteristicas) {
                features.add(convertirFeatureAGeoJSON(feature));
            }
        }

        Map<String, Object> geojson = new LinkedHashMap<>();
        geojson.put("type", "FeatureCollection");
        geojson.put("features", features);
        return geojson;
    }

    @Override
    @Transactional(readOnly = true)
    public Map<String, Object> obtenerCapaComoGeoJSON(Integer idCapa) {
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
        capaInfo.put("publico", capa.getPublico());

        geojson.put("capa", capaInfo);
        geojson.put("features", features);
        return geojson;
    }

    @Override
    @Transactional
    @SuppressWarnings("unchecked")
    public Map<String, Object> importarGeoJSON(Map<String, Object> payload) {
        Map<String, Object> capaPayload = new LinkedHashMap<>();
        capaPayload.put("nombre", obtenerString(payload, "nombre"));
        capaPayload.put("tipo", obtenerString(payload, "tipo"));
        capaPayload.put("formato", "GEOJSON");
        capaPayload.put("publico", obtenerBoolean(payload, "publico"));
        capaPayload.put("idUsuario", obtenerIntegerFlexible(payload, "idUsuario", "id_usuario"));

        Capa capa = new Capa();
        cargarDatosCapa(capa, capaPayload);
        Capa capaGuardada = capaRepo.save(capa);

        Object geojsonObj = payload.get("geojson");
        if (!(geojsonObj instanceof Map<?, ?> geojsonMap)) {
            throw new RuntimeException("Debe enviar un objeto GeoJSON válido en el campo 'geojson'");
        }

        Object featuresObj = geojsonMap.get("features");
        if (!(featuresObj instanceof List<?> features)) {
            throw new RuntimeException("El GeoJSON debe ser un FeatureCollection con features");
        }

        int insertados = 0;
        for (Object featureObj : features) {
            if (featureObj instanceof Map<?, ?> fMap) {
                procesarYGuardarCaracteristica(capaGuardada, (Map<String, Object>) fMap);
                insertados++;
            }
        }

        Map<String, Object> respuesta = convertirCapaARespuesta(capaGuardada);
        respuesta.put("mensaje", "GeoJSON importado correctamente");
        respuesta.put("featuresInsertados", insertados);
        return respuesta;
    }

    private void cargarDatosCapa(Capa capa, Map<String, Object> payload) {
        String nombre = obtenerString(payload, "nombre");
        if (nombre == null || nombre.isBlank()) {
            throw new RuntimeException("El nombre de la capa es obligatorio");
        }

        capa.setNombre(nombre.trim());
        capa.setTipo(obtenerString(payload, "tipo"));
        capa.setFormato(obtenerString(payload, "formato"));

        Boolean publico = obtenerBoolean(payload, "publico");
        capa.setPublico(publico != null ? publico : false);

        Integer idUsuario = obtenerIntegerFlexible(payload, "idUsuario", "id_usuario", "usuarioId");
        if (idUsuario == null) {
            throw new RuntimeException("El id del usuario es obligatorio");
        }

        Usuario usuario = entityManager.find(Usuario.class, idUsuario);
        if (usuario == null) {
            throw new RuntimeException("Usuario no encontrado");
        }
        capa.setUsuario(usuario);

        if (capa.getFechaCreacion() == null) {
            capa.setFechaCreacion(new Timestamp(System.currentTimeMillis()));
        }

        if (capa.getFormato() == null || capa.getFormato().isBlank()) {
            capa.setFormato("GEOJSON");
        }
    }

    private Map<String, Object> convertirCapaARespuesta(Capa capa) {
        Map<String, Object> map = new LinkedHashMap<>();
        map.put("id", capa.getId());
        map.put("nombre", capa.getNombre());
        map.put("tipo", capa.getTipo());
        map.put("formato", capa.getFormato());
        map.put("publico", capa.getPublico());
        map.put("fechaCreacion", capa.getFechaCreacion());

        if (capa.getUsuario() != null) {
            map.put("idUsuario", capa.getUsuario().getId());
            map.put("usuario", capa.getUsuario().getNombre());
        } else {
            map.put("idUsuario", null);
            map.put("usuario", null);
        }
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
            properties.put("tipoCapa", feature.getCapa().getTipo());
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
        if (json == null || json.isBlank()) return new LinkedHashMap<>();
        try {
            return objectMapper.readValue(json, Map.class);
        } catch (Exception e) {
            return new LinkedHashMap<>();
        }
    }

    private String obtenerString(Map<String, Object> payload, String key) {
        Object valor = payload.get(key);
        if (valor == null) return null;
        String texto = String.valueOf(valor).trim();
        return texto.isEmpty() || texto.equalsIgnoreCase("null") ? null : texto;
    }

    private Boolean obtenerBoolean(Map<String, Object> payload, String key) {
        Object valor = payload.get(key);
        if (valor == null) return null;
        if (valor instanceof Boolean bool) return bool;
        String texto = String.valueOf(valor).trim();
        if (texto.isEmpty() || texto.equalsIgnoreCase("null")) return null;
        return Boolean.parseBoolean(texto);
    }

    private Integer obtenerIntegerFlexible(Map<String, Object> payload, String... keys) {
        for (String key : keys) {
            Integer valor = convertirInteger(payload.get(key));
            if (valor != null) return valor;
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
    @Override
    @Transactional
    public Map<String, Object> registrarCapaConKml(
            MultipartFile archivoKml,
            String nombre,
            String tipo,
            Boolean publico,
            Integer idUsuario,
            Integer idTipoVegetacion,
            String descripcion) {

        if (archivoKml == null || archivoKml.isEmpty()) {
            throw new RuntimeException("El archivo KML es obligatorio");
        }

        if (nombre == null || nombre.isBlank()) {
            throw new RuntimeException("El nombre de la capa es obligatorio");
        }

        if (idUsuario == null) {
            throw new RuntimeException("El ID de usuario es obligatorio");
        }

        Usuario usuario = entityManager.find(Usuario.class, idUsuario);
        if (usuario == null) {
            throw new RuntimeException("Usuario no encontrado con ID: " + idUsuario);
        }

        // 1. Guardar la entidad Capa
        Capa capa = new Capa();
        capa.setNombre(nombre.trim());
        capa.setTipo(tipo != null && !tipo.isBlank() ? tipo.trim().toUpperCase() : "GENERAL");
        capa.setFormato("KML");
        capa.setPublico(publico != null ? publico : false);
        capa.setUsuario(usuario);
        capa.setFechaCreacion(new Timestamp(System.currentTimeMillis()));

        Capa capaGuardada = capaRepo.save(capa);

        // 2. Extraer geometrías del KML
        List<Geometry> geometrias = parsearGeometriasKml(archivoKml);
        if (geometrias.isEmpty()) {
            throw new RuntimeException("No se encontraron etiquetas <coordinates> válidas en el archivo KML");
        }

        TipoVegetacion tipoVeg = null;
        if (idTipoVegetacion != null && idTipoVegetacion > 0) {
            tipoVeg = entityManager.find(TipoVegetacion.class, idTipoVegetacion);
        }

        // 3. Crear registros en capa_caracteristica
        int insertados = 0;
        for (Geometry geom : geometrias) {
            CapaCaracteristica feature = new CapaCaracteristica();
            feature.setCapa(capaGuardada);
            feature.setTipoVegetacion(tipoVeg);
            feature.setGeometria(geom);

            // Armar JSON de propiedades descriptivas
            Map<String, Object> propMap = new LinkedHashMap<>();
            propMap.put("archivo_origen", archivoKml.getOriginalFilename());
            propMap.put("indice", insertados + 1);
            if (descripcion != null && !descripcion.isBlank()) {
                propMap.put("descripcion", descripcion.trim());
            }

            try {
                feature.setPropiedades(objectMapper.writeValueAsString(propMap));
            } catch (Exception e) {
                feature.setPropiedades("{}");
            }

            capaFeatureRepo.save(feature);
            insertados++;
        }

        Map<String, Object> respuesta = convertirCapaARespuesta(capaGuardada);
        respuesta.put("mensaje", "Capa y características KML registradas con éxito");
        respuesta.put("featuresInsertados", insertados);
        return respuesta;
    }

    private List<Geometry> parsearGeometriasKml(MultipartFile file) {
        List<Geometry> listaGeom = new ArrayList<>();
        try (InputStream is = file.getInputStream()) {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setNamespaceAware(true);
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(is);

            NodeList coordsNodes = doc.getElementsByTagName("coordinates");
            for (int i = 0; i < coordsNodes.getLength(); i++) {
                String rawCoords = coordsNodes.item(i).getTextContent().trim();
                if (rawCoords.isEmpty()) continue;

                String[] tuplas = rawCoords.split("\\s+");
                List<Coordinate> coords = new ArrayList<>();
                for (String t : tuplas) {
                    if (t.isBlank()) continue;
                    String[] p = t.split(",");
                    if (p.length >= 2) {
                        double lon = Double.parseDouble(p[0].trim());
                        double lat = Double.parseDouble(p[1].trim());
                        coords.add(new Coordinate(lon, lat));
                    }
                }

                if (coords.size() >= 3) {
                    // Polígono: asegurar cierre
                    Coordinate first = coords.get(0);
                    Coordinate last = coords.get(coords.size() - 1);
                    if (!first.equals2D(last)) {
                        coords.add(new Coordinate(first.x, first.y));
                    }
                    LinearRing ring = geometryFactory.createLinearRing(coords.toArray(new Coordinate[0]));
                    Geometry poly = geometryFactory.createPolygon(ring, null);
                    poly.setSRID(4326);
                    listaGeom.add(poly);
                } else if (coords.size() == 2) {
                    // Línea
                    Geometry line = geometryFactory.createLineString(coords.toArray(new Coordinate[0]));
                    line.setSRID(4326);
                    listaGeom.add(line);
                } else if (coords.size() == 1) {
                    // Punto
                    Geometry pt = geometryFactory.createPoint(coords.get(0));
                    pt.setSRID(4326);
                    listaGeom.add(pt);
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("Error al interpretar el KML: " + e.getMessage(), e);
        }
        return listaGeom;
    }
}