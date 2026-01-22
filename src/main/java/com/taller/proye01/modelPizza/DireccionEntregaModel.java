package com.taller.proye01.modelPizza;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "DireccionEntrega")
public class DireccionEntregaModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idDireccion")
    Integer idDireccion;

    @Column(name = "idCliente")
    Integer idCliente;

    @Column(name = "calle", length = 100)
    String calle;

    @Column(name = "numero", length = 10)
    String numero;

    @Column(name = "ciudad", length = 50)
    String ciudad;

    @Column(name = "zona", length = 50)
    String zona;

    @Column(name = "ubicacionGps", length = 255)
    String ubicacionGps;

    @Column(name = "referencia", length = 255)
    String referencia;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "idCliente", insertable = false, updatable = false)
    ClienteModel cliente;

    
}

