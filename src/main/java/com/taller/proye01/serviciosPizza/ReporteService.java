package com.taller.proye01.serviciosPizza;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

import org.springframework.stereotype.Service;

import com.taller.proye01.modelPizza.Devolucion;
import com.taller.proye01.modelPizza.FilaTablaDTO;
import com.taller.proye01.modelPizza.ReporteFiltroDTO;
import com.taller.proye01.modelPizza.ReporteResponseDTO;
import com.taller.proye01.modelPizza.ResumenDTO;
import com.taller.proye01.modelPizza.SerieTemporalDTO;
import com.taller.proye01.modelPizza.TransaccionModel;
import com.taller.proye01.modelPizza.VentaModel;
import com.taller.proye01.repositoryPizza.DevolucionRepo;
import com.taller.proye01.repositoryPizza.TransaccionRepo;
import com.taller.proye01.repositoryPizza.VentaRepo;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class ReporteService {

    private final VentaRepo ventaRepo;
    private final TransaccionRepo transaccionRepo;
    private final DevolucionRepo devolucionRepo;

    private static final DateTimeFormatter RANGE_FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy");

    // Punto de entrada único
    public ReporteResponseDTO generar(ReporteFiltroDTO filtro) {
        if (filtro.getTipo() == null) {
            throw new IllegalArgumentException("Debes indicar el tipo de reporte (ventas o cashflow)");
        }
        return switch (filtro.getTipo().toLowerCase()) {
            case "ventas"   -> generarReporteVentas(filtro);
            case "cashflow" -> generarReporteCashflow(filtro);
            default -> throw new IllegalArgumentException("Tipo de reporte no soportado: " + filtro.getTipo());
        };
    }

    // ===== Helpers para fechas =====

    private void normalizarYValidarRango(ReporteFiltroDTO filtro) {
        LocalDate hoy = LocalDate.now();
        LocalDate desde = Optional.ofNullable(filtro.getDesde()).orElse(hoy);
        LocalDate hasta = Optional.ofNullable(filtro.getHasta()).orElse(hoy);

        if (desde.isAfter(hasta)) {
            throw new IllegalArgumentException("Rango inválido: Desde > Hasta");
        }
        if (hasta.isAfter(hoy)) {
            throw new IllegalArgumentException("Seleccione un rango válido (sin fechas futuras)");
        }

        filtro.setDesde(desde);
        filtro.setHasta(hasta);
    }

    private java.sql.Date toSqlDate(LocalDate d) {
        return java.sql.Date.valueOf(d);
    }

    private String rangoTexto(LocalDate desde, LocalDate hasta) {
        return desde.format(RANGE_FMT) + " – " + hasta.format(RANGE_FMT);
    }

    // ===== RF10.1: Reporte de Ventas por Fecha =====

    private ReporteResponseDTO generarReporteVentas(ReporteFiltroDTO filtro) {
        normalizarYValidarRango(filtro);

        java.sql.Date d1 = toSqlDate(filtro.getDesde());
        java.sql.Date d2 = toSqlDate(filtro.getHasta());

        List<VentaModel> ventas = ventaRepo.ventasPorRangoFecha(d1, d2);

        // Si quieres considerar solo ventas pagadas, aquí podrías filtrar por estado
        // ventas = ventas.stream()
        //        .filter(v -> "PAGADA".equalsIgnoreCase(v.getEstado()))
        //        .toList();

        double totalIngresos = 0d;
        Map<String, Double> metodosPago = new HashMap<>();
        Map<String, Double> ingresosPorDia = new HashMap<>();
        List<FilaTablaDTO> filas = new ArrayList<>();

        for (VentaModel v : ventas) {
            double monto = v.getTotal();
            totalIngresos += monto;

            // Agrupar por método de pago (tipodepago)
            if (v.getTipoDePago() != null) {
                String metodo = v.getTipoDePago().trim().toUpperCase();
                metodosPago.merge(metodo, monto, Double::sum);
            }

            // Serie por día (yyyy-MM-dd)
            String fechaStr = v.getFecha() != null ? v.getFecha().toString() : "";
            if (!fechaStr.isEmpty()) {
                ingresosPorDia.merge(fechaStr, monto, Double::sum);
            }

            // Fila de tabla
            FilaTablaDTO fila = new FilaTablaDTO();
            fila.setFecha(fechaStr);
            fila.setTipo(""); // no aplica en reporte de ventas
            fila.setCategoria(""); // opcional: podrías usar estado
            String detalle = "Pedido #" + v.getIdPedido() +
                    " - Cliente #" + v.getIdCliente();
            fila.setDetalle(detalle);
            fila.setMetodo(v.getTipoDePago());
            fila.setMonto(monto);

            filas.add(fila);
        }

        // Serie temporal
        List<String> dias = new ArrayList<>(ingresosPorDia.keySet());
        Collections.sort(dias);
        List<Double> valoresIngresos = dias.stream()
                .map(ingresosPorDia::get)
                .toList();

        SerieTemporalDTO serie = new SerieTemporalDTO();
        serie.setEtiquetas(dias);
        serie.setValoresIngresos(valoresIngresos);
        serie.setValoresEgresos(null);

        // Resumen
        ResumenDTO resumen = new ResumenDTO();
        resumen.setCantidadVentas(ventas.size());
        resumen.setTotalIngresos(totalIngresos);
        resumen.setTotalEgresos(0);
        resumen.setBalance(0);

        // Armar respuesta
        ReporteResponseDTO resp = new ReporteResponseDTO();
        resp.setTitulo("Reporte de Ventas por Fecha");
        resp.setRango(rangoTexto(filtro.getDesde(), filtro.getHasta()));
        resp.setGeneradoEn(LocalDateTime.now());
        resp.setResumen(resumen);
        resp.setSerieTemporal(serie);
        resp.setMetodosPago(metodosPago);
        resp.setEgresosPorCategoria(null);
        resp.setFilas(filas);

        return resp;
    }

    // ===== RF10.2: Reporte de Ingresos / Egresos =====

    private ReporteResponseDTO generarReporteCashflow(ReporteFiltroDTO filtro) {
        normalizarYValidarRango(filtro);

        java.sql.Date d1 = toSqlDate(filtro.getDesde());
        java.sql.Date d2 = toSqlDate(filtro.getHasta());

        List<FilaTablaDTO> movimientos = new ArrayList<>();

        // --- Ingresos por Transacción (Ventas pagadas) ---
        if (filtro.isIncluirVentas()) {
            List<TransaccionModel> trans = transaccionRepo.transaccionesPorRangoFecha(d1, d2);
            for (TransaccionModel t : trans) {
                FilaTablaDTO fila = new FilaTablaDTO();
                String fechaStr = t.getFecha() != null ? t.getFecha().toString() : "";
                fila.setFecha(fechaStr);
                fila.setTipo("Ingreso");
                fila.setCategoria("Venta");
                fila.setDetalle("Pedido #" + t.getIdPedido());
                fila.setMetodo(t.getTipoPago());
                fila.setMonto(t.getMonto());
                movimientos.add(fila);
            }
        }

        // --- Egresos por Devolución ---
        if (filtro.isIncluirDevoluciones()) {
            List<Devolucion> devs = devolucionRepo.devolucionesPorRangoFecha(d1, d2);
            for (Devolucion d : devs) {
                FilaTablaDTO fila = new FilaTablaDTO();
                String fechaStr = d.getFecha() != null ? d.getFecha().toString() : "";
                fila.setFecha(fechaStr);
                fila.setTipo("Egreso");
                fila.setCategoria("Devolución");

                String detalle = "Devolución de venta";
                if (d.getVenta() != null && d.getVenta().getIdVenta() != null) {
                    detalle += " #" + d.getVenta().getIdVenta();
                }
                fila.setDetalle(detalle);

                fila.setMetodo(""); // normalmente una devolución no tiene método de pago en esta tabla

                // Ajusta el nombre del campo de monto según tu entidad Devolucion
                double monto = d.getMonto(); // por ejemplo: getMonto(), getImporte(), getTotalDevolucion()
                fila.setMonto(monto);

                movimientos.add(fila);
            }
        }

        // Si más adelante tienes tabla de gastos/compras, aquí se integraría:
        // if (filtro.isIncluirGastos()) { ... }

        // Ordenar por fecha (String "yyyy-MM-dd")
        movimientos.sort(Comparator.comparing(FilaTablaDTO::getFecha));

        // Totales y agregados
        double totalIngresos = movimientos.stream()
                .filter(m -> "Ingreso".equalsIgnoreCase(m.getTipo()))
                .mapToDouble(FilaTablaDTO::getMonto)
                .sum();

        double totalEgresos = movimientos.stream()
                .filter(m -> "Egreso".equalsIgnoreCase(m.getTipo()))
                .mapToDouble(FilaTablaDTO::getMonto)
                .sum();

        double balance = totalIngresos - totalEgresos;

        // Serie temporal (por día)
        Map<String, double[]> serieMap = new TreeMap<>(); // fecha -> [ing, eg]
        Map<String, Double> egresosPorCategoria = new HashMap<>();

        for (FilaTablaDTO m : movimientos) {
            String fecha = m.getFecha();
            if (fecha == null || fecha.isEmpty()) continue;
            serieMap.putIfAbsent(fecha, new double[]{0d, 0d});
            double[] arr = serieMap.get(fecha);

            if ("Ingreso".equalsIgnoreCase(m.getTipo())) {
                arr[0] += m.getMonto();
            } else {
                arr[1] += m.getMonto();
                egresosPorCategoria.merge(
                        m.getCategoria() != null ? m.getCategoria() : "Sin categoría",
                        m.getMonto(),
                        Double::sum
                );
            }
        }

        List<String> etiquetas = new ArrayList<>(serieMap.keySet());
        List<Double> valsIng = etiquetas.stream().map(f -> serieMap.get(f)[0]).toList();
        List<Double> valsEg  = etiquetas.stream().map(f -> serieMap.get(f)[1]).toList();

        SerieTemporalDTO serie = new SerieTemporalDTO();
        serie.setEtiquetas(etiquetas);
        serie.setValoresIngresos(valsIng);
        serie.setValoresEgresos(valsEg);

        ResumenDTO resumen = new ResumenDTO();
        resumen.setCantidadVentas(
                movimientos.stream().filter(m -> "Ingreso".equalsIgnoreCase(m.getTipo())).count()
        );
        resumen.setTotalIngresos(totalIngresos);
        resumen.setTotalEgresos(totalEgresos);
        resumen.setBalance(balance);

        ReporteResponseDTO resp = new ReporteResponseDTO();
        resp.setTitulo("Reporte de Ingresos y Egresos");
        resp.setRango(rangoTexto(filtro.getDesde(), filtro.getHasta()));
        resp.setGeneradoEn(LocalDateTime.now());
        resp.setResumen(resumen);
        resp.setSerieTemporal(serie);
        resp.setMetodosPago(null);
        resp.setEgresosPorCategoria(egresosPorCategoria);
        resp.setFilas(movimientos);

        return resp;
    }
}
