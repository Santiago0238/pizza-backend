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
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.Setter;
@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "CategoriaProducto")
public class CategoriaProductoModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idCategoria")
    Integer idCategoria;

    @Column(name = "nombreCategoria", length = 100)
    String nombreCategoria;

    @Column(name = "descripcion", length = 255)
    String descripcion;

    @Getter(AccessLevel.NONE)
	@Setter(AccessLevel.NONE)
    @OneToMany(mappedBy = "categoriaProducto")
    List<ProductoModel> productos;
}

