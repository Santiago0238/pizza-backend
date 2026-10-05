package com.taller.proye01.model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "tipo_vegetacion")
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class TipoVegetacion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(name = "nombre", nullable = false, unique = true, length = 100)
    private String nombre;

    @Column(name = "inflamabilidad", length = 20)
    private String inflamabilidad;

    @Column(name = "combustible", length = 50)
    private String combustible;

    @Column(name = "descripcion", length = 50)
    private String descripcion;

    @JsonIgnore
    @OneToMany(mappedBy = "tipoVegetacion")
    private List<CapaCaracteristica> capaCaracteristicas;

    @JsonIgnore
    @OneToMany(mappedBy = "tipoVegetacion")
    private List<ZonaRiesgo> zonasRiesgo;
}