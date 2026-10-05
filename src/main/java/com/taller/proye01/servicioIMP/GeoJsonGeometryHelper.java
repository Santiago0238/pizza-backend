package com.taller.proye01.servicioIMP;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.locationtech.jts.geom.*;
import org.locationtech.jts.io.WKTReader;

public class GeoJsonGeometryHelper {

    private static final GeometryFactory GEOMETRY_FACTORY =
            new GeometryFactory(new PrecisionModel(), 4326);

    private GeoJsonGeometryHelper() {
    }

    @SuppressWarnings("unchecked")
    public static Geometry convertirGeoJsonAGeometry(Map<String, Object> geojson) {
        if (geojson == null) {
            throw new RuntimeException("La geometría GeoJSON es obligatoria");
        }

        String type = String.valueOf(geojson.get("type"));
        Object coordinatesObj = geojson.get("coordinates");

        if (!(coordinatesObj instanceof List<?> coordinates)) {
            throw new RuntimeException("La geometría GeoJSON no tiene coordenadas válidas");
        }

        Geometry geometry;

        switch (type) {
            case "Point" -> geometry = crearPoint((List<Object>) coordinates);
            case "LineString" -> geometry = crearLineString((List<Object>) coordinates);
            case "Polygon" -> geometry = crearPolygon((List<Object>) coordinates);
            case "MultiPoint" -> geometry = crearMultiPoint((List<Object>) coordinates);
            case "MultiLineString" -> geometry = crearMultiLineString((List<Object>) coordinates);
            case "MultiPolygon" -> geometry = crearMultiPolygon((List<Object>) coordinates);
            default -> throw new RuntimeException("Tipo de geometría no soportado: " + type);
        }

        geometry.setSRID(4326);

        return geometry;
    }

    public static Geometry convertirWktAGeometry(String wkt) {
        try {
            WKTReader reader = new WKTReader(GEOMETRY_FACTORY);
            Geometry geometry = reader.read(wkt);
            geometry.setSRID(4326);
            return geometry;
        } catch (Exception e) {
            throw new RuntimeException("WKT inválido: " + e.getMessage());
        }
    }

    public static Map<String, Object> convertirGeometryAGeoJson(Geometry geometry) {
        if (geometry == null) {
            return null;
        }

        Map<String, Object> geojson = new LinkedHashMap<>();

        geojson.put("type", geometry.getGeometryType());
        geojson.put("coordinates", convertirCoordenadas(geometry));

        return geojson;
    }

    private static Point crearPoint(List<Object> coords) {
        Coordinate coordinate = crearCoordinate(coords);
        Point point = GEOMETRY_FACTORY.createPoint(coordinate);
        point.setSRID(4326);
        return point;
    }

    private static LineString crearLineString(List<Object> coords) {
        Coordinate[] coordinates = crearCoordinateArray(coords);
        LineString lineString = GEOMETRY_FACTORY.createLineString(coordinates);
        lineString.setSRID(4326);
        return lineString;
    }

    private static Polygon crearPolygon(List<Object> rings) {
        if (rings.isEmpty()) {
            throw new RuntimeException("El Polygon no tiene anillos");
        }

        LinearRing shell = GEOMETRY_FACTORY.createLinearRing(
                crearCoordinateArray((List<Object>) rings.get(0))
        );

        LinearRing[] holes = new LinearRing[Math.max(0, rings.size() - 1)];

        for (int i = 1; i < rings.size(); i++) {
            holes[i - 1] = GEOMETRY_FACTORY.createLinearRing(
                    crearCoordinateArray((List<Object>) rings.get(i))
            );
        }

        Polygon polygon = GEOMETRY_FACTORY.createPolygon(shell, holes);
        polygon.setSRID(4326);

        return polygon;
    }

    private static MultiPoint crearMultiPoint(List<Object> coords) {
        Point[] points = coords.stream()
                .map(c -> crearPoint((List<Object>) c))
                .toArray(Point[]::new);

        MultiPoint multiPoint = GEOMETRY_FACTORY.createMultiPoint(points);
        multiPoint.setSRID(4326);

        return multiPoint;
    }

    private static MultiLineString crearMultiLineString(List<Object> lines) {
        LineString[] lineStrings = lines.stream()
                .map(l -> crearLineString((List<Object>) l))
                .toArray(LineString[]::new);

        MultiLineString multiLineString = GEOMETRY_FACTORY.createMultiLineString(lineStrings);
        multiLineString.setSRID(4326);

        return multiLineString;
    }

    private static MultiPolygon crearMultiPolygon(List<Object> polygons) {
        Polygon[] polygonArray = polygons.stream()
                .map(p -> crearPolygon((List<Object>) p))
                .toArray(Polygon[]::new);

        MultiPolygon multiPolygon = GEOMETRY_FACTORY.createMultiPolygon(polygonArray);
        multiPolygon.setSRID(4326);

        return multiPolygon;
    }

    private static Coordinate crearCoordinate(List<Object> coords) {
        if (coords.size() < 2) {
            throw new RuntimeException("Coordenada incompleta");
        }

        double lon = convertirDouble(coords.get(0));
        double lat = convertirDouble(coords.get(1));

        if (coords.size() >= 3) {
            double z = convertirDouble(coords.get(2));
            return new Coordinate(lon, lat, z);
        }

        return new Coordinate(lon, lat);
    }

    private static Coordinate[] crearCoordinateArray(List<Object> coords) {
        return coords.stream()
                .map(c -> crearCoordinate((List<Object>) c))
                .toArray(Coordinate[]::new);
    }

    private static Double convertirDouble(Object valor) {
        if (valor instanceof Number number) {
            return number.doubleValue();
        }

        return Double.parseDouble(String.valueOf(valor));
    }

    private static Object convertirCoordenadas(Geometry geometry) {
        String type = geometry.getGeometryType();

        return switch (type) {
            case "Point" -> coordenadaToList(geometry.getCoordinate());
            case "LineString", "LinearRing" -> coordinateSequenceToList(geometry.getCoordinates());
            case "Polygon" -> polygonToList((Polygon) geometry);
            case "MultiPoint" -> multiPointToList((MultiPoint) geometry);
            case "MultiLineString" -> multiLineStringToList((MultiLineString) geometry);
            case "MultiPolygon" -> multiPolygonToList((MultiPolygon) geometry);
            default -> null;
        };
    }

    private static List<Double> coordenadaToList(Coordinate c) {
        List<Double> list = new ArrayList<>();

        list.add(c.getX());
        list.add(c.getY());

        if (!Double.isNaN(c.getZ())) {
            list.add(c.getZ());
        }

        return list;
    }

    private static List<List<Double>> coordinateSequenceToList(Coordinate[] coords) {
        return List.of(coords)
                .stream()
                .map(GeoJsonGeometryHelper::coordenadaToList)
                .toList();
    }

    private static List<Object> polygonToList(Polygon polygon) {
        List<Object> rings = new ArrayList<>();

        rings.add(coordinateSequenceToList(polygon.getExteriorRing().getCoordinates()));

        for (int i = 0; i < polygon.getNumInteriorRing(); i++) {
            rings.add(coordinateSequenceToList(polygon.getInteriorRingN(i).getCoordinates()));
        }

        return rings;
    }

    private static List<Object> multiPointToList(MultiPoint multiPoint) {
        List<Object> coords = new ArrayList<>();

        for (int i = 0; i < multiPoint.getNumGeometries(); i++) {
            coords.add(coordenadaToList(multiPoint.getGeometryN(i).getCoordinate()));
        }

        return coords;
    }

    private static List<Object> multiLineStringToList(MultiLineString multiLineString) {
        List<Object> lines = new ArrayList<>();

        for (int i = 0; i < multiLineString.getNumGeometries(); i++) {
            lines.add(coordinateSequenceToList(multiLineString.getGeometryN(i).getCoordinates()));
        }

        return lines;
    }

    private static List<Object> multiPolygonToList(MultiPolygon multiPolygon) {
        List<Object> polygons = new ArrayList<>();

        for (int i = 0; i < multiPolygon.getNumGeometries(); i++) {
            polygons.add(polygonToList((Polygon) multiPolygon.getGeometryN(i)));
        }

        return polygons;
    }
}