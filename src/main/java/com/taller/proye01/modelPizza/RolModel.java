package com.taller.proye01.modelPizza;

import lombok.AccessLevel;
import java.util.Set;

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
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "rol")
public class RolModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idRol")
    private Integer idRol;

    @Column(name = "nombreRol", length = 50)
    private String nombreRol;

    @Column(name = "descripcion", length = 50)
    private String descripcion;

    @JsonIgnore
    @OneToMany(mappedBy = "rol")
    Set<RolMeModel> menus;

    @JsonIgnore
    @OneToMany(mappedBy = "rol")
    Set<RolUsuModel> usuariosAsignados;

    @JsonIgnore
    @OneToMany(mappedBy = "rol")
    Set<UsuarioModel> usuarios;
}