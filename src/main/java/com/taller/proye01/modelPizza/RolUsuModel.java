package com.taller.proye01.modelPizza;


import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.MapsId;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "rolUsu")
public class RolUsuModel {

    @EmbeddedId
    RolUsuPK id;

    @ManyToOne
    @MapsId("idRol")
    @JoinColumn(name = "idRol")
    RolModel rol;

    @ManyToOne
    @MapsId("idUsuario")
    @JoinColumn(name = "idUsuario")
    UsuarioModel usuario;
}