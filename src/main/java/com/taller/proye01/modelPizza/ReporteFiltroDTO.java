package com.taller.proye01.modelPizza;

import java.time.LocalDate;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReporteFiltroDTO {

    // "ventas" o "cashflow"
    private String tipo;

    private LocalDate desde;
    private LocalDate hasta;

    // Solo aplica para tipo = "cashflow"
    private boolean incluirVentas = true;
    private boolean incluirDevoluciones = true;
    private boolean incluirGastos = true;
}
