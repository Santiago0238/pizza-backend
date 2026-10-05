package com.taller.proye01.model;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.List;
import org.locationtech.jts.geom.LineString;
import com.fasterxml.jackson.annotation.JsonIgnore; // <-- IMPORTANTE
import jakarta.persistence.*;
import lombok.*;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "ruta")
public class Ruta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "usuario_id", nullable = false)
    private Integer usuarioId;

    @Column(name = "nombre", nullable = false, length = 255)
    private String nombre;

    @Column(name = "descripcion", length = 500)
    private String descripcion;

    @JsonIgnore
    @Column(name = "ruta_linea", columnDefinition = "geometry(LineStringZ,4326)")
    private LineString rutaLinea;

    @Column(name = "distancia_km")
    private BigDecimal distanciaKm;

    @Column(name = "fecha_creacion")
    private Timestamp fechaCreacion;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", insertable = false, updatable = false)
    private Usuario usuario;

    @JsonIgnore 
    @OneToMany(mappedBy = "ruta", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PuntoRuta> puntos;
}