package com.taller.proye01.modelPizza;


import java.sql.Date;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "reclamo")
public class ReclamoModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idReclamo")
    private Integer idReclamo;

    @Column(name = "idPedido")
    private Integer idPedido;

    @Column(name = "idUsuario")
    private Integer idUsuario;

    @Column(name = "descripcion", length = 255)
    private String descripcion;

    @Column(name = "fecha")
    @Temporal(TemporalType.DATE)
    private Date fecha;

    @Column(name = "estado")
    private Integer estado;
    
    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "pedido", insertable = false, updatable = false)
    private PedidoModel pedido;
  
    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "usuario", insertable = false, updatable = false)
    private UsuarioModel usuario;
}