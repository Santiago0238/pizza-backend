package com.taller.proye01.modelPizza;


import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;

import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "proPromo")
public class ProPromoModel {

    @EmbeddedId
    private ProPromoPK id;

    @ManyToOne
    @MapsId("idProducto")
    @JoinColumn(name = "idProducto")
    private ProductoModel producto;

    @ManyToOne
    @MapsId("idPromocion")
    @JoinColumn(name = "idPromocion")
    private PromocionModel promocion;
}