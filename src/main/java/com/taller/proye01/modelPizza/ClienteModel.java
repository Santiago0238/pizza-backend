package com.taller.proye01.modelPizza;

import java.sql.Date;
import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
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
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "Cliente")
public class ClienteModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idCliente")
    Integer idCliente;

    @Column(name = "nombre", length = 100)
    String nombre;

    @Column(name = "apellido", length = 100)
    String apellido;

    @Column(name = "email", length = 100)
    String email;

    @Column(name = "telefono", length = 20)
    String telefono;

    @Column(name = "fechaRegistro")
    @Temporal(TemporalType.DATE)
    Date fechaRegistro;

    @Column(name = "activo")
    Integer activo;

   
	
    @OneToMany(mappedBy = "cliente")
    List<DireccionEntregaModel> direcciones;

    @Getter(AccessLevel.NONE)
	@Setter(AccessLevel.NONE)
    @OneToMany(mappedBy = "cliente")
    List<HistorialComprasModel> historialCompras;

    @Getter(AccessLevel.NONE)
	@Setter(AccessLevel.NONE)
    @OneToMany(mappedBy = "cliente")
    List<PedidoModel> pedidos;

    @Getter(AccessLevel.NONE)
	@Setter(AccessLevel.NONE)
    @OneToMany(mappedBy = "cliente")
    List<VentaModel> ventas;
}
