package com.taller.proye01.controller;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Locale;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.CacheControl;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

@RestController
@RequestMapping("/api/mosaicos")
public class MosaicoTileController {

	private final SentinelService sentinelService;
	private final RestTemplate restTemplate;

	// NUEVO ENDPOINT OFICIAL DE CDSE (Copernicus Data Space Ecosystem)
	@Value("${sig.mosaicos.sentinelhub.instance-id:TU_NUEVO_INSTANCE_ID_DE_CDSE}")
	private String sentinelHubInstanceId;

	@Value("${sig.mosaicos.sentinelhub.base-url:https://sh.dataspace.copernicus.eu/ogc/wms}")
	private String sentinelHubBaseUrl;

	private static final int TAMANO_TESELA = 256;
	private static final int DIAS_BUSQUEDA_SENTINEL = 7;

	public MosaicoTileController(SentinelService sentinelService, RestTemplate restTemplate) {
		this.sentinelService = sentinelService;
		this.restTemplate = restTemplate;
	}

	@GetMapping("/disponibilidad")
	public Map<String, Boolean> disponibilidad() {
		return Map.of(
			"eox", true, 
			"sentinelHub", sentinelService.verificarSentinelHub(sentinelHubInstanceId, sentinelHubBaseUrl)
		);
	}

	@GetMapping(value = "/eox/{capa}/{z}/{x}/{y}.jpg", produces = MediaType.IMAGE_JPEG_VALUE)
	@Cacheable(cacheNames = "eoxTiles", key = "#capa + ':' + #z + ':' + #x + ':' + #y + ':' + (#fecha != null ? #fecha.toString() : 'actual')")
	public ResponseEntity<byte[]> teselaEox(
			@PathVariable String capa, @PathVariable int z, @PathVariable int x, @PathVariable int y,
			@RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha) {

		String capaNormalizada = capa.endsWith("_3857") ? capa : capa + "_3857";
		String url = String.format("https://tiles.maps.eox.at/wmts/1.0.0/%s/default/GoogleMapsCompatible/%d/%d/%d.jpg",
				capaNormalizada, z, y, x);

		return proxyImagen(url, MediaType.IMAGE_JPEG, fecha);
	}

	@GetMapping(value = "/sentinelhub/{capa}/{z}/{x}/{y}.png", produces = MediaType.IMAGE_PNG_VALUE)
	@Cacheable(cacheNames = "sentinelHubTiles", key = "#capa + ':' + #z + ':' + #x + ':' + #y + ':' + (#fecha != null ? #fecha.toString() : 'actual')")
	public ResponseEntity<byte[]> teselaSentinelHub(
			@PathVariable String capa, @PathVariable int z, @PathVariable int x, @PathVariable int y,
			@RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate fecha) {

		if (sentinelHubInstanceId == null || sentinelHubInstanceId.isBlank()) {
			return ResponseEntity.status(503).build();
		}

		LocalDate fechaConsulta = fecha != null ? fecha : LocalDate.now();
		if (fechaConsulta.isAfter(LocalDate.now())) {
			return ResponseEntity.badRequest().build();
		}

		LocalDate fechaInicio = fechaConsulta.minusDays(DIAS_BUSQUEDA_SENTINEL);
		String timeInicio = fechaInicio.format(DateTimeFormatter.ISO_DATE) + "T00:00:00Z";
		String timeFin = fechaConsulta.format(DateTimeFormatter.ISO_DATE) + "T23:59:59Z";
		String intervaloTiempo = timeInicio + "/" + timeFin;

		double[] bbox = TileBBoxUtil.tileABbox3857(z, x, y);

		String url = UriComponentsBuilder.fromHttpUrl(sentinelHubBaseUrl + "/" + sentinelHubInstanceId)
				.queryParam("service", "WMS")
				.queryParam("version", "1.3.0")
				.queryParam("request", "GetMap")
				.queryParam("layers", capa)
				.queryParam("styles", "")
				.queryParam("bbox", String.format(Locale.US, "%f,%f,%f,%f", bbox[0], bbox[1], bbox[2], bbox[3]))
				.queryParam("width", TAMANO_TESELA)
				.queryParam("height", TAMANO_TESELA)
				.queryParam("crs", "EPSG:3857")
				.queryParam("format", "image/png")
				.queryParam("transparent", "true")
				.queryParam("time", intervaloTiempo)
				.build().encode().toUriString();

		return proxyImagen(url, MediaType.IMAGE_PNG, fechaConsulta);
	}

	private ResponseEntity<byte[]> proxyImagen(String url, MediaType tipo, LocalDate fecha) {
		try {
			HttpHeaders headers = new HttpHeaders();
			headers.set("User-Agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) SpringBootGIS/1.0");
			HttpEntity<Void> entity = new HttpEntity<>(headers);

			ResponseEntity<byte[]> response = restTemplate.exchange(url, HttpMethod.GET, entity, byte[].class);
			byte[] imagen = response.getBody();

			if (imagen == null || imagen.length == 0) {
				return ResponseEntity.notFound().build();
			}

			long tiempoCache = (fecha != null && fecha.isBefore(LocalDate.now())) 
					? TimeUnit.DAYS.toSeconds(30) 
					: TimeUnit.HOURS.toSeconds(1);

			return ResponseEntity.ok()
					.cacheControl(CacheControl.maxAge(tiempoCache, TimeUnit.SECONDS).cachePublic())
					.contentType(tipo)
					.body(imagen);

		} catch (RestClientException ex) {
			System.err.println("Error obteniendo mosaico de CDSE: " + ex.getMessage());
			return ResponseEntity.status(502).build();
		}
	}
}

@Service
class SentinelService {
	private final RestTemplate restTemplate;

	public SentinelService(RestTemplate restTemplate) {
		this.restTemplate = restTemplate;
	}

	@Cacheable(cacheNames = "sentinelHubDisponible")
	public boolean verificarSentinelHub(String instanceId, String baseUrl) {
		if (instanceId == null || instanceId.isBlank()) {
			return false;
		}
		try {
			String url = baseUrl + "/" + instanceId + "?service=WMS&request=GetCapabilities";
			HttpHeaders headers = new HttpHeaders();
			headers.set("User-Agent", "SpringBootGIS/1.0");
			HttpEntity<Void> entity = new HttpEntity<>(headers);
			restTemplate.exchange(url, HttpMethod.GET, entity, String.class);
			return true;
		} catch (RestClientException ex) {
			return false;
		}
	}
}
class TileBBoxUtil {
	private static final double ORIGIN_SHIFT = 2 * Math.PI * 6378137 / 2.0;
	private static final int TAMANO_TESELA = 256;

	static double[] tileABbox3857(int z, int x, int y) {
		int teselasPorLado = 1 << z;
		double resolucion = (ORIGIN_SHIFT * 2) / (TAMANO_TESELA * teselasPorLado);
		double minX = x * TAMANO_TESELA * resolucion - ORIGIN_SHIFT;
		double maxX = (x + 1) * TAMANO_TESELA * resolucion - ORIGIN_SHIFT;
		double maxY = ORIGIN_SHIFT - y * TAMANO_TESELA * resolucion;
		double minY = ORIGIN_SHIFT - (y + 1) * TAMANO_TESELA * resolucion;
		return new double[] { minX, minY, maxX, maxY };
	}
}