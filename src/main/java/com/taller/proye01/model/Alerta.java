
package com.taller.proye01.model;

import java.sql.Timestamp;

import org.locationtech.jts.geom.Point;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "alerta")
public class Alerta {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;


	@Column(name = "nivel", length = 20)
	private String nivel;

	@Column(name = "estado", length = 20)
	private String estado;

	@Column(name = "descripcion", length = 255)
	private String descripcion;

	@Column(name = "fecha_emision")
	private Timestamp fechaEmision;
	
	
	@Column(name = "punto_ubicacion", columnDefinition = "geometry(Point,4326)")
    private Point 	punto_ubicacion;
	
	@Column(name = "comunidad", length = 255)
	private String comunidad;
	
	@Column(name = "telefono", length = 255)
	private String telefono;

	@Column(name = "fecha_cierre")
	private Timestamp fechaCierre;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "id_usuario")
	@JsonIgnoreProperties({ "hibernateLazyInitializer", "handler", "contra", "alertas" })
	private Usuario usuario;



	@ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_incendio")
    @JsonIgnoreProperties({
        "hibernateLazyInitializer",
        "handler",
        "focosCalor",
        "alertas",
        "zonaRiesgo"
    })
    private Incendio incendio;
}
