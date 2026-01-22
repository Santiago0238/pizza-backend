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
@Table(name = "HistorialCompras")
public class HistorialComprasModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idHistorial")
    Integer idHistorial;

    @Column(name = "idCliente")
    Integer idCliente;

    @Column(name = "idPedido")
    Integer idPedido;

    @Column(name = "fechaCompra")
    Date fechaCompra;

    @Column(name = "montoTotal")
    double montoTotal;

    
    @ManyToOne
    @JoinColumn(name = "idCliente", insertable = false, updatable = false)
    ClienteModel cliente;

  
    @ManyToOne
    @JoinColumn(name = "idPedido", insertable = false, updatable = false)
    PedidoModel pedido;
}

