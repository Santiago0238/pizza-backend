package com.taller.proye01.servicioIMP;

import java.io.InputStream;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.io.WKTReader;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.LinearRing;
import org.locationtech.jts.geom.Polygon;
import org.locationtech.jts.geom.PrecisionModel;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;

import com.taller.proye01.model.TipoVegetacion;
import com.taller.proye01.model.ZonaRiesgo;
import com.taller.proye01.repository.TipoVegetacionRepo;
import com.taller.proye01.repository.ZonaDeRiesgoRepo;
import com.taller.proye01.servicio.ZonaRiesgoService;

@Service
public class ZonaRiesgoServiceIMP implements ZonaRiesgoService {

    private final ZonaDeRiesgoRepo zonaRiesgoRepo;
    private final TipoVegetacionRepo tipoVegetacionRepo;
    private final GeometryFactory geometryFactory = new GeometryFactory(new PrecisionModel(), 4326);

    public ZonaRiesgoServiceIMP(ZonaDeRiesgoRepo zonaRiesgoRepo, TipoVegetacionRepo tipoVegetacionRepo) {
        this.zonaRiesgoRepo = zonaRiesgoRepo;
        this.tipoVegetacionRepo = tipoVegetacionRepo;
    }

    @Override
    @Transactional(readOnly = true)
    public List<ZonaRiesgo> listar() {
        return zonaRiesgoRepo.findAll();
    }

    @Override
    @Transactional(readOnly = true)
    public ZonaRiesgo obtenerPorId(Integer id) {
        return zonaRiesgoRepo.findById(id).orElseThrow(() -> new RuntimeException("Zona de riesgo no encontrada"));
    }

    @Override
    @Transactional
    public ZonaRiesgo guardar(ZonaRiesgo zona) {
        if (zona.getNombre() == null || zona.getNombre().isBlank()) {
            throw new RuntimeException("El nombre de la zona es obligatorio");
        }

        if (zona.getNivelRiesgo() == null || zona.getNivelRiesgo().isBlank()) {
            throw new RuntimeException("El nivel de riesgo es obligatorio");
        }

        zona.setNombre(zona.getNombre().trim());
        zona.setNivelRiesgo(zona.getNivelRiesgo().trim().toUpperCase());

        if (zona.getTipoVegetacion() != null && zona.getTipoVegetacion().getId() != null) {
            TipoVegetacion tipoVegetacion = tipoVegetacionRepo.findById(zona.getTipoVegetacion().getId())
                    .orElseThrow(() -> new RuntimeException("Tipo de vegetación no encontrado"));
            zona.setTipoVegetacion(tipoVegetacion);
        }

        zona.setFechaActualizacion(new Timestamp(System.currentTimeMillis()));
        return zonaRiesgoRepo.save(zona);
    }

    @Override
    @Transactional
    public ZonaRiesgo actualizar(Integer id, ZonaRiesgo zona) {
        ZonaRiesgo zonaActual = zonaRiesgoRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Zona de riesgo no encontrada"));

        if (zona.getNombre() == null || zona.getNombre().isBlank()) {
            throw new RuntimeException("El nombre de la zona es obligatorio");
        }

        if (zona.getNivelRiesgo() == null || zona.getNivelRiesgo().isBlank()) {
            throw new RuntimeException("El nivel de riesgo es obligatorio");
        }

        zonaActual.setNombre(zona.getNombre().trim());
        zonaActual.setNivelRiesgo(zona.getNivelRiesgo().trim().toUpperCase());
        zonaActual.setDescripcion(zona.getDescripcion());
        zonaActual.setFuenteDatos(zona.getFuenteDatos());

        if (zona.getTipoVegetacion() != null && zona.getTipoVegetacion().getId() != null) {
            TipoVegetacion tipoVegetacion = tipoVegetacionRepo.findById(zona.getTipoVegetacion().getId())
                    .orElseThrow(() -> new RuntimeException("Tipo de vegetación no encontrado"));
            zonaActual.setTipoVegetacion(tipoVegetacion);
        } else {
            zonaActual.setTipoVegetacion(null);
        }

        if (zona.getPoligono() != null) {
            zonaActual.setPoligono(zona.getPoligono());
        }

        zonaActual.setFechaActualizacion(new Timestamp(System.currentTimeMillis()));
        return zonaRiesgoRepo.save(zonaActual);
    }

    @Override
    @Transactional
    public void eliminar(Integer id) {
        ZonaRiesgo zona = zonaRiesgoRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Zona de riesgo no encontrada"));
        zonaRiesgoRepo.delete(zona);
    }

    @Override
    @Transactional(readOnly = true)
    public List<ZonaRiesgo> buscarPorNivel(String nivelRiesgo) {
        if (nivelRiesgo == null || nivelRiesgo.isBlank()) {
            return zonaRiesgoRepo.findAll();
        }
        return zonaRiesgoRepo.findByNivelRiesgoIgnoreCase(nivelRiesgo.trim());
    }

    @Override
    @Transactional(readOnly = true)
    public List<ZonaRiesgo> buscarPorTexto(String texto) {
        if (texto == null || texto.isBlank()) {
            return zonaRiesgoRepo.findAll();
        }
        return zonaRiesgoRepo.buscarPorTexto(texto.trim());
    }

    @Override
    @Transactional
    public ZonaRiesgo guardarDesdeKml(
            MultipartFile file,
            String nombre,
            String nivelRiesgo,
            String descripcion,
            String fuenteDatos,
            Integer idTipoVegetacion) {

        if (file == null || file.isEmpty()) {
            throw new RuntimeException("El archivo KML es obligatorio");
        }

        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null || !originalFilename.toLowerCase().endsWith(".kml")) {
            throw new RuntimeException("El archivo debe tener extensión .kml");
        }

        if (nombre == null || nombre.isBlank()) {
            throw new RuntimeException("El nombre de la zona es obligatorio");
        }

        if (nivelRiesgo == null || nivelRiesgo.isBlank()) {
            throw new RuntimeException("El nivel de riesgo es obligatorio");
        }

        Polygon poligono = extraerPoligonoDeKml(file);

        ZonaRiesgo zona = new ZonaRiesgo();
        zona.setNombre(nombre.trim());
        zona.setNivelRiesgo(nivelRiesgo.trim().toUpperCase());
        zona.setDescripcion(descripcion != null ? descripcion.trim() : null);
        zona.setFuenteDatos(fuenteDatos != null ? fuenteDatos.trim() : "Archivo KML: " + originalFilename);
        zona.setPoligono(poligono);
        zona.setFechaActualizacion(new Timestamp(System.currentTimeMillis()));

        if (idTipoVegetacion != null) {
            TipoVegetacion tipoVegetacion = tipoVegetacionRepo.findById(idTipoVegetacion)
                    .orElseThrow(() -> new RuntimeException("Tipo de vegetación no encontrado"));
            zona.setTipoVegetacion(tipoVegetacion);
        }

        return zonaRiesgoRepo.save(zona);
    }

    private Polygon extraerPoligonoDeKml(MultipartFile file) {
        try (InputStream is = file.getInputStream()) {
            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            factory.setNamespaceAware(true);
            DocumentBuilder builder = factory.newDocumentBuilder();
            Document doc = builder.parse(is);

            NodeList coordsList = doc.getElementsByTagName("coordinates");
            if (coordsList.getLength() == 0) {
                throw new RuntimeException("No se encontró la etiqueta <coordinates> en el archivo KML");
            }

            String coordsRaw = coordsList.item(0).getTextContent().trim();
            String[] tuplas = coordsRaw.split("\\s+");

            List<Coordinate> listaCoords = new ArrayList<>();
            for (String tupla : tuplas) {
                if (tupla.isBlank()) continue;
                String[] partes = tupla.split(",");
                if (partes.length >= 2) {
                    double lon = Double.parseDouble(partes[0].trim());
                    double lat = Double.parseDouble(partes[1].trim());
                    listaCoords.add(new Coordinate(lon, lat));
                }
            }

            if (listaCoords.size() < 3) {
                throw new RuntimeException("El KML no contiene suficientes coordenadas para formar un polígono (mínimo 3)");
            }

            Coordinate first = listaCoords.get(0);
            Coordinate last = listaCoords.get(listaCoords.size() - 1);
            if (!first.equals2D(last)) {
                listaCoords.add(new Coordinate(first.x, first.y));
            }

            if (listaCoords.size() < 4) {
                throw new RuntimeException("Un polígono cerrado requiere al menos 4 puntos");
            }

            Coordinate[] coordArray = listaCoords.toArray(new Coordinate[0]);
            LinearRing shell = geometryFactory.createLinearRing(coordArray);
            Polygon poly = geometryFactory.createPolygon(shell, null);
            poly.setSRID(4326);

            return poly;

        } catch (Exception e) {
            throw new RuntimeException("Error al procesar el archivo KML: " + e.getMessage(), e);
        }
    }

    @Override
    @Transactional
    public ZonaRiesgo actualizarDesdeKml(
            Integer id,
            MultipartFile file,
            String nombre,
            String nivelRiesgo,
            String descripcion,
            String fuenteDatos,
            Integer idTipoVegetacion) {

        ZonaRiesgo zona = zonaRiesgoRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Zona de riesgo no encontrada con ID: " + id));

        if (nombre != null && !nombre.isBlank()) {
            zona.setNombre(nombre.trim());
        }
        if (nivelRiesgo != null && !nivelRiesgo.isBlank()) {
            zona.setNivelRiesgo(nivelRiesgo.trim().toUpperCase());
        }
        if (descripcion != null) {
            zona.setDescripcion(descripcion.trim());
        }

        // Actualizar archivo y geometría KML si se adjuntó uno nuevo
        if (file != null && !file.isEmpty()) {
            Polygon nuevoPoligono = extraerPoligonoDeKml(file);
            zona.setPoligono(nuevoPoligono);
            zona.setFuenteDatos(fuenteDatos != null && !fuenteDatos.isBlank() 
                    ? fuenteDatos.trim() 
                    : "Actualizado via KML: " + file.getOriginalFilename());
        } else if (fuenteDatos != null) {
            zona.setFuenteDatos(fuenteDatos.trim());
        }

        if (idTipoVegetacion != null && idTipoVegetacion > 0) {
            TipoVegetacion tipoVegetacion = tipoVegetacionRepo.findById(idTipoVegetacion)
                    .orElseThrow(() -> new RuntimeException("Tipo de vegetación no encontrado"));
            zona.setTipoVegetacion(tipoVegetacion);
        }

        zona.setFechaActualizacion(new Timestamp(System.currentTimeMillis()));
        return zonaRiesgoRepo.save(zona);
    }

    @Override
    @Transactional
    public ZonaRiesgo actualizarConGeometriaWkt(
            Integer id,
            String wkt,
            String nombre,
            String nivelRiesgo,
            String descripcion,
            String fuenteDatos,
            Integer idTipoVegetacion) {

        ZonaRiesgo zona = zonaRiesgoRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Zona de riesgo no encontrada con ID: " + id));

        if (nombre != null && !nombre.isBlank()) zona.setNombre(nombre.trim());
        if (nivelRiesgo != null && !nivelRiesgo.isBlank()) zona.setNivelRiesgo(nivelRiesgo.trim().toUpperCase());
        if (descripcion != null) zona.setDescripcion(descripcion.trim());
        if (fuenteDatos != null) zona.setFuenteDatos(fuenteDatos.trim());

        // Si el usuario redibujó a mano en el mapa del front (en formato WKT)
        if (wkt != null && !wkt.isBlank()) {
            try {
                WKTReader reader = new WKTReader(geometryFactory);
                Polygon poly = (Polygon) reader.read(wkt);
                poly.setSRID(4326);
                zona.setPoligono(poly);
            } catch (Exception e) {
                throw new RuntimeException("Error al interpretar la geometría WKT redibujada: " + e.getMessage());
            }
        }

        if (idTipoVegetacion != null && idTipoVegetacion > 0) {
            TipoVegetacion tipoVegetacion = tipoVegetacionRepo.findById(idTipoVegetacion)
                    .orElseThrow(() -> new RuntimeException("Tipo de vegetación no encontrado"));
            zona.setTipoVegetacion(tipoVegetacion);
        }

        zona.setFechaActualizacion(new Timestamp(System.currentTimeMillis()));
        return zonaRiesgoRepo.save(zona);
    }
}