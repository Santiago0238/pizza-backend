package com.taller.proye01.modelPizza;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FilaTablaDTO {

    // Fecha en texto (luego puedes formatear en el front)
    private String fecha;

    // "Ingreso" / "Egreso" (para cashflow) o vacío en reporte de ventas
    private String tipo;

    // "Ventas", "Devoluciones", "Gastos…", etc.
    private String categoria;

    // Detalle (cliente, descripción, etc.)
    private String detalle;

    // Solo en reporte de ventas: método(s) de pago (EFECTIVO+QR, etc.)
    private String metodo;

    // Monto en Bs
    private double monto;
}
