package com.taller.proye01.model;

import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import org.locationtech.jts.geom.Geometry;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.persistence.*;
import lombok.*;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "capa_caracteristica")
public class CapaCaracteristica {

		@Id
	    @GeneratedValue(strategy = GenerationType.IDENTITY)
	    private Integer id;

	    @ManyToOne(fetch = FetchType.LAZY)
	    @JoinColumn(name = "id_capa", nullable = false)
	    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
	    private Capa capa;

	    @ManyToOne(fetch = FetchType.LAZY)
	    @JoinColumn(name = "id_tipo_vegetacion")
	    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
	    private TipoVegetacion tipoVegetacion;

	    @JdbcTypeCode(SqlTypes.JSON)
	    @Column(name = "propiedades", columnDefinition = "jsonb")
	    private String propiedades;

	    @Column(name = "geometria", columnDefinition = "geometry(Geometry,4326)")
	    private Geometry geometria;
}