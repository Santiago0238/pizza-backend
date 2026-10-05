package com.taller.proye01.DTO;
import java.math.BigDecimal;
import java.sql.Timestamp;
import org.locationtech.jts.geom.LineString;

public interface RutaDTO {
    Integer getId();
    String getNombre();
    String getDescripcion();
    BigDecimal getDistanciaKm();
    Timestamp getFechaCreacion();
    Integer getUsuarioId();
    String getRutaLinea();
}