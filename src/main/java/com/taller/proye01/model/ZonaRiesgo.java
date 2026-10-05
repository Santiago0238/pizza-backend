package com.taller.proye01.model;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.List;

import org.locationtech.jts.geom.Polygon;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "zona_riesgo")
public class ZonaRiesgo {

	    @Id
	    @GeneratedValue(strategy = GenerationType.IDENTITY)
	    private Integer id;

	    @Column(name = "nombre", nullable = false, length = 150)
	    private String nombre;

	    @Column(name = "nivel_riesgo", nullable = false, length = 20)
	    private String nivelRiesgo;

	    @Column(name = "descripcion", length = 50)
	    private String descripcion;

	    @Column(name = "fuente_datos", length = 100)
	    private String fuenteDatos;

	    @Column(name = "fecha_actualizacion")
	    private Timestamp fechaActualizacion;

	    @JsonIgnore
	    @Column(name = "poligono", columnDefinition = "geometry(Polygon,4326)")
	    private Polygon poligono;

	    @ManyToOne(fetch = FetchType.LAZY)
	    @JoinColumn(name = "id_tipo_vegetacion")
	    private TipoVegetacion tipoVegetacion;

	    @JsonIgnore
	    @OneToMany(mappedBy = "zonaRiesgo")
	    private List<Comunidad> comunidades;

	    @JsonIgnore
	    @OneToMany(mappedBy = "zonaRiesgo")
	    private List<Incendio> incendios;
	
	
}
