package com.taller.proye01.modelPizza;
import java.sql.Date;
import java.util.List;
import java.util.Set;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.AccessLevel;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "promocion")
public class PromocionModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idPromocion")
    private Integer idPromocion;

    @Column(name = "nombre", length = 100)
    private String nombre;

    @Column(name = "tipo", length = 100)
    private String tipo;

    @Column(name = "estado", length = 100)
    private String estado;

    @Column(name = "valor", length = 100)
    private String valor;

    @Column(name = "descripcion", length = 255)
    private String descripcion;

    @Column(name = "descuentoPorcentaje")
    private Double descuentoPorcentaje;

    @Column(name = "fechaInicio")
    @Temporal(TemporalType.DATE)
    private Date fechaInicio;

    @Column(name = "fechaFin")
    @Temporal(TemporalType.DATE)
    private Date fechaFin;
   
    
    @Getter(AccessLevel.NONE)
    @Setter(AccessLevel.NONE)
    @OneToMany(mappedBy = "promocion")
    Set<ProPromoModel> proPromo;
    
}