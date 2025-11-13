package negocio;

import data.DPedido;
import data.DPago;
import data.DMetodoPago;
import data.DDireccion;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

/**
 * Clase de lógica de negocio para pedido
 */
public class NPedido {
    
    private final DPedido dPedido;
    private final DDireccion dDireccion;
    private final DPago dPago;
    
    public NPedido() {
        this.dPedido = new DPedido();
        this.dDireccion = new DDireccion();
        this.dPago = new DPago();
    }
    
    /**
     * Obtiene todos los pedidos
     */
    public List<String[]> getAll() throws SQLException {
        return dPedido.getAll();
    }
    
    /**
     * Obtiene un pedido por ID
     */
    public List<String[]> getById(int id) throws SQLException {
        return dPedido.getById(id);
    }
    
    /**
     * Crea un pedido simple sin dirección (solo con método de pago y usuario)
     */
    public List<String[]> crearPedidoSimple(String descripcion, double total, int metodoPagoId, int usuarioId) throws SQLException {
        return dPedido.save(descripcion, total, total, metodoPagoId, usuarioId);
    }
    
    /**
     * Obtiene pedidos por cliente (email)
     */
    public List<String[]> getByClienteEmail(String email) throws SQLException {
        return dPedido.getByClienteEmail(email);
    }
    
    /**
     * Crea un nuevo pedido con dirección
     */
    public List<String[]> crearPedido(String nombreDireccion, Double longitud, Double latitud, String referencia, 
                                     String descripcion, double total, int metodoPagoId, int usuarioId) throws SQLException {
        // 1. Crear la dirección
        List<String[]> direccion = dDireccion.save(nombreDireccion, longitud, latitud, referencia);
        if (direccion.isEmpty()) {
            throw new SQLException("Error al crear la dirección");
        }
        
        // 2. Crear el pedido
        return dPedido.save(descripcion, total, total, metodoPagoId, usuarioId);
    }
    
    /**
     * Actualiza el estado de un pedido
     */
    public List<String[]> updateEstado(int id, String estado) throws SQLException {
        return dPedido.updateEstado(id, estado);
    }
    
    /**
     * Elimina un pedido
     */
    public boolean delete(int id) throws SQLException {
        return dPedido.delete(id);
    }
    
    /**
     * Obtiene estadísticas de pedidos
     */
    public List<String[]> getEstadisticasPedidos() throws SQLException {
        return dPedido.getEstadisticasPedidos();
    }
    
  
    
    /**
     * Procesa un pedido (cambia estado a "procesando")
     */
    public List<String[]> procesarPedido(int pedidoId) throws SQLException {
        return dPedido.updateEstado(pedidoId, "procesando");
    }
    
  
    
    /**
     * Entrega un pedido (cambia estado a "entregado")
     */
    public List<String[]> entregarPedido(int pedidoId) throws SQLException {
        return dPedido.updateEstado(pedidoId, "entregado");
    }
    
    /**
     * Cancela un pedido (cambia estado a "cancelado")
     */
    public List<String[]> cancelarPedido(int pedidoId) throws SQLException {
        return dPedido.updateEstado(pedidoId, "cancelado");
    }
    
    /**
     * Obtiene pedidos pendientes de envío
     */
    public List<String[]> getPedidosPendientesEnvio() throws SQLException {
        return dPedido.getPedidosPendientesEnvio();
    }
    
    /**
     * Obtiene pedidos por usuario
     */
    public List<String[]> getByUsuario(int usuarioId) throws SQLException {
        return dPedido.getByUsuario(usuarioId);
    }
    
    /**
     * Obtiene pedidos por método de pago
     */
    public List<String[]> getByMetodoPago(int metodoPagoId) throws SQLException {
        return dPedido.getByMetodoPago(metodoPagoId);
    }
    
    /**
     * Genera URL de Google Maps para un pedido
     
    public String generarUrlGoogleMaps(int pedidoId) throws SQLException {
        List<String[]> pedido = dPedido.getById(pedidoId);
        if (pedido.isEmpty()) {
            return null;
        }
        
        String[] datos = pedido.get(0);
        int direccionId = Integer.parseInt(datos[1]); // direccion_id
        
        return dDireccion.generarUrlGoogleMaps(direccionId);
    }
    */
    
    /**
     * Obtiene pedidos cercanos a unas coordenadas
     
    public List<String[]> getPedidosCercanos(double latitud, double longitud, double radioKm) throws SQLException {
        return dPedido.getPedidosPendientesEnvio(); // Por simplicidad, retornamos todos los pendientes
        // En una implementación completa, filtraríamos por distancia
    }
    */
    
    /**
     * Realiza el pago de un pedido
     */
    public List<String[]> registrarPago(int pedidoId, double monto, String tipoPago, int metodoPagoId) throws SQLException {
        List<String[]> pedido = dPedido.getById(pedidoId);
        if (pedido.isEmpty()) {
            throw new SQLException("Pedido no encontrado");
        }
        
        String estado = pedido.get(0)[5]; // El estado está en la posición 5
        if ("entregado".equals(estado) || "cancelado".equals(estado)) {
            throw new SQLException("No se puede registrar pago para un pedido " + estado);
        }
        
        double totalPedido = Double.parseDouble(pedido.get(0)[3]); // importe_total
        double totalPagado = dPago.getTotalPagadoPedido(pedidoId);
        
        if (totalPagado + monto > totalPedido) {
            throw new SQLException("El monto excede el total del pedido");
        }
        
        // Registrar el pago
        dPago.save(pedidoId, monto, tipoPago, metodoPagoId);
        totalPagado += monto;
        
        // Actualizar estado del pedido si está completamente pagado
        if (Math.abs(totalPagado - totalPedido) < 0.01) { // Comparación con tolerancia
            return procesarPedido(pedidoId);
        }
        
        return pedido;
    }
    
    /**
     * Obtiene el total pagado de un pedido
     */
    public double getTotalPagadoPedido(int pedidoId) throws SQLException {
        return dPago.getTotalPagadoPedido(pedidoId);
    }
    
    /**
     * Obtiene los pagos de un pedido
     */
    public List<String[]> getPagosByPedido(int pedidoId) throws SQLException {
        return dPago.getPagosByPedido(pedidoId);
    }
    
    public void disconnect() {
        dPedido.disconnect();
        dDireccion.disconnect();
        dPago.disconnect();
    }
}