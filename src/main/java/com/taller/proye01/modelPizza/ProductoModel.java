package com.taller.proye01.modelPizza;

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
@Table(name = "producto")
public class ProductoModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idProducto")
    Integer idProducto;

    @Column(name = "idCategoria")
    Integer idCategoria;

    @Column(name = "idProveedor")
    Integer idProveedor;

    @Column(name = "nombre", length = 100)
    String nombre;

    @Column(name = "precio")
    double precio;

    @Column(name = "disponible")
    Integer disponible;

    @Column(name = "imagenUrl", length = 255)
    String imagenUrl;
    
   
    @ManyToOne
    @MapsId("idCategoria")
    @JoinColumn(name = "idCategoria")
    CategoriaProductoModel categoriaProducto;
    
    
    @ManyToOne
    @MapsId("idProveedor")
    @JoinColumn(name = "idProveedor", insertable = false, updatable = false)
    ProveedorModel proveedor;
    
    @Getter(AccessLevel.NONE)
	@Setter(AccessLevel.NONE)
    @OneToMany(mappedBy = "producto")
    List<PedidoDetalleModel> pedidos;
    
    @Getter(AccessLevel.NONE)
	@Setter(AccessLevel.NONE)
    @OneToMany(mappedBy = "producto")
    List<PersonalizacionPizzaModel> personalizaciones;
   
    
    @Getter(AccessLevel.NONE)
	@Setter(AccessLevel.NONE)
	@OneToMany(mappedBy = "producto")
	Set<ProPromoModel> proPromo ;
    
    
}