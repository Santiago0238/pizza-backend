package com.taller.proye01.servicioIMP;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDate;
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
import org.springframework.web.client.RestTemplate;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;


import com.taller.proye01.model.FocoCalor;
import com.taller.proye01.model.Incendio;
import com.taller.proye01.repository.FocoCalorRepo;
import com.taller.proye01.repository.IncendioRepo;
import com.taller.proye01.servicio.FocoCalorService;
//https://firms.modaps.eosdis.nasa.gov/api/area/csv/351afcf9b97b65f2f1d91c4258131f2d/VIIRS_NOAA21_NRT/-65.5,-22.0,-64.5,-21.0/1

@Service
public class FocoCalorServiceImp implements FocoCalorService {

	  private final FocoCalorRepo focoCalorRepository;
	    private final IncendioRepo incendioRepository;

	    private final GeometryFactory geometryFactory =
	            new GeometryFactory(new PrecisionModel(), 4326);

	    private static final String MAP_KEY = "351afcf9b97b65f2f1d91c4258131f2d";
	    private static final String AREA_SAMA = "-65.5,-22.0,-64.5,-21.0";

	    private static final String[] PRODUCTOS_NASA = {
	            "VIIRS_SNPP_NRT",
	            "VIIRS_NOAA20_NRT",
	            "VIIRS_NOAA21_NRT",
	            "MODIS_NRT"
	    };

	    public FocoCalorServiceImp(
	            FocoCalorRepo focoCalorRepository,
	            IncendioRepo incendioRepository) {
	        this.focoCalorRepository = focoCalorRepository;
	        this.incendioRepository = incendioRepository;
	    }

	    @Override
	    @Transactional(readOnly = true)
	    public List<Map<String, Object>> listarFocos() {
	        return focoCalorRepository.listarConIncendio()
	                .stream()
	                .map(this::convertirARespuesta)
	                .toList();
	    }

	    @Override
	    @Transactional(readOnly = true)
	    public Map<String, Object> obtenerPorId(Integer id) {
	        FocoCalor foco = focoCalorRepository.findById(id)
	                .orElseThrow(() -> new RuntimeException("Foco de calor no encontrado"));

	        return convertirARespuesta(foco);
	    }

	    @Override
	    public List<Map<String, Object>> consultarFocosNasaSama() {
	        return obtenerRegistrosDesdeTodasLasFuentes(2)
	                .stream()
	                .map(this::convertirRegistroNasaAMap)
	                .toList();
	    }

	    @Override
	    public List<Map<String, Object>> consultarFocosNasaSamaPorProducto(String producto, Integer dias) {
	        String productoValidado = validarProducto(producto);
	        int diasConsulta = validarDias(dias);

	        return obtenerRegistrosDesdeProducto(productoValidado, diasConsulta)
	                .stream()
	                .map(this::convertirRegistroNasaAMap)
	                .toList();
	    }

	    @Override
	    @Transactional
	    public Map<String, Object> sincronizarFocosNasaSama() {
	        List<RegistroFocoNasa> registros = obtenerRegistrosDesdeTodasLasFuentes(2);
	        return guardarRegistrosNasa(registros);
	    }

	    @Override
	    @Transactional
	    public Map<String, Object> sincronizarFocosNasaSamaPorProducto(String producto, Integer dias) {
	        String productoValidado = validarProducto(producto);
	        int diasConsulta = validarDias(dias);

	        List<RegistroFocoNasa> registros = obtenerRegistrosDesdeProducto(productoValidado, diasConsulta);

	        return guardarRegistrosNasa(registros);
	    }

	    private Map<String, Object> guardarRegistrosNasa(List<RegistroFocoNasa> registros) {
	        int insertados = 0;
	        int duplicados = 0;

	        List<Map<String, Object>> focosGuardados = new ArrayList<>();

	        for (RegistroFocoNasa registro : registros) {

	            boolean existe = focoCalorRepository
	                    .buscarPosibleDuplicado(
	                            registro.fecha(),
	                            registro.satelite(),
	                            registro.producto(),
	                            registro.lat(),
	                            registro.lon()
	                    )
	                    .isPresent();

	            if (existe) {
	                duplicados++;
	                continue;
	            }

	            FocoCalor foco = new FocoCalor();

	            foco.setFecha(registro.fecha());
	            foco.setSatelite(registro.satelite());
	            foco.setConfianza(registro.confianza());
	            foco.setTemperatura(registro.temperatura());
	            foco.setFuente(registro.producto());

	            foco.setScan(registro.scan());
	            foco.setTrack(registro.track());
	            foco.setInstrumento(registro.instrumento());
	            foco.setFrp(registro.frp());
	            foco.setDaynight(registro.daynight());

	            Point punto = geometryFactory.createPoint(
	                    new Coordinate(registro.lon(), registro.lat())
	            );
	            punto.setSRID(4326);

	            foco.setPunto(punto);

	            FocoCalor guardado = focoCalorRepository.save(foco);

	            insertados++;
	            focosGuardados.add(convertirARespuesta(guardado));
	        }

	        Map<String, Object> respuesta = new LinkedHashMap<>();
	        respuesta.put("mensaje", "Sincronización NASA FIRMS completada");
	        respuesta.put("totalRecibidos", registros.size());
	        respuesta.put("insertados", insertados);
	        respuesta.put("duplicados", duplicados);
	        respuesta.put("focos", focosGuardados);

	        return respuesta;
	    }

	    @Override
	    @Transactional(readOnly = true)
	    public List<Map<String, Object>> listarFocosSinIncendio() {
	        return focoCalorRepository.listarSinIncendio()
	                .stream()
	                .map(this::convertirARespuesta)
	                .toList();
	    }

	    @Override
	    @Transactional(readOnly = true)
	    public List<Map<String, Object>> listarFocosPorIncendio(Integer idIncendio) {
	        return focoCalorRepository.listarPorIncendio(idIncendio)
	                .stream()
	                .map(this::convertirARespuesta)
	                .toList();
	    }

	    @Override
	    @Transactional
	    public Map<String, Object> asociarFocoAIncendio(Integer idFoco, Integer idIncendio) {
	        FocoCalor foco = focoCalorRepository.findById(idFoco)
	                .orElseThrow(() -> new RuntimeException("Foco de calor no encontrado"));

	        Incendio incendio = incendioRepository.findById(idIncendio)
	                .orElseThrow(() -> new RuntimeException("Incendio no encontrado"));

	        foco.setIncendio(incendio);

	        FocoCalor actualizado = focoCalorRepository.save(foco);

	        actualizarCantidadFocosIncendio(idIncendio);

	        return convertirARespuesta(actualizado);
	    }

	    @Override
	    @Transactional
	    public Map<String, Object> quitarFocoDeIncendio(Integer idFoco) {
	        FocoCalor foco = focoCalorRepository.findById(idFoco)
	                .orElseThrow(() -> new RuntimeException("Foco de calor no encontrado"));

	        Integer idIncendioAnterior = foco.getIncendio() != null
	                ? foco.getIncendio().getId()
	                : null;

	        foco.setIncendio(null);

	        FocoCalor actualizado = focoCalorRepository.save(foco);

	        if (idIncendioAnterior != null) {
	            actualizarCantidadFocosIncendio(idIncendioAnterior);
	        }

	        return convertirARespuesta(actualizado);
	    }

	    @Override
	    @Transactional(readOnly = true)
	    public List<Map<String, Object>> buscarPorConfianza(String confianza) {
	        if (confianza == null || confianza.isBlank()) {
	            throw new RuntimeException("La confianza es obligatoria");
	        }

	        return focoCalorRepository.buscarPorConfianza(confianza.trim())
	                .stream()
	                .map(this::convertirARespuesta)
	                .toList();
	    }

	    @Override
	    @Transactional(readOnly = true)
	    public List<Map<String, Object>> buscarPorSatelite(String satelite) {
	        if (satelite == null || satelite.isBlank()) {
	            throw new RuntimeException("El satélite es obligatorio");
	        }

	        return focoCalorRepository.buscarPorSatelite(satelite.trim())
	                .stream()
	                .map(this::convertirARespuesta)
	                .toList();
	    }

	    @Override
	    @Transactional(readOnly = true)
	    public List<Map<String, Object>> buscarDentroDeArea(String wktArea) {
	        if (wktArea == null || wktArea.isBlank()) {
	            throw new RuntimeException("El área WKT es obligatoria");
	        }

	        return focoCalorRepository.buscarDentroDeArea(wktArea.trim())
	                .stream()
	                .map(this::convertirARespuesta)
	                .toList();
	    }

	    @Override
	    @Transactional(readOnly = true)
	    public List<Map<String, Object>> buscarCercanos(Double lat, Double lon, Double radioMetros) {
	        if (lat == null || lon == null || radioMetros == null) {
	            throw new RuntimeException("Latitud, longitud y radio son obligatorios");
	        }

	        if (!coordenadaValida(lat, lon)) {
	            throw new RuntimeException("Coordenadas inválidas");
	        }

	        if (radioMetros <= 0) {
	            throw new RuntimeException("El radio debe ser mayor a cero");
	        }

	        return focoCalorRepository.buscarCercanos(lat, lon, radioMetros)
	                .stream()
	                .map(this::convertirARespuesta)
	                .toList();
	    }

	    @Override
	    @Transactional(readOnly = true)
	    public Map<String, Object> listarGeoJSON() {
	        List<Map<String, Object>> features = focoCalorRepository.listarConIncendio()
	                .stream()
	                .filter(f -> f.getPunto() != null)
	                .map(this::convertirAFeatureGeoJSON)
	                .toList();

	        Map<String, Object> geojson = new LinkedHashMap<>();
	        geojson.put("type", "FeatureCollection");
	        geojson.put("features", features);

	        return geojson;
	    }

	    private List<RegistroFocoNasa> obtenerRegistrosDesdeTodasLasFuentes(Integer dias) {
	        List<RegistroFocoNasa> todos = new ArrayList<>();

	        int diasConsulta = validarDias(dias);

	        for (String producto : PRODUCTOS_NASA) {
	            todos.addAll(obtenerRegistrosDesdeProducto(producto, diasConsulta));
	        }

	        return todos;
	    }

	    private List<RegistroFocoNasa> obtenerRegistrosDesdeProducto(String producto, Integer dias) {
	        List<RegistroFocoNasa> registros = new ArrayList<>();
	        RestTemplate restTemplate = new RestTemplate();

	        try {
	            String url = construirUrlNasa(producto, dias);
	            String csv = restTemplate.getForObject(url, String.class);

	            registros = parsearCsvNasa(csv, producto);

	        } catch (Exception e) {
	            System.err.println("Error consultando NASA FIRMS producto " + producto + ": " + e.getMessage());
	        }

	        return registros;
	    }

	    private String construirUrlNasa(String producto, Integer dias) {
	        return "https://firms.modaps.eosdis.nasa.gov/api/area/csv/"
	                + MAP_KEY
	                + "/"
	                + producto
	                + "/"
	                + AREA_SAMA
	                + "/"
	                + dias;
	    }

	    private String validarProducto(String producto) {
	        if (producto == null || producto.isBlank() || producto.equalsIgnoreCase("TODOS")) {
	            return "TODOS";
	        }

	        for (String p : PRODUCTOS_NASA) {
	            if (p.equalsIgnoreCase(producto.trim())) {
	                return p;
	            }
	        }

	        throw new RuntimeException("Producto NASA FIRMS no válido: " + producto);
	    }

	    private int validarDias(Integer dias) {
	        if (dias == null) {
	            return 2;
	        }

	        if (dias < 1) {
	            return 1;
	        }

	        if (dias > 10) {
	            return 10;
	        }

	        return dias;
	    }

	    private List<RegistroFocoNasa> parsearCsvNasa(String csv, String producto) {
	        List<RegistroFocoNasa> registros = new ArrayList<>();

	        if (csv == null || csv.isBlank()) {
	            return registros;
	        }

	        String[] lineas = csv.split("\\r?\\n");

	        if (lineas.length <= 1) {
	            return registros;
	        }

	        for (int i = 1; i < lineas.length; i++) {
	            String linea = lineas[i].trim();

	            if (linea.isBlank()) {
	                continue;
	            }

	            String[] columnas = linea.split(",");

	            if (columnas.length < 14) {
	                continue;
	            }

	            try {
	                Double lat = convertirDouble(columnas[0]);
	                Double lon = convertirDouble(columnas[1]);

	                BigDecimal brightTi4 = convertirBigDecimal(columnas[2]);
	                BigDecimal scan = convertirBigDecimal(columnas[3]);
	                BigDecimal track = convertirBigDecimal(columnas[4]);

	                String acqDate = columnas[5].trim();
	                String acqTime = columnas[6].trim();
	                String satellite = columnas[7].trim();
	                String instrument = columnas[8].trim();
	                String confidence = columnas[9].trim();

	                BigDecimal brightTi5 = convertirBigDecimal(columnas[11]);
	                BigDecimal frp = convertirBigDecimal(columnas[12]);

	                String daynight = columnas[13].trim();

	                Timestamp fecha = convertirFechaNasa(acqDate, acqTime);

	                if (lat == null || lon == null || !coordenadaValida(lat, lon)) {
	                    continue;
	                }

	                registros.add(new RegistroFocoNasa(
	                        lat,
	                        lon,
	                        brightTi4,
	                        scan,
	                        track,
	                        fecha,
	                        satellite,
	                        instrument,
	                        confidence,
	                        brightTi5,
	                        frp,
	                        daynight,
	                        producto
	                ));

	            } catch (Exception e) {
	                System.err.println("Fila NASA ignorada: " + linea + " | " + e.getMessage());
	            }
	        }

	        return registros;
	    }

	    private static final ZoneId ZONA_UTC = ZoneId.of("UTC");
	    private static final ZoneId ZONA_BOLIVIA = ZoneId.of("America/La_Paz"); // GMT-04:00

	    private Timestamp convertirFechaNasa(String fecha, String hora) {
	        if (fecha == null || fecha.isBlank()) {
	            throw new RuntimeException("Fecha NASA inválida");
	        }

	        if (hora == null || hora.isBlank()) {
	            throw new RuntimeException("Hora NASA inválida");
	        }

	        String horaNormalizada = hora.trim();
	        while (horaNormalizada.length() < 4) {
	            horaNormalizada = "0" + horaNormalizada;
	        }

	        int horas = Integer.parseInt(horaNormalizada.substring(0, 2));
	        int minutos = Integer.parseInt(horaNormalizada.substring(2, 4));

	        // 1. Interpretar la fecha y hora de adquisición como UTC
	        LocalDate fechaUtc = LocalDate.parse(fecha.trim());
	        LocalTime horaUtc = LocalTime.of(horas, minutos, 0);
	        ZonedDateTime adquisicionUtc = ZonedDateTime.of(fechaUtc, horaUtc, ZONA_UTC);

	        // 2. Convertir a la hora local de Bolivia (UTC - 4)
	        ZonedDateTime adquisicionBolivia = adquisicionUtc.withZoneSameInstant(ZONA_BOLIVIA);

	        // 3. Retornar el Timestamp ajustado a la hora local
	        return Timestamp.valueOf(adquisicionBolivia.toLocalDateTime());
	    }
	    private void actualizarCantidadFocosIncendio(Integer idIncendio) {
	        Incendio incendio = incendioRepository.findById(idIncendio)
	                .orElseThrow(() -> new RuntimeException("Incendio no encontrado"));

	        int cantidad = focoCalorRepository.listarPorIncendio(idIncendio).size();

	        incendio.setCantidadFocos(cantidad);

	        incendioRepository.save(incendio);
	    }

	    private Map<String, Object> convertirARespuesta(FocoCalor foco) {
	        Map<String, Object> respuesta = new LinkedHashMap<>();

	        respuesta.put("id", foco.getId());
	        respuesta.put("fecha", foco.getFecha());
	        respuesta.put("satelite", foco.getSatelite());
	        respuesta.put("confianza", foco.getConfianza());
	        respuesta.put("temperatura", foco.getTemperatura());
	        respuesta.put("fuente", foco.getFuente());

	        respuesta.put("scan", foco.getScan());
	        respuesta.put("track", foco.getTrack());
	        respuesta.put("instrumento", foco.getInstrumento());
	        respuesta.put("frp", foco.getFrp());
	        respuesta.put("daynight", foco.getDaynight());

	        Point punto = foco.getPunto();

	        if (punto != null) {
	            respuesta.put("lat", punto.getY());
	            respuesta.put("lon", punto.getX());

	            Map<String, Object> puntoGeoJson = new LinkedHashMap<>();
	            puntoGeoJson.put("type", "Point");
	            puntoGeoJson.put("coordinates", List.of(punto.getX(), punto.getY()));

	            respuesta.put("punto", puntoGeoJson);
	        } else {
	            respuesta.put("lat", null);
	            respuesta.put("lon", null);
	            respuesta.put("punto", null);
	        }

	        if (foco.getIncendio() != null) {
	            Map<String, Object> incendioMap = new LinkedHashMap<>();
	            incendioMap.put("id", foco.getIncendio().getId());
	            incendioMap.put("nombre", foco.getIncendio().getNombre());
	            incendioMap.put("estado", foco.getIncendio().getEstado());

	            respuesta.put("idIncendio", foco.getIncendio().getId());
	            respuesta.put("incendio", incendioMap);
	        } else {
	            respuesta.put("idIncendio", null);
	            respuesta.put("incendio", null);
	        }

	        return respuesta;
	    }

	    private Map<String, Object> convertirAFeatureGeoJSON(FocoCalor foco) {
	        Point punto = foco.getPunto();

	        Map<String, Object> geometry = new LinkedHashMap<>();
	        geometry.put("type", "Point");
	        geometry.put("coordinates", List.of(punto.getX(), punto.getY()));

	        Map<String, Object> properties = new LinkedHashMap<>();
	        properties.put("id", foco.getId());
	        properties.put("fecha", foco.getFecha());
	        properties.put("satelite", foco.getSatelite());
	        properties.put("confianza", foco.getConfianza());
	        properties.put("temperatura", foco.getTemperatura());
	        properties.put("fuente", foco.getFuente());

	        properties.put("scan", foco.getScan());
	        properties.put("track", foco.getTrack());
	        properties.put("instrumento", foco.getInstrumento());
	        properties.put("frp", foco.getFrp());
	        properties.put("daynight", foco.getDaynight());

	        if (foco.getIncendio() != null) {
	            properties.put("idIncendio", foco.getIncendio().getId());
	            properties.put("incendio", foco.getIncendio().getNombre());
	            properties.put("estadoIncendio", foco.getIncendio().getEstado());
	        }

	        Map<String, Object> feature = new LinkedHashMap<>();
	        feature.put("type", "Feature");
	        feature.put("id", foco.getId());
	        feature.put("properties", properties);
	        feature.put("geometry", geometry);

	        return feature;
	    }

	    private Map<String, Object> convertirRegistroNasaAMap(RegistroFocoNasa registro) {
	        Map<String, Object> map = new LinkedHashMap<>();

	        map.put("lat", registro.lat());
	        map.put("lon", registro.lon());
	        map.put("temperatura", registro.temperatura());
	        map.put("scan", registro.scan());
	        map.put("track", registro.track());
	        map.put("fecha", registro.fecha());
	        map.put("satelite", registro.satelite());
	        map.put("instrumento", registro.instrumento());
	        map.put("confianza", registro.confianza());
	        map.put("brightTi5", registro.brightTi5());
	        map.put("frp", registro.frp());
	        map.put("daynight", registro.daynight());
	        map.put("fuente", registro.producto());

	        Map<String, Object> punto = new LinkedHashMap<>();
	        punto.put("type", "Point");
	        punto.put("coordinates", List.of(registro.lon(), registro.lat()));

	        map.put("punto", punto);

	        return map;
	    }

	    private boolean coordenadaValida(Double lat, Double lon) {
	        return lat >= -90 && lat <= 90 && lon >= -180 && lon <= 180;
	    }

	    private Double convertirDouble(String valor) {
	        if (valor == null || valor.isBlank()) {
	            return null;
	        }

	        return Double.parseDouble(valor.trim());
	    }

	    private BigDecimal convertirBigDecimal(String valor) {
	        if (valor == null || valor.isBlank()) {
	            return null;
	        }

	        return new BigDecimal(valor.trim());
	    }

	    private record RegistroFocoNasa(
	            Double lat,
	            Double lon,
	            BigDecimal temperatura,
	            BigDecimal scan,
	            BigDecimal track,
	            Timestamp fecha,
	            String satelite,
	            String instrumento,
	            String confianza,
	            BigDecimal brightTi5,
	            BigDecimal frp,
	            String daynight,
	            String producto
	    ) {
	    }
	    
	    private static final String[] PRODUCTOS_NASA_SP = {
	            "VIIRS_SNPP_SP",
	            "VIIRS_NOAA20_SP",
	            "VIIRS_NOAA21_NRT", // NOAA-21 usa NRT mientras esté en transición
	            "MODIS_SP"
	    };
	    

	    @Override
	    public List<Map<String, Object>> consultarFocosNasaPorFecha(String producto, Integer dias, LocalDate fecha) {
	        validarFechaConsulta(fecha, dias);
	        String prodValidado = validarProductoHistorico(producto);
	        int diasConsulta = validarDias(dias);

	        return obtenerRegistrosDesdeProductoPorFecha(prodValidado, diasConsulta, fecha)
	                .stream()
	                .map(this::convertirRegistroNasaAMap)
	                .toList();
	    }

	    @Override
	    @Transactional
	    public Map<String, Object> sincronizarFocosNasaPorFecha(String producto, Integer dias, LocalDate fecha) {
	        validarFechaConsulta(fecha, dias);
	        String prodValidado = validarProductoHistorico(producto);
	        int diasConsulta = validarDias(dias);

	        List<RegistroFocoNasa> registros = obtenerRegistrosDesdeProductoPorFecha(prodValidado, diasConsulta, fecha);
	        return guardarRegistrosNasa(registros);
	    }
	    

	    private List<RegistroFocoNasa> obtenerRegistrosDesdeProductoPorFecha(String producto, Integer dias, LocalDate fecha) {
	        List<RegistroFocoNasa> registros = new ArrayList<>();
	        RestTemplate restTemplate = new RestTemplate();

	        // Si la fecha es de los últimos 7 días, usar catálogo NRT; si es anterior, usar SP
	        boolean esReciente = !fecha.isBefore(LocalDate.now().minusDays(7));
	        String[] catalogoAUsar = esReciente ? PRODUCTOS_NASA : PRODUCTOS_NASA_SP;

	        try {
	            if ("TODOS".equalsIgnoreCase(producto)) {
	                for (String p : catalogoAUsar) {
	                    String url = construirUrlNasaPorFecha(p, dias, fecha);
	                    String csv = restTemplate.getForObject(url, String.class);
	                    registros.addAll(parsearCsvNasa(csv, p));
	                }
	            } else {
	                // Ajustar sufijo según la antigüedad de la fecha
	                String prodAjustado = esReciente 
	                        ? producto.replace("_SP", "_NRT") 
	                        : producto.replace("_NRT", "_SP");

	                String url = construirUrlNasaPorFecha(prodAjustado, dias, fecha);
	                String csv = restTemplate.getForObject(url, String.class);
	                registros = parsearCsvNasa(csv, prodAjustado);
	            }
	        } catch (Exception e) {
	            System.err.println("Error consultando NASA FIRMS para la fecha " + fecha + ": " + e.getMessage());
	        }

	        return registros;
	    }
	    private String construirUrlNasaPorFecha(String producto, Integer dias, LocalDate fecha) {
	        String urlBase = "https://firms.modaps.eosdis.nasa.gov/api/area/csv/"
	                + MAP_KEY + "/"
	                + producto + "/"
	                + AREA_SAMA + "/"
	                + dias;

	        if (fecha != null) {
	            urlBase += "/" + fecha.toString(); // Genera YYYY-MM-DD
	        }
	        return urlBase;
	    }
	    private void validarFechaConsulta(LocalDate fecha, Integer dias) {
	        if (fecha == null) {
	            throw new IllegalArgumentException("La fecha de consulta no puede ser nula");
	        }

	        LocalDate hoy = LocalDate.now();

	        if (fecha.isAfter(hoy)) {
	            throw new IllegalArgumentException("No se pueden consultar fechas futuras a la actual (" + hoy + ")");
	        }

	        // Validación adicional: fecha + rango de días no debe pasarse del día presente
	        int diasOffset = (dias != null && dias > 0) ? dias - 1 : 0;
	        if (fecha.plusDays(diasOffset).isAfter(hoy)) {
	            throw new IllegalArgumentException("El rango de " + dias + " días a partir de " + fecha + " supera la fecha actual");
	        }
	    }
	    

	    private String validarProductoHistorico(String producto) {
	        if (producto == null || producto.isBlank() || producto.equalsIgnoreCase("TODOS")) {
	            return "TODOS";
	        }
	        // Si mandan NRT, convertir a SP para histórico
	        String normalizado = producto.trim().toUpperCase().replace("_NRT", "_SP");
	        for (String p : PRODUCTOS_NASA_SP) {
	            if (p.equalsIgnoreCase(normalizado)) {
	                return p;
	            }
	        }
	        return normalizado;
	    }
	}