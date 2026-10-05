package com.taller.proye01.model;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;

import org.locationtech.jts.geom.MultiPolygon;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "incendio")
public class Incendio {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "nombre", nullable = false, length = 100)
    private String nombre;

    @Column(name = "fecha_inicio")
    private Timestamp fechaInicio;

    @Column(name = "fecha_fin")
    private Timestamp fechaFin;

    @Column(name = "estado", length = 50)
    private String estado;

    @Column(name = "causa", length = 100)
    private String causa;

    @Column(name = "origen", length = 100)
    private String origen;

    @Column(name = "nivel_gravedad", length = 30)
    private String nivelGravedad;

    @Column(name = "superficie_afectada_ha")
    private BigDecimal superficieAfectadaHa;

    @Column(name = "cantidad_focos")
    private Integer cantidadFocos;

    @Column(name = "cantidad_afectados")
    private Integer cantidadAfectados;

    @Column(name = "cantidad_fallecidos")
    private Integer cantidadFallecidos;

    @Column(name = "costo_estimado")
    private BigDecimal costoEstimado;

    @Column(name = "metodo_control", length = 100)
    private String metodoControl;

    @Column(name = "tiempo_control_horas")
    private BigDecimal tiempoControlHoras;

    @Column(name = "observaciones", length = 50)
    private String observaciones;

    @Column(name = "activo")
    private Boolean activo;

    @Column(name = "historico")
    private Boolean historico;

    @Column(name = "fecha_registro")
    private Timestamp fechaRegistro;

    @Column(name = "poligono", columnDefinition = "geometry(MultiPolygon,4326)")
    private MultiPolygon poligono;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_zona_riesgo")
    private ZonaRiesgo zonaRiesgo;

    @JsonIgnore
    @OneToMany(mappedBy = "incendio")
    private List<FocoCalor> focosCalor;

    @JsonIgnore
    @OneToMany(mappedBy = "incendio")
    private List<Alerta> alertas;

  

    @JsonIgnore
    @OneToMany(mappedBy = "incendio")
    private List<IncendioComunidad> incendioComunidades;

   
}