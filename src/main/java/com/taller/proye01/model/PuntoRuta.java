package com.taller.proye01.model;

import java.sql.Timestamp;

import org.locationtech.jts.geom.Point;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.*;
import lombok.*;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "puntos_ruta")
public class PuntoRuta {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "orden")
    private Integer orden;

    @Column(name = "nombre", length = 255)
    private String nombre;

    @Column(name = "descripcion", length = 500)
    private String descripcion;

    @Column(name = "elevacion")
    private Double elevacion;

    @Column(name = "tiempo")
    private Timestamp tiempo;

    @JsonIgnore
    @Column(
        name = "geom",
        columnDefinition = "geometry(PointZ,4326)"
    )
    private Point punto;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ruta_id", nullable = false)
    private Ruta ruta;
}