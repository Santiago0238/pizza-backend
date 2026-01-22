package com.taller.proye01.modelPizza;

import java.util.List;
import java.util.Set;

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
@Data
@Entity
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "Menu")
public class MenuModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "idMenu")
    Integer idMenu;

    @Column(name = "nombre", length = 40)
    String nombre;

    @Column(name = "estado")
    Integer estado;

    @Getter(AccessLevel.NONE)
	@Setter(AccessLevel.NONE)
    @OneToMany(mappedBy = "menu")
    Set<RolMeModel> menpro;
}
