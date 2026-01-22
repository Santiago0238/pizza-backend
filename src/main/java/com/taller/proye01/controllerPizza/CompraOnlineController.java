package com.taller.proye01.controllerPizza;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;

import com.taller.proye01.modelPizza.ClienteModel;
import com.taller.proye01.modelPizza.DireccionEntregaModel;
import com.taller.proye01.modelPizza.PedidoDetalleModel;
import com.taller.proye01.modelPizza.PedidoModel;
import com.taller.proye01.modelPizza.TransaccionModel;
import com.taller.proye01.serviciosPizza.ClienteService;
import com.taller.proye01.serviciosPizza.DireccionEntregaService;
import com.taller.proye01.serviciosPizza.PedidoDetalleService;
import com.taller.proye01.serviciosPizza.PedidoService;
import com.taller.proye01.serviciosPizza.TransaccionService;

import java.sql.Date;
import java.sql.Timestamp;

@RestController
@RequestMapping("/compra-online")
@CrossOrigin(origins = "http://localhost:4200")
public class CompraOnlineController {

    @Autowired
    private ClienteService clienteService;

    @Autowired
    private DireccionEntregaService direccionEntregaService;

    @Autowired
    private PedidoService pedidoService;

    @Autowired
    private PedidoDetalleService pedidoDetalleService;

    @Autowired
    private TransaccionService transaccionService;
    Date hoy = new Date(System.currentTimeMillis());

    @PostMapping("/crear")
    public ResponseEntity<?> crearPedidoOnline(@RequestBody PedidoOnlineRequestDTO dto) {
        try {
            if (dto == null || dto.getDatosCliente() == null || dto.getPedido() == null
                    || dto.getDetalles() == null || dto.getDetalles().isEmpty()
                    || dto.getTransaccion() == null) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "Datos incompletos para registrar la compra online"));
            }

            // ========= 1) Cliente =========
            DatosClienteEntregaDTO dc = dto.getDatosCliente();

            ClienteModel cliente = clienteService.buscarPorTelefono(dc.getTelefono());

            if (cliente == null) {
                cliente = new ClienteModel();
                cliente.setNombre(dc.getNombre());
                cliente.setTelefono(dc.getTelefono());
                cliente.setEmail(null); // si no envías email desde el front
                cliente.setActivo(1);
                cliente.setFechaRegistro(hoy);
                cliente = clienteService.guardar(cliente);
            }

            // ========= 2) Dirección de entrega =========
            DireccionEntregaModel dir = new DireccionEntregaModel();
            dir.setIdCliente(cliente.getIdCliente());
            dir.setCalle(dc.getDireccion());
            dir.setZona(dc.getZona());
            dir.setCiudad("Tarija"); // o la ciudad que corresponda
            dir.setNumero(null);
            dir.setReferencia(dc.getReferencia());
            dir.setUbicacionGps(null);

            dir = direccionEntregaService.guardar(dir);
            Date hoy = new Date(System.currentTimeMillis());
            // ========= 3) Pedido =========
            PedidoModel pedidoReq = dto.getPedido();
            PedidoModel pedido = new PedidoModel();

            pedido.setIdCliente(cliente.getIdCliente());
            pedido.setEstadoPedido(pedidoReq.getEstadoPedido() != null ? pedidoReq.getEstadoPedido() : "PENDIENTE");
            pedido.setTipo(pedidoReq.getTipo()); // DELIVERY / RETIRO
            pedido.setIdDireccion(dir.getIdDireccion());
            pedido.setFecha(hoy);
            pedido.setTiempoEstimadoEntrega(pedidoReq.getTiempoEstimadoEntrega());
            pedido.setTotal(pedidoReq.getTotal());
            pedido.setIdUsuario(pedidoReq.getIdUsuario()); // opcional, si se guarda el usuario del sistema

            pedido = pedidoService.guardar(pedido);

            // ========= 4) Detalles del pedido =========
            List<PedidoDetalleModel> detallesGuardados = new ArrayList<>();

            for (PedidoDetalleModel detReq : dto.getDetalles()) {
                PedidoDetalleModel det = new PedidoDetalleModel();
                det.setIdPedido(pedido.getIdPedido());
                det.setIdProducto(detReq.getIdProducto());
                det.setCantidad(detReq.getCantidad());
                det.setPrecioUnitario(detReq.getPrecioUnitario());
                det = pedidoDetalleService.guardar(det);
                detallesGuardados.add(det);
            }

            // ========= 5) Transacción =========
            TransaccionModel trxReq = dto.getTransaccion();
            TransaccionModel trx = new TransaccionModel();

            trx.setIdPedido(pedido.getIdPedido());
            trx.setTipoPago(trxReq.getTipoPago());
            trx.setMonto(trxReq.getMonto());
            trx.setFecha(hoy);
            trx.setIdUsuario(trxReq.getIdUsuario()); // opcional

            trx = transaccionService.guardar(trx);

            // ========= 6) Construir respuesta =========
            PedidoOnlineResponseDTO resp = new PedidoOnlineResponseDTO();
            resp.setPedido(pedido);
            resp.setDetalles(detallesGuardados);
            resp.setTransaccion(trx);
            resp.setMensaje("Pedido registrado correctamente");

            return ResponseEntity.ok(resp);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", "Error al registrar la compra online: " + e.getMessage()));
        }
    }
    


    public static class DatosClienteEntregaDTO {
        private String nombre;
        private String telefono;
        private String direccion;
        private String zona;
        private String referencia;

        // Getters y setters
        public String getNombre() { return nombre; }
        public void setNombre(String nombre) { this.nombre = nombre; }

        public String getTelefono() { return telefono; }
        public void setTelefono(String telefono) { this.telefono = telefono; }

        public String getDireccion() { return direccion; }
        public void setDireccion(String direccion) { this.direccion = direccion; }

        public String getZona() { return zona; }
        public void setZona(String zona) { this.zona = zona; }

        public String getReferencia() { return referencia; }
        public void setReferencia(String referencia) { this.referencia = referencia; }
    }

  
    public static class PedidoOnlineRequestDTO {
        private DatosClienteEntregaDTO datosCliente;
        private PedidoModel pedido;
        private List<PedidoDetalleModel> detalles;
        private TransaccionModel transaccion;

        public DatosClienteEntregaDTO getDatosCliente() { return datosCliente; }
        public void setDatosCliente(DatosClienteEntregaDTO datosCliente) { this.datosCliente = datosCliente; }

        public PedidoModel getPedido() { return pedido; }
        public void setPedido(PedidoModel pedido) { this.pedido = pedido; }

        public List<PedidoDetalleModel> getDetalles() { return detalles; }
        public void setDetalles(List<PedidoDetalleModel> detalles) { this.detalles = detalles; }

        public TransaccionModel getTransaccion() { return transaccion; }
        public void setTransaccion(TransaccionModel transaccion) { this.transaccion = transaccion; }
    }

    public static class PedidoOnlineResponseDTO {
        private PedidoModel pedido;
        private List<PedidoDetalleModel> detalles;
        private TransaccionModel transaccion;
        private String mensaje;

        public PedidoModel getPedido() { return pedido; }
        public void setPedido(PedidoModel pedido) { this.pedido = pedido; }

        public List<PedidoDetalleModel> getDetalles() { return detalles; }
        public void setDetalles(List<PedidoDetalleModel> detalles) { this.detalles = detalles; }

        public TransaccionModel getTransaccion() { return transaccion; }
        public void setTransaccion(TransaccionModel transaccion) { this.transaccion = transaccion; }

        public String getMensaje() { return mensaje; }
        public void setMensaje(String mensaje) { this.mensaje = mensaje; }
    }
}
