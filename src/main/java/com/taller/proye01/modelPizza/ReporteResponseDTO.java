package com.taller.proye01.modelPizza;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReporteResponseDTO {

    private String titulo;            // "Reporte de Ventas por Fecha" / "Reporte de Ingresos y Egresos"
    private String rango;             // "dd/MM/yyyy – dd/MM/yyyy"
    private LocalDateTime generadoEn; // fecha/hora de generación (servidor)

    private ResumenDTO resumen;       // tarjetas superiores

    // Datos para gráficos
    private SerieTemporalDTO serieTemporal;             // línea/barras por día
    private Map<String, Double> metodosPago;            // solo ventas: efectivo, tarjeta, qr
    private Map<String, Double> egresosPorCategoria;    // solo cashflow: pastel de egresos

    // Datos de la tabla
    private List<FilaTablaDTO> filas;
}