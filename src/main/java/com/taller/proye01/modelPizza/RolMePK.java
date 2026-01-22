package com.taller.proye01.modelPizza;

import jakarta.persistence.Embeddable;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;


@Embeddable
@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode

public class RolMePK {   
     Integer idRol;
     Integer idMenu;
}