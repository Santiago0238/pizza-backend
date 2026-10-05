package com.taller.proye01.servicioIMP;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Timestamp;
import java.time.OffsetDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.InputStream;
import java.util.List;
import java.util.Map;

import org.locationtech.jts.geom.Coordinate;
import org.locationtech.jts.geom.GeometryFactory;
import org.locationtech.jts.geom.LineString;
import org.locationtech.jts.geom.Point;
import org.locationtech.jts.geom.PrecisionModel;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.w3c.dom.NodeList;

import com.taller.proye01.DTO.RutaDTO;
import com.taller.proye01.model.PuntoRuta;
import com.taller.proye01.model.Ruta;
import com.taller.proye01.repository.PuntoRutaRepo;
import com.taller.proye01.repository.RutaRepo;
import com.taller.proye01.servicio.RutaService;
import java.nio.charset.StandardCharsets;

import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;

@Service
public class RutaServiceIMP implements RutaService {

	private final RutaRepo rutaRepo;
	private final PuntoRutaRepo puntoRutaRepo;

	private final GeometryFactory geometryFactory = new GeometryFactory(new PrecisionModel(), 4326);

	public RutaServiceIMP(RutaRepo rutaRepo, PuntoRutaRepo puntoRutaRepo) {
		this.rutaRepo = rutaRepo;
		this.puntoRutaRepo = puntoRutaRepo;
	}

	@Override
	@Transactional(readOnly = true)
	public List<RutaDTO> listarRutas() {
		return rutaRepo.findAllSimplificado();
	}

	@Override
	@Transactional(readOnly = true)
	public Map<String, Object> listarComoGeoJSON() {

		List<RutaDTO> rutas = rutaRepo.findAllSimplificado();

		Map<String, Object> featureCollection = new HashMap<>();
		featureCollection.put("type", "FeatureCollection");

		List<Map<String, Object>> features = new ArrayList<>();

		for (RutaDTO rutaDto : rutas) {

			// =========================================================
			// 1. LINEA PRINCIPAL DE LA RUTA
			// =========================================================

			String wkt = rutaDto.getRutaLinea();

			if (wkt != null && !wkt.trim().isEmpty()) {

				try {

					Map<String, Object> featureLinea = new HashMap<>();
					featureLinea.put("type", "Feature");
					featureLinea.put("id", "ruta_" + rutaDto.getId());

					Map<String, Object> properties = new HashMap<>();

					properties.put("tipo", "ruta");
					properties.put("ruta_id", rutaDto.getId());
					properties.put("nombre", rutaDto.getNombre());
					properties.put("descripcion", rutaDto.getDescripcion());
					properties.put("distancia_km", rutaDto.getDistanciaKm());
					properties.put("usuario_id", rutaDto.getUsuarioId());
					properties.put("fecha_creacion", rutaDto.getFechaCreacion());

					featureLinea.put("properties", properties);

					Map<String, Object> geometryLinea = new HashMap<>();
					geometryLinea.put("type", "LineString");
					geometryLinea.put("coordinates", convertirWktACoordenadas(wkt));

					featureLinea.put("geometry", geometryLinea);

					features.add(featureLinea);

				} catch (Exception e) {

					System.err.println("Error parseando línea de ruta " + rutaDto.getId() + ": " + e.getMessage());
				}
			}

			// =========================================================
			// 2. PUNTOS DE INTERES / WAYPOINTS
			// =========================================================

			try {

				List<PuntoRuta> puntosRuta = puntoRutaRepo.findByRutaIdOrderByOrdenAsc(rutaDto.getId());

				for (PuntoRuta punto : puntosRuta) {

					if (punto.getPunto() == null) {
						continue;
					}

					// Feature independiente para cada punto
					Map<String, Object> featurePunto = new HashMap<>();

					featurePunto.put("type", "Feature");
					featurePunto.put("id", "punto_" + punto.getId());

					// -------------------------------------------------
					// PROPIEDADES DEL PUNTO
					// -------------------------------------------------

					Map<String, Object> propPunto = new HashMap<>();

					propPunto.put("tipo", "punto_interes");

					propPunto.put("ruta_id", rutaDto.getId());

					propPunto.put("ruta_nombre", rutaDto.getNombre());

					propPunto.put("nombre",
							punto.getNombre() != null && !punto.getNombre().isBlank() ? punto.getNombre()
									: "Punto " + punto.getOrden());

					propPunto.put("descripcion", punto.getDescripcion());

					propPunto.put("orden", punto.getOrden());

					propPunto.put("elevacion", punto.getElevacion());

					propPunto.put("tiempo", punto.getTiempo());

					featurePunto.put("properties", propPunto);

					// -------------------------------------------------
					// GEOMETRIA DEL PUNTO
					// -------------------------------------------------

					Map<String, Object> geometryPunto = new HashMap<>();

					geometryPunto.put("type", "Point");

					double elevacion = 0.0;

					if (punto.getElevacion() != null) {
						elevacion = punto.getElevacion();
					} else if (!Double.isNaN(punto.getPunto().getCoordinate().getZ())) {
						elevacion = punto.getPunto().getCoordinate().getZ();
					}

					double[] coordenadasPunto = { punto.getPunto().getX(), punto.getPunto().getY(), elevacion };

					geometryPunto.put("coordinates", coordenadasPunto);

					featurePunto.put("geometry", geometryPunto);

					// Agregar el punto al FeatureCollection
					features.add(featurePunto);
				}

			} catch (Exception e) {

				System.err.println("Error cargando puntos de la ruta " + rutaDto.getId() + ": " + e.getMessage());
			}
		}

		// =============================================================
		// FEATURE COLLECTION FINAL
		// =============================================================

		featureCollection.put("features", features);

		return featureCollection;
	}

	@Override
	@Transactional
	public Map<String, Object> registrarRuta(Map<String, Object> payload) {
		String nombre = (String) payload.get("nombre");
		String descripcion = (String) payload.get("descripcion");

		Integer usuarioId = obtenerUsuarioId(payload);

		@SuppressWarnings("unchecked")
		List<List<Object>> listaCoords = (List<List<Object>>) payload.get("coordenadas");

		if (listaCoords == null || listaCoords.size() < 2) {
			throw new RuntimeException("La ruta debe tener al menos dos puntos");
		}

		// 1. Guardar la línea principal de la ruta
		Coordinate[] coordinates = construirCoordenadas(listaCoords);
		LineString lineString = geometryFactory.createLineString(coordinates);

		Ruta ruta = new Ruta();
		ruta.setNombre(nombre);
		ruta.setDescripcion(descripcion);
		ruta.setFechaCreacion(new Timestamp(System.currentTimeMillis()));
		ruta.setRutaLinea(lineString);
		ruta.setDistanciaKm(calcularDistanciaKm(coordinates));
		ruta.setUsuarioId(usuarioId);

		Ruta rutaGuardada = rutaRepo.save(ruta);

		// 2. NUEVO: Verificar si vienen Puntos de Interés (Waypoints) explícitos en el
		// payload
		@SuppressWarnings("unchecked")
		List<Map<String, Object>> listaPuntosInteres = (List<Map<String, Object>>) payload.get("puntosInteres");

		if (listaPuntosInteres != null && !listaPuntosInteres.isEmpty()) {
			// Si el cliente envía los puntos de interés parseados del KML
			int orden = 1;
			for (Map<String, Object> pDto : listaPuntosInteres) {
				PuntoRuta puntoRuta = new PuntoRuta();
				puntoRuta.setRuta(rutaGuardada);
				puntoRuta.setOrden(orden++);

				// Mapear nombre y descripción del punto de referencia
				puntoRuta.setNombre((String) pDto.get("nombre"));
				puntoRuta.setDescripcion((String) pDto.get("descripcion"));

				double lon = Double.parseDouble(pDto.get("longitud").toString());
				double lat = Double.parseDouble(pDto.get("latitud").toString());
				double alt = pDto.get("elevacion") != null ? Double.parseDouble(pDto.get("elevacion").toString()) : 0.0;

				Point punto = geometryFactory.createPoint(new Coordinate(lon, lat, alt));
				puntoRuta.setPunto(punto);
				puntoRuta.setElevacion(alt);

				puntoRutaRepo.save(puntoRuta);
			}
		} else {
			// Fallback anterior: Si no hay puntos de interés específicos, guarda los
			// vértices de la línea
			for (int i = 0; i < coordinates.length; i++) {
				PuntoRuta puntoRuta = new PuntoRuta();
				puntoRuta.setRuta(rutaGuardada);
				puntoRuta.setOrden(i + 1);

				Point punto = geometryFactory.createPoint(coordinates[i]);
				puntoRuta.setPunto(punto);
				puntoRuta.setElevacion(coordinates[i].z);

				puntoRutaRepo.save(puntoRuta);
			}
		}

		return Map.of("mensaje", "Ruta registrada correctamente", "id", rutaGuardada.getId());
	}

	@Override
	@Transactional
	public Map<String, Object> editarRuta(Integer id, Ruta datosActualizados) {
		Ruta ruta = rutaRepo.findById(id).orElseThrow(() -> new RuntimeException("Ruta no encontrada"));

		ruta.setNombre(datosActualizados.getNombre());
		ruta.setDescripcion(datosActualizados.getDescripcion());

		rutaRepo.save(ruta);

		return Map.of("mensaje", "Ruta actualizada correctamente");
	}

	@Override
	@Transactional(readOnly = true)
	public List<RutaDTO> filtrarRutas(String wktArea) {
		return rutaRepo.filtrarPorAreaEspacial(wktArea);
	}

	@Override
	@Transactional
	public Map<String, Object> eliminarRuta(Integer id) {
		if (!rutaRepo.existsById(id)) {
			throw new RuntimeException("La ruta no existe");
		}

		puntoRutaRepo.deleteByRutaId(id);
		rutaRepo.deleteById(id);

		return Map.of("mensaje", "Ruta eliminada correctamente");
	}

	private Integer obtenerUsuarioId(Map<String, Object> payload) {
		Object usuarioIdObj = payload.get("usuarioId");
		if (usuarioIdObj == null) {
			return 1;
		}
		return Integer.parseInt(usuarioIdObj.toString());
	}

	private Coordinate[] construirCoordenadas(List<List<Object>> listaCoords) {
		Coordinate[] coordinates = new Coordinate[listaCoords.size()];

		for (int i = 0; i < listaCoords.size(); i++) {
			List<Object> puntoClave = listaCoords.get(i);

			double lon = Double.parseDouble(puntoClave.get(0).toString());
			double lat = Double.parseDouble(puntoClave.get(1).toString());
			double alt = puntoClave.size() > 2 ? Double.parseDouble(puntoClave.get(2).toString()) : 0.0;

			coordinates[i] = new Coordinate(lon, lat, alt);
		}

		return coordinates;
	}

	private BigDecimal calcularDistanciaKm(Coordinate[] coordinates) {
		double distanciaTotalKm = 0.0;

		for (int i = 1; i < coordinates.length; i++) {
			distanciaTotalKm += calcularDistanciaHaversine(coordinates[i - 1].y, coordinates[i - 1].x, coordinates[i].y,
					coordinates[i].x);
		}

		return BigDecimal.valueOf(distanciaTotalKm).setScale(2, RoundingMode.HALF_UP);
	}

	private double calcularDistanciaHaversine(double lat1, double lon1, double lat2, double lon2) {
		final double radioTierraKm = 6371.0;

		double dLat = Math.toRadians(lat2 - lat1);
		double dLon = Math.toRadians(lon2 - lon1);

		double a = Math.sin(dLat / 2) * Math.sin(dLat / 2) + Math.cos(Math.toRadians(lat1))
				* Math.cos(Math.toRadians(lat2)) * Math.sin(dLon / 2) * Math.sin(dLon / 2);

		double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));

		return radioTierraKm * c;
	}

	private List<double[]> convertirWktACoordenadas(String wkt) {
		List<double[]> coords = new ArrayList<>();

		String contenidoPuntos = wkt.substring(wkt.indexOf("(") + 1, wkt.lastIndexOf(")"));

		String[] puntosTuplas = contenidoPuntos.split(",");

		for (String tupla : puntosTuplas) {
			String[] ejes = tupla.trim().split("\\s+");

			if (ejes.length >= 2) {
				double lon = Double.parseDouble(ejes[0]);
				double lat = Double.parseDouble(ejes[1]);
				double alt = ejes.length > 2 ? Double.parseDouble(ejes[2]) : 0.0;

				coords.add(new double[] { lon, lat, alt });
			}
		}

		return coords;
	}

	@Override
	public ResponseEntity<byte[]> exportarKml(Integer id) {
		Ruta ruta = rutaRepo.findById(id)
				.orElseThrow(() -> new RuntimeException("No se encontró la ruta con ID: " + id));

		LineString linea = ruta.getRutaLinea();

		if (linea == null || linea.isEmpty()) {
			throw new RuntimeException("La ruta con ID " + id + " no tiene geometría para exportar");
		}

		Coordinate[] coordenadas = linea.getCoordinates();
		StringBuilder coordenadasKml = new StringBuilder();

		for (Coordinate coordenada : coordenadas) {
			double longitud = coordenada.getX();
			double latitud = coordenada.getY();
			double altitud = coordenada.getZ();

			if (Double.isNaN(altitud)) {
				altitud = 0;
			}

			coordenadasKml.append(longitud).append(",").append(latitud).append(",").append(altitud).append("\n");
		}

		String nombre = escaparXml(ruta.getNombre() != null ? ruta.getNombre() : "Ruta " + ruta.getId());

		String descripcion = escaparXml(ruta.getDescripcion() != null ? ruta.getDescripcion() : "");

		String kml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" + "<kml xmlns=\"http://www.opengis.net/kml/2.2\">\n"
				+ "  <Document>\n" + "    <name>" + nombre + "</name>\n" + "    <Placemark>\n" + "      <name>" + nombre
				+ "</name>\n" + "      <description>" + descripcion + "</description>\n" + "      <Style>\n"
				+ "        <LineStyle>\n" + "          <color>ff0000ff</color>\n" + "          <width>4</width>\n"
				+ "        </LineStyle>\n" + "      </Style>\n" + "      <LineString>\n"
				+ "        <tessellate>1</tessellate>\n" + "        <altitudeMode>clampToGround</altitudeMode>\n"
				+ "        <coordinates>\n" + coordenadasKml + "        </coordinates>\n" + "      </LineString>\n"
				+ "    </Placemark>\n" + "  </Document>\n" + "</kml>";

		byte[] archivo = kml.getBytes(StandardCharsets.UTF_8);

		String nombreArchivo = ruta.getNombre() != null
				? ruta.getNombre().trim().replaceAll("[^a-zA-Z0-9áéíóúÁÉÍÓÚñÑ_-]", "_")
				: "ruta_" + ruta.getId();

		nombreArchivo += ".kml";

		HttpHeaders headers = new HttpHeaders();
		headers.setContentType(MediaType.parseMediaType("application/vnd.google-earth.kml+xml"));
		headers.setContentDisposition(
				ContentDisposition.attachment().filename(nombreArchivo, StandardCharsets.UTF_8).build());
		headers.setContentLength(archivo.length);

		return ResponseEntity.ok().headers(headers).body(archivo);
	}

	@Override
	@Transactional
	public Map<String, Object> registrarRutaDesdeKml(InputStream kmlInputStream, Integer usuarioId, String nombreRuta,
			String descripcionRuta) {

		try {

			// =========================================================
			// 1. PARSEAR KML
			// =========================================================

			DocumentBuilderFactory dbFactory = DocumentBuilderFactory.newInstance();

			// Soporte para KML con namespaces
			dbFactory.setNamespaceAware(true);

			DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();

			Document doc = dBuilder.parse(kmlInputStream);

			doc.getDocumentElement().normalize();

			// =========================================================
			// 2. CREAR RUTA
			// =========================================================

			Ruta ruta = new Ruta();

			ruta.setUsuarioId(usuarioId != null ? usuarioId : 1);

			ruta.setNombre(nombreRuta != null && !nombreRuta.isBlank() ? nombreRuta.trim() : "Ruta KML");

			ruta.setDescripcion(descripcionRuta);

			ruta.setFechaCreacion(new Timestamp(System.currentTimeMillis()));

			// =========================================================
			// 3. LISTA EXCLUSIVA DE WAYPOINTS REALES
			// =========================================================

			List<PuntoRuta> puntosInteres = new ArrayList<>();

			Coordinate[] coordsLinea = null;

			// =========================================================
			// 4. BUSCAR PLACEMARKS
			// =========================================================

			NodeList placemarkList = doc.getElementsByTagNameNS("*", "Placemark");

			for (int i = 0; i < placemarkList.getLength(); i++) {

				Node node = placemarkList.item(i);

				if (node.getNodeType() != Node.ELEMENT_NODE) {

					continue;
				}

				Element placemark = (Element) node;

				// =====================================================
				// NOMBRE DEL PLACEMARK
				// =====================================================

				String nombrePlacemark = obtenerTextoElementoDirecto(placemark, "name");

				// =====================================================
				// 5. VERIFICAR SI ES UN POINT REAL
				// =====================================================

				Element pointElement = obtenerPrimerElementoDirecto(placemark, "Point");

				if (pointElement != null) {

					String coordsStr = obtenerTextoElementoDirecto(pointElement, "coordinates");

					if (coordsStr == null || coordsStr.isBlank()) {

						continue;
					}

					// Un Point debería contener una sola coordenada
					String coordenada = coordsStr.trim().split("\\s+")[0];

					String[] partes = coordenada.split(",");

					if (partes.length < 2) {
						continue;
					}

					double lon = Double.parseDouble(partes[0].trim());

					double lat = Double.parseDouble(partes[1].trim());

					double alt = 0.0;

					if (partes.length >= 3 && !partes[2].isBlank()) {

						alt = Double.parseDouble(partes[2].trim());
					}

					// ================================================
					// CREAR SOLAMENTE EL WAYPOINT REAL
					// ================================================

					PuntoRuta puntoRuta = new PuntoRuta();

					puntoRuta.setRuta(ruta);

					puntoRuta.setOrden(puntosInteres.size() + 1);

					// Mantener nombre original del KML
					if (nombrePlacemark != null && !nombrePlacemark.isBlank()) {

						puntoRuta.setNombre(nombrePlacemark.trim());

					} else {

						puntoRuta.setNombre("Punto de interés " + (puntosInteres.size() + 1));
					}

					// ================================================
					// GEOMETRÍA
					// ================================================

					Point punto = geometryFactory.createPoint(new Coordinate(lon, lat, alt));

					punto.setSRID(4326);

					puntoRuta.setPunto(punto);

					puntoRuta.setElevacion(alt);

					// ================================================
					// DESCRIPCIÓN
					// ================================================

					String descripcionPunto = obtenerTextoElementoDirecto(placemark, "description");

					if (descripcionPunto != null && !descripcionPunto.isBlank()) {

						puntoRuta.setDescripcion(descripcionPunto.trim());
					}

					// ================================================
					// TIMESTAMP DEL WAYPOINT
					// ================================================

					String fechaKml = obtenerTextoElementoDescendiente(placemark, "when");

					if (fechaKml != null && !fechaKml.isBlank()) {

						try {

							OffsetDateTime offsetDateTime = OffsetDateTime.parse(fechaKml.trim());

							puntoRuta.setTiempo(Timestamp.from(offsetDateTime.toInstant()));

						} catch (Exception ex) {

							System.err.println("No se pudo interpretar fecha " + "del punto '" + nombrePlacemark + "': "
									+ fechaKml);
						}
					}

					// ================================================
					// ESTE ES UN WAYPOINT REAL
					// ================================================

					puntosInteres.add(puntoRuta);

					// MUY IMPORTANTE:
					// Ya procesamos este Placemark como Point.
					// No se utilizará como LineString.

					continue;
				}

				// =====================================================
				// 6. VERIFICAR SI ES EL LINESTRING DE LA RUTA
				// =====================================================

				Element lineElement = obtenerPrimerElementoDirecto(placemark, "LineString");

				if (lineElement != null) {

					String coordsStr = obtenerTextoElementoDirecto(lineElement, "coordinates");

					if (coordsStr == null || coordsStr.isBlank()) {

						continue;
					}

					String[] coordenadasTexto = coordsStr.trim().split("\\s+");

					List<Coordinate> coordenadasValidas = new ArrayList<>();

					for (String coordenadaTexto : coordenadasTexto) {

						if (coordenadaTexto == null || coordenadaTexto.isBlank()) {

							continue;
						}

						String[] partes = coordenadaTexto.trim().split(",");

						if (partes.length < 2) {
							continue;
						}

						try {

							double lon = Double.parseDouble(partes[0].trim());

							double lat = Double.parseDouble(partes[1].trim());

							double alt = 0.0;

							if (partes.length >= 3 && !partes[2].isBlank()) {

								alt = Double.parseDouble(partes[2].trim());
							}

							coordenadasValidas.add(new Coordinate(lon, lat, alt));

						} catch (NumberFormatException ex) {

							System.err.println("Coordenada KML ignorada: " + coordenadaTexto);
						}
					}

					if (coordenadasValidas.size() >= 2) {

						coordsLinea = coordenadasValidas.toArray(new Coordinate[0]);
					}
				}
			}

			// =========================================================
			// 7. CREAR LINESTRING
			// =========================================================

			if (coordsLinea == null || coordsLinea.length < 2) {

				throw new RuntimeException("El KML no contiene un LineString válido.");
			}

			LineString lineString = geometryFactory.createLineString(coordsLinea);

			lineString.setSRID(4326);

			ruta.setRutaLinea(lineString);

			ruta.setDistanciaKm(calcularDistanciaKm(coordsLinea));

			// =========================================================
			// 8. GUARDAR RUTA
			// =========================================================

			Ruta rutaGuardada = rutaRepo.save(ruta);

			// =========================================================
			// 9. GUARDAR SOLAMENTE LOS WAYPOINTS REALES
			// =========================================================

			for (PuntoRuta puntoInteres : puntosInteres) {

				puntoInteres.setRuta(rutaGuardada);

				puntoRutaRepo.save(puntoInteres);
			}

			// =========================================================
			// 10. RESPUESTA
			// =========================================================

			Map<String, Object> respuesta = new HashMap<>();

			respuesta.put("mensaje", "Ruta KML registrada correctamente");

			respuesta.put("id", rutaGuardada.getId());

			respuesta.put("nombre", rutaGuardada.getNombre());

			respuesta.put("puntosInteresRegistrados", puntosInteres.size());

			respuesta.put("verticesLinea", coordsLinea.length);

			return respuesta;

		} catch (Exception e) {

			throw new RuntimeException("Error al procesar el archivo KML: " + e.getMessage(), e);
		}
	}

	// Método auxiliar seguro para leer etiquetas XML
	private String obtenerTextoElemento(Element parent, String tagName) {
		NodeList list = parent.getElementsByTagName(tagName);
		if (list.getLength() > 0) {
			return list.item(0).getTextContent().trim();
		}
		return null;
	}

	private String escaparXml(String texto) {
		if (texto == null) {
			return "";
		}

		return texto.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;").replace("\"", "&quot;")
				.replace("'", "&apos;");
	}
	
	private Element obtenerPrimerElementoDirecto(
	        Element parent,
	        String nombre) {

	    NodeList hijos =
	            parent.getChildNodes();

	    for (int i = 0;
	         i < hijos.getLength();
	         i++) {

	        Node nodo =
	                hijos.item(i);

	        if (nodo.getNodeType()
	                != Node.ELEMENT_NODE) {

	            continue;
	        }

	        String localName =
	                nodo.getLocalName();

	        String nodeName =
	                nodo.getNodeName();

	        if (nombre.equals(localName)
	                || nombre.equals(nodeName)
	                || nodeName.endsWith(
	                        ":" + nombre
	                )) {

	            return (Element) nodo;
	        }
	    }

	    return null;
	}
	private String obtenerTextoElementoDirecto(
	        Element parent,
	        String nombre) {

	    Element elemento =
	            obtenerPrimerElementoDirecto(
	                    parent,
	                    nombre
	            );

	    if (elemento == null) {
	        return null;
	    }

	    String texto =
	            elemento.getTextContent();

	    return texto != null
	            ? texto.trim()
	            : null;
	}
	
	private String obtenerTextoElementoDescendiente(
	        Element parent,
	        String nombre) {

	    NodeList elementos =
	            parent.getElementsByTagNameNS(
	                    "*",
	                    nombre
	            );

	    if (elementos.getLength() == 0) {

	        elementos =
	                parent.getElementsByTagName(
	                        nombre
	                );
	    }

	    if (elementos.getLength() == 0) {
	        return null;
	    }

	    String texto =
	            elementos.item(0)
	                    .getTextContent();

	    return texto != null
	            ? texto.trim()
	            : null;
	}
}