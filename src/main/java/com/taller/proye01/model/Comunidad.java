package com.taller.proye01.model;

import java.util.List;

import org.locationtech.jts.geom.Point;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "comunidad")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class Comunidad {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "nombre", nullable = false, length = 100)
    private String nombre;

    @Column(name = "poblacion")
    private Integer poblacion;

    @Column(name = "contacto_lider", length = 100)
    private String contactoLider;

    @Column(name = "telefono_lider", length = 20)
    private String telefonoLider;

    @Column(name = "notas", length = 50)
    private String notas;

    @Column(name = "ubicacion", columnDefinition = "geometry(Point,4326)")
    private Point ubicacion;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_zona_riesgo")
    private ZonaRiesgo zonaRiesgo;

    @JsonIgnore
    @OneToMany(mappedBy = "comunidad")
    private List<IncendioComunidad> incendioComunidades;
}