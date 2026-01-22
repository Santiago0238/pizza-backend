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
@Table(name = "venta")
public class VentaModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idVenta")
    private Integer idVenta;

    @Column(name = "idPedido")
    private Integer idPedido;

    @Column(name = "idCliente")
    private Integer idCliente;

    @Column(name = "idUsuario")
    private Integer idUsuario;

    @Column(name = "fecha")
    @Temporal(TemporalType.DATE)
    private Date fecha;

    @Column(name = "total")
    private double total;

    @Column(name = "estado", length = 50)
    private String estado;

    @Column(name = "observaciones", length = 50)
    private String observaciones;

    @Column(name = "detalle", length = 50)
    private String detalle;

    @Column(name = "tipodepago", length = 50)
    private String tipodepago;
    public String getTipoDePago() { return tipodepago; }
    public void setTipoDePago(String tipoDePago) { this.tipodepago = tipoDePago; }
    
    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "idCliente", insertable = false, updatable = false)
    ClienteModel cliente;
    
    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "idPedido", insertable = false, updatable = false)
    PedidoModel pedido;
    
    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "idUsuario", insertable = false, updatable = false)
    UsuarioModel usuario;
}