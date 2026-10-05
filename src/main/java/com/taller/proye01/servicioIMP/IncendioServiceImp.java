package com.taller.proye01.servicioIMP;


import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.taller.proye01.model.FocoCalor;
import com.taller.proye01.model.Incendio;
import com.taller.proye01.model.ZonaRiesgo;
import com.taller.proye01.repository.FocoCalorRepo;
import com.taller.proye01.repository.IncendioRepo;
import com.taller.proye01.repository.ZonaDeRiesgoRepo;
import com.taller.proye01.servicio.IncendioService;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class IncendioServiceImp implements IncendioService {

    private final IncendioRepo incendioRepository;
    private final ZonaDeRiesgoRepo zonaRiesgoRepository;
    private final FocoCalorRepo focoCalorRepository;

    @Override
    @Transactional(readOnly = true)
    public List<Map<String, Object>> listarIncendios() {
        return incendioRepository.listarConZonaRiesgo()
                .stream()
                .map(this::convertirARespuesta)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public Map<String, Object> obtenerPorId(Integer id) {
        Incendio incendio = incendioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Incendio no encontrado"));

        return convertirARespuesta(incendio);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Map<String, Object>> listarIncendiosActivos() {
        return incendioRepository.listarActivos()
                .stream()
                .map(this::convertirARespuesta)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Map<String, Object>> listarHistorialIncendios() {
        return incendioRepository.listarHistorial()
                .stream()
                .map(this::convertirARespuesta)
                .toList();
    }

    @Override
    @Transactional
    public Map<String, Object> registrarIncendio(Map<String, Object> payload) {
        Incendio incendio = new Incendio();

        cargarDatosGenerales(incendio, payload);

        if (incendio.getEstado() == null || incendio.getEstado().isBlank()) {
            incendio.setEstado("ACTIVO");
        }

        incendio.setActivo(true);
        incendio.setHistorico(false);

        Incendio guardado = incendioRepository.save(incendio);

        return convertirARespuesta(guardado);
    }

    @Override
    @Transactional
    public Map<String, Object> actualizarIncendio(Integer id, Map<String, Object> payload) {
        Incendio incendio = incendioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Incendio no encontrado"));

        cargarDatosGenerales(incendio, payload);

        Incendio actualizado = incendioRepository.save(incendio);

        return convertirARespuesta(actualizado);
    }

    @Override
    @Transactional
    public Map<String, Object> actualizarEstado(Integer id, Map<String, Object> payload) {
        Incendio incendio = incendioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Incendio no encontrado"));

        String estado = obtenerString(payload, "estado");

        if (estado == null || estado.isBlank()) {
            throw new RuntimeException("El estado del incendio es obligatorio");
        }

        incendio.setEstado(estado.trim().toUpperCase());

        if (estado.equalsIgnoreCase("CONTROLADO") || estado.equalsIgnoreCase("EXTINGUIDO")) {
            incendio.setActivo(false);
            incendio.setHistorico(true);

            if (incendio.getFechaFin() == null) {
                incendio.setFechaFin(new Timestamp(System.currentTimeMillis()));
            }
        }

        if (estado.equalsIgnoreCase("ACTIVO") || estado.equalsIgnoreCase("EN_CURSO")) {
            incendio.setActivo(true);
            incendio.setHistorico(false);
            incendio.setFechaFin(null);
        }

        Incendio actualizado = incendioRepository.save(incendio);

        return convertirARespuesta(actualizado);
    }

    @Override
    @Transactional
    public Map<String, Object> registrarMetodoControl(Integer id, Map<String, Object> payload) {
        Incendio incendio = incendioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Incendio no encontrado"));

        String metodoControl = obtenerString(payload, "metodoControl");
        BigDecimal tiempoControlHoras = obtenerBigDecimal(payload, "tiempoControlHoras");

        if (metodoControl == null || metodoControl.isBlank()) {
            throw new RuntimeException("El método de control es obligatorio");
        }

        incendio.setMetodoControl(metodoControl.trim());
        incendio.setTiempoControlHoras(tiempoControlHoras);

        Incendio actualizado = incendioRepository.save(incendio);

        return convertirARespuesta(actualizado);
    }

    @Override
    @Transactional
    public Map<String, Object> registrarSuperficieAfectada(Integer id, Map<String, Object> payload) {
        Incendio incendio = incendioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Incendio no encontrado"));

        BigDecimal superficieAfectadaHa = obtenerBigDecimal(payload, "superficieAfectadaHa");

        if (superficieAfectadaHa == null) {
            superficieAfectadaHa = obtenerBigDecimal(payload, "superficie_afectada_ha");
        }

        if (superficieAfectadaHa == null || superficieAfectadaHa.compareTo(BigDecimal.ZERO) < 0) {
            throw new RuntimeException("La superficie afectada debe ser válida");
        }

        incendio.setSuperficieAfectadaHa(superficieAfectadaHa);

        Incendio actualizado = incendioRepository.save(incendio);

        return convertirARespuesta(actualizado);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Map<String, Object>> buscarPorEstado(String estado) {
        if (estado == null || estado.isBlank()) {
            throw new RuntimeException("El estado es obligatorio");
        }

        return incendioRepository.buscarPorEstado(estado.trim())
                .stream()
                .map(this::convertirARespuesta)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Map<String, Object>> buscarPorNivelGravedad(String nivelGravedad) {
        if (nivelGravedad == null || nivelGravedad.isBlank()) {
            throw new RuntimeException("El nivel de gravedad es obligatorio");
        }

        return incendioRepository.buscarPorNivelGravedad(nivelGravedad.trim())
                .stream()
                .map(this::convertirARespuesta)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<Map<String, Object>> buscarPorTexto(String texto) {
        if (texto == null || texto.isBlank()) {
            return listarIncendios();
        }

        return incendioRepository.buscarPorTexto(texto.trim())
                .stream()
                .map(this::convertirARespuesta)
                .toList();
    }

    @Override
    @Transactional
    public Map<String, Object> eliminarIncendio(Integer id) {
        Incendio incendio = incendioRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Incendio no encontrado"));

        incendioRepository.delete(incendio);

        Map<String, Object> respuesta = new LinkedHashMap<>();
        respuesta.put("mensaje", "Incendio eliminado correctamente");
        respuesta.put("id", id);

        return respuesta;
    }

    private void cargarDatosGenerales(Incendio incendio, Map<String, Object> payload) {
        String nombre = obtenerString(payload, "nombre");

        if (nombre == null || nombre.isBlank()) {
            throw new RuntimeException("El nombre del incendio es obligatorio");
        }

        incendio.setNombre(nombre.trim());

        Integer idZonaRiesgo = obtenerIntegerFlexible(
                payload,
                "idZonaRiesgo",
                "id_zona_riesgo",
                "idZona"
        );

        if (idZonaRiesgo != null) {
            ZonaRiesgo zonaRiesgo = zonaRiesgoRepository.findById(idZonaRiesgo)
                    .orElseThrow(() -> new RuntimeException("Zona de riesgo no encontrada"));

            incendio.setZonaRiesgo(zonaRiesgo);
        } else {
            incendio.setZonaRiesgo(null);
        }

        Timestamp fechaInicio = obtenerTimestampFlexible(payload, "fechaInicio", "fecha_inicio");

        if (fechaInicio != null) {
            incendio.setFechaInicio(fechaInicio);
        }

        Timestamp fechaFin = obtenerTimestampFlexible(payload, "fechaFin", "fecha_fin");

        if (fechaFin != null) {
            incendio.setFechaFin(fechaFin);
        }

        String estado = obtenerString(payload, "estado");

        if (estado != null) {
            incendio.setEstado(estado.trim().toUpperCase());
        }

        incendio.setCausa(obtenerString(payload, "causa"));
        incendio.setOrigen(obtenerString(payload, "origen"));
        incendio.setNivelGravedad(obtenerStringFlexible(payload, "nivelGravedad", "nivel_gravedad"));

        incendio.setSuperficieAfectadaHa(
                obtenerBigDecimalFlexible(payload, "superficieAfectadaHa", "superficie_afectada_ha")
        );

        incendio.setCantidadFocos(
                obtenerIntegerFlexible(payload, "cantidadFocos", "cantidad_focos")
        );

        incendio.setCantidadAfectados(
                obtenerIntegerFlexible(payload, "cantidadAfectados", "cantidad_afectados")
        );

        incendio.setCantidadFallecidos(
                obtenerIntegerFlexible(payload, "cantidadFallecidos", "cantidad_fallecidos")
        );

        incendio.setCostoEstimado(
                obtenerBigDecimalFlexible(payload, "costoEstimado", "costo_estimado")
        );

        incendio.setMetodoControl(
                obtenerStringFlexible(payload, "metodoControl", "metodo_control")
        );

        incendio.setTiempoControlHoras(
                obtenerBigDecimalFlexible(payload, "tiempoControlHoras", "tiempo_control_horas")
        );

        incendio.setObservaciones(obtenerString(payload, "observaciones"));

        Boolean activo = obtenerBoolean(payload, "activo");
        Boolean historico = obtenerBoolean(payload, "historico");

        if (activo != null) {
            incendio.setActivo(activo);
        }

        if (historico != null) {
            incendio.setHistorico(historico);
        }
    }

    private Map<String, Object> convertirARespuesta(Incendio incendio) {
        Map<String, Object> respuesta = new LinkedHashMap<>();

        respuesta.put("id", incendio.getId());
        respuesta.put("nombre", incendio.getNombre());

        if (incendio.getZonaRiesgo() != null) {
            ZonaRiesgo zona = incendio.getZonaRiesgo();

            respuesta.put("idZonaRiesgo", zona.getId());

            Map<String, Object> zonaMap = new LinkedHashMap<>();
            zonaMap.put("id", zona.getId());
            zonaMap.put("nombre", zona.getNombre());
            zonaMap.put("nivelRiesgo", zona.getNivelRiesgo());
            zonaMap.put("descripcion", zona.getDescripcion());

            respuesta.put("zonaRiesgo", zonaMap);
        } else {
            respuesta.put("idZonaRiesgo", null);
            respuesta.put("zonaRiesgo", null);
        }

        respuesta.put("fechaInicio", incendio.getFechaInicio());
        respuesta.put("fechaFin", incendio.getFechaFin());
        respuesta.put("estado", incendio.getEstado());
        respuesta.put("causa", incendio.getCausa());
        respuesta.put("origen", incendio.getOrigen());
        respuesta.put("nivelGravedad", incendio.getNivelGravedad());
        respuesta.put("superficieAfectadaHa", incendio.getSuperficieAfectadaHa());
        respuesta.put("cantidadFocos", incendio.getCantidadFocos());
        respuesta.put("cantidadAfectados", incendio.getCantidadAfectados());
        respuesta.put("cantidadFallecidos", incendio.getCantidadFallecidos());
        respuesta.put("costoEstimado", incendio.getCostoEstimado());
        respuesta.put("metodoControl", incendio.getMetodoControl());
        respuesta.put("tiempoControlHoras", incendio.getTiempoControlHoras());
        respuesta.put("observaciones", incendio.getObservaciones());
        respuesta.put("activo", incendio.getActivo());
        respuesta.put("historico", incendio.getHistorico());
        respuesta.put("fechaRegistro", incendio.getFechaRegistro());

        return respuesta;
    }

    private String obtenerString(Map<String, Object> payload, String key) {
        Object valor = payload.get(key);

        if (valor == null) {
            return null;
        }

        String texto = String.valueOf(valor).trim();

        return texto.isEmpty() || texto.equalsIgnoreCase("null") ? null : texto;
    }

    private String obtenerStringFlexible(Map<String, Object> payload, String... keys) {
        for (String key : keys) {
            String valor = obtenerString(payload, key);

            if (valor != null) {
                return valor;
            }
        }

        return null;
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

    private BigDecimal obtenerBigDecimal(Map<String, Object> payload, String key) {
        return convertirBigDecimal(payload.get(key));
    }

    private BigDecimal obtenerBigDecimalFlexible(Map<String, Object> payload, String... keys) {
        for (String key : keys) {
            BigDecimal valor = convertirBigDecimal(payload.get(key));

            if (valor != null) {
                return valor;
            }
        }

        return null;
    }

    private Timestamp obtenerTimestampFlexible(Map<String, Object> payload, String... keys) {
        for (String key : keys) {
            Timestamp valor = convertirTimestamp(payload.get(key));

            if (valor != null) {
                return valor;
            }
        }

        return null;
    }

    private Boolean obtenerBoolean(Map<String, Object> payload, String key) {
        Object valor = payload.get(key);

        if (valor == null) {
            return null;
        }

        if (valor instanceof Boolean bool) {
            return bool;
        }

        String texto = String.valueOf(valor).trim();

        if (texto.isEmpty() || texto.equalsIgnoreCase("null")) {
            return null;
        }

        return Boolean.parseBoolean(texto);
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
            throw new RuntimeException("Valor entero inválido: " + texto);
        }
    }

    private BigDecimal convertirBigDecimal(Object valor) {
        if (valor == null) {
            return null;
        }

        if (valor instanceof BigDecimal bigDecimal) {
            return bigDecimal;
        }

        if (valor instanceof Number number) {
            return BigDecimal.valueOf(number.doubleValue());
        }

        String texto = String.valueOf(valor).trim();

        if (texto.isEmpty() || texto.equalsIgnoreCase("null")) {
            return null;
        }

        try {
            return new BigDecimal(texto);
        } catch (NumberFormatException e) {
            throw new RuntimeException("Valor decimal inválido: " + texto);
        }
    }

    private Timestamp convertirTimestamp(Object valor) {
        if (valor == null) {
            return null;
        }

        if (valor instanceof Timestamp timestamp) {
            return timestamp;
        }

        String texto = String.valueOf(valor).trim();

        if (texto.isEmpty() || texto.equalsIgnoreCase("null")) {
            return null;
        }

        try {
            if (texto.length() == 10) {
                return Timestamp.valueOf(texto + " 00:00:00");
            }

            if (texto.contains("T")) {
                texto = texto.replace("T", " ");
            }

            if (texto.endsWith("Z")) {
                texto = texto.substring(0, texto.length() - 1);
            }

            if (texto.length() == 16) {
                texto = texto + ":00";
            }

            return Timestamp.valueOf(texto);
        } catch (Exception e) {
            throw new RuntimeException("Fecha inválida. Use formato yyyy-MM-dd HH:mm:ss");
        }
    }

    @Override
    @Transactional
    public Map<String, Object> registrarIncendioDesdeFoco(Integer idFoco, Map<String, Object> payload) {

        FocoCalor foco = focoCalorRepository.findById(idFoco)
                .orElseThrow(() -> new RuntimeException("Foco de calor no encontrado"));

        if (foco.getIncendio() != null) {
            throw new RuntimeException("Este foco ya está asociado a un incendio");
        }

        String nombre = obtenerString(payload, "nombre");

        if (nombre == null || nombre.isBlank()) {
            nombre = "Incendio NASA FIRMS - " + foco.getFecha();
        }

        Incendio incendio = new Incendio();

        incendio.setNombre(nombre);
        incendio.setFechaInicio(foco.getFecha());
        incendio.setEstado("ACTIVO");
        incendio.setCausa(obtenerString(payload, "causa") != null ? obtenerString(payload, "causa") : "Por determinar");
        incendio.setOrigen("Foco de calor NASA FIRMS");
        incendio.setNivelGravedad(obtenerString(payload, "nivelGravedad") != null ? obtenerString(payload, "nivelGravedad") : "ALTO");
        incendio.setCantidadFocos(1);
        incendio.setCantidadAfectados(0);
        incendio.setCantidadFallecidos(0);
        incendio.setActivo(true);
        incendio.setHistorico(false);
        incendio.setFechaRegistro(new Timestamp(System.currentTimeMillis()));
        incendio.setObservaciones("Incendio registrado a partir de foco de calor NASA FIRMS");

        Integer idZonaRiesgo = obtenerIntegerFlexible(
                payload,
                "idZonaRiesgo",
                "id_zona_riesgo",
                "idZona"
        );

        if (idZonaRiesgo != null) {
            ZonaRiesgo zonaRiesgo = zonaRiesgoRepository.findById(idZonaRiesgo)
                    .orElseThrow(() -> new RuntimeException("Zona de riesgo no encontrada"));

            incendio.setZonaRiesgo(zonaRiesgo);
        }

        BigDecimal superficieAfectadaHa = obtenerBigDecimalFlexible(
                payload,
                "superficieAfectadaHa",
                "superficie_afectada_ha"
        );

        if (superficieAfectadaHa != null) {
            incendio.setSuperficieAfectadaHa(superficieAfectadaHa);
        } else {
            incendio.setSuperficieAfectadaHa(BigDecimal.ZERO);
        }

        Incendio incendioGuardado = incendioRepository.save(incendio);

        foco.setIncendio(incendioGuardado);
        focoCalorRepository.save(foco);

        return convertirARespuesta(incendioGuardado);
    }
}