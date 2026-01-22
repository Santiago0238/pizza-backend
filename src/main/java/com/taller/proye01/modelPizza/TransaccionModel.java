package com.taller.proye01.modelPizza;


import java.sql.Date;
import java.time.LocalDateTime;

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
@Table(name = "transaccion")
public class TransaccionModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idTransaccion")
    private Integer idTransaccion;

    @Column(name = "idPedido")
    private Integer idPedido;

    @Column(name = "tipoPago", length = 50)
    private String tipoPago;

    @Column(name = "monto")
    private double monto;

    @Column(name = "fecha")
    @Temporal(TemporalType.DATE)
    private Date fecha;

    @Column(name = "idUsuario")
    private Integer idUsuario;
    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "idPedido", insertable = false, updatable = false)
    private PedidoModel pedido;
    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "idUsuario", insertable = false, updatable = false)
    private UsuarioModel usuario;
}