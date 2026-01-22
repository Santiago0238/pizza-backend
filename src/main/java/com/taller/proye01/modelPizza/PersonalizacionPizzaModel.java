package com.taller.proye01.modelPizza;



import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "PersonalizacionPizza")
public class PersonalizacionPizzaModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idPersonalizacion")
    private Integer idPersonalizacion;

    @Column(name = "idProducto")
    private Integer idProducto;

    @Column(name = "tamano", length = 100)
    private String tamano;

    @Column(name = "masa", length = 100)
    private String masa;

    @Column(name = "tipo", length = 100)
    private String tipo;

    @Column(name = "costoAdicional")
    private double costoAdicional;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "idProducto", insertable = false, updatable = false)
    private ProductoModel producto;
}
