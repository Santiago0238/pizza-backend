package com.taller.proye01.modelPizza;

import java.util.Date;

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
@Table(name = "Devolucion")
public class Devolucion {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idDevolucion")
    Integer idDevolucion;

    @Column(name = "idVenta")
    Integer idVenta;

    @Column(name = "monto")
    double monto;

    @Column(name = "metodo")
    String metodo;

    @Column(name = "motivo")
    String motivo;

    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "fecha")
    Date fecha;

    @Column(name = "idUsuario")
    Integer idUsuario;

    @ManyToOne
    @JoinColumn(name = "idVenta", insertable = false, updatable = false)
    VentaModel venta;

    @ManyToOne
    @JoinColumn(name = "idUsuario", insertable = false, updatable = false)
    UsuarioModel usuario;
}
