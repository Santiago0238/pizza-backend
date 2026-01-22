package com.taller.proye01.modelPizza;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
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
@Table(name = "usuario")
public class UsuarioModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idUsuario")
    private Integer idUsuario;

    @Column(name = "idRol")
    private Integer idRol;

    @Column(name = "nombreUsuario", length = 50)
    private String nombreUsuario;

    @Column(name = "contrasena", length = 255)
    private String contrasena;

    @Column(name = "telefono", length = 20)
    private String telefono;

    @Column(name = "correo", length = 100)
    private String correo;

    @Column(name = "estado")
    private Integer estado;
    
    @JsonIgnore
    @ManyToOne
    @JoinColumn(name = "idRol", insertable = false, updatable = false)
    RolModel rol;

    @Getter(AccessLevel.NONE)
	@Setter(AccessLevel.NONE)
    @OneToMany(mappedBy = "usuario")
    List<PedidoModel> pedidos;

    @Getter(AccessLevel.NONE)
	@Setter(AccessLevel.NONE)
    @OneToMany(mappedBy = "usuario")
    List<ReclamoModel> reclamos;

    @Getter(AccessLevel.NONE)
   	@Setter(AccessLevel.NONE)
    @OneToMany(mappedBy = "usuario")
    List<RolUsuModel> rolesUsuario;

    @Getter(AccessLevel.NONE)
	@Setter(AccessLevel.NONE)
    @OneToMany(mappedBy = "usuario")
    List<TransaccionModel> transacciones;

    @Getter(AccessLevel.NONE)
	@Setter(AccessLevel.NONE)
    @OneToMany(mappedBy = "usuario")
    List<VentaModel> ventas;
}