package com.taller.proye01.model;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;

import org.locationtech.jts.geom.Point;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.persistence.*;
import lombok.*;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "foco_calor")
public class FocoCalor {

	@Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "fecha", nullable = false)
    private Timestamp fecha;

    @Column(name = "satelite", length = 50)
    private String satelite;

    @Column(name = "confianza")
    private String  confianza;

    @Column(name = "temperatura")
    private BigDecimal temperatura;

    @Column(name = "fuente", length = 50)
    private String fuente;
    
    @Column(name = "scan")
    private BigDecimal scan;

    @Column(name = "track")
    private BigDecimal track;

    @Column(name = "instrumento", length = 50)
    private String instrumento;

    @Column(name = "frp")
    private BigDecimal frp;

    @Column(name = "daynight", length = 5)
    private String daynight;
    

    @Column(name = "punto", columnDefinition = "geometry(Point,4326)")
    @JsonIgnoreProperties({"envelope", "boundary", "boundaryDimension", "envelopeInternal", "factory", "precisionModel"})
    private Point punto;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_incendio")
    @JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
    private Incendio incendio;
    
  
}