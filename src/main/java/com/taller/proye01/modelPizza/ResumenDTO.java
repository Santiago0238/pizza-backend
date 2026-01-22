package com.taller.proye01.modelPizza;

import lombok.Data;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ResumenDTO {

    // RF10.1: cantidad de ventas
    // RF10.2: cantidad de ingresos (pagos)
    private long cantidadVentas;

    private double totalIngresos;
    private double totalEgresos;  // solo para cashflow
    private double balance;       // solo para cashflow
}