package com.taller.proye01.modelPizza;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "proveedor")
public class ProveedorModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idProveedor")
    Integer idProveedor;

    @Column(name = "nombre", length = 100)
    String nombre;

    @Column(name = "telefono", length = 20)
    String telefono;

    @Column(name = "email", length = 100)
    String email;

    @Column(name = "tipoProducto", length = 100)
    String tipoProducto;

    @Column(name = "plazoPago")
    Integer plazoPago;

    @Column(name = "direccion", length = 255)
    String direccion;
    
    @JsonIgnore
    @OneToMany(mappedBy = "proveedor")
    List<ProductoModel> productos;
}