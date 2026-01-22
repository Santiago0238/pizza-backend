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
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "Pedido")
public class PedidoModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idPedido")
    Integer idPedido;

    @Column(name = "idCliente")
     Integer idCliente;

    @Column(name = "idDireccion")
     Integer idDireccion;

    @Column(name = "idUsuario")
     Integer idUsuario;
    

    @Column(name = "estadoPedido", length = 255)
    String estadoPedido;

    @Column(name = "tipo", length = 255)
    String tipo;

    @Column(name = "fecha")
    Date fecha;

    @Column(name = "tiempoEstimadoEntrega")
    Integer tiempoEstimadoEntrega;

    @Column(name = "total")
    double total;

    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "idCliente", insertable = false, updatable = false)
    ClienteModel cliente;
    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "idDireccion", insertable = false, updatable = false)
    DireccionEntregaModel direccionEntrega;
    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "idUsuario", insertable = false, updatable = false)
    UsuarioModel usuario;

   
    @Getter(AccessLevel.NONE)
    @Setter(AccessLevel.NONE)
    @OneToMany(mappedBy = "pedido")
    Set<PedidoDetalleModel> pedidoDetalles;

}

