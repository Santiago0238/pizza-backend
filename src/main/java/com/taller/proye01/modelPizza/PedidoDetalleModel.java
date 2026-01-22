package com.taller.proye01.modelPizza;

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
@Table(name = "PedidoDetalle")
public class PedidoDetalleModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idCarrito")
    Integer idCarrito;

  
    @Column(name = "idPedido")
   Integer idPedido;

    @Column(name = "idProducto")
    Integer idProducto;

    @Column(name = "cantidad")
    Integer cantidad;

    @Column(name = "precioUnitario")
    double precioUnitario;

    
    @ManyToOne
    @JoinColumn(name = "idPedido", insertable = false, updatable = false)
    PedidoModel pedido;

   
    @ManyToOne
    @JoinColumn(name = "idProducto", insertable = false, updatable = false)
    ProductoModel producto;
}
