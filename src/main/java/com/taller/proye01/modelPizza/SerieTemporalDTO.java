package com.taller.proye01.modelPizza;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class SerieTemporalDTO {

    // Fechas "yyyy-MM-dd"
    private List<String> etiquetas;

    // Serie de ingresos (ventas o ingresos del día)
    private List<Double> valoresIngresos;

    // Solo cashflow: serie de egresos
    private List<Double> valoresEgresos;
}