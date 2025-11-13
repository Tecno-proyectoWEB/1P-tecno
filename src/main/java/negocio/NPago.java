package negocio;

import data.DPago;
import data.DPedido;
import java.sql.SQLException;
import java.util.List;

/**
 * Clase de lógica de negocio para pagos
 */
public class NPago {
    private final DPago dPago;
    private final DPedido dPedido;
    
    public NPago() {
        this.dPago = new DPago();
        this.dPedido = new DPedido();
    }
    
    /**
     * Registra un nuevo pago
     * @param pedidoId ID del pedido
     * @param monto Monto del pago
     * @param tipoPago Tipo de pago (INICIAL, FINAL)
     * @param metodoPagoId ID del método de pago
     * @return Lista con los datos del pago registrado
     * @throws SQLException 
     */
    public List<String[]> save(int pedidoId, double monto, String tipoPago, int metodoPagoId) throws SQLException {
        // Primero verificamos el estado del pedido
        List<String[]> pedido = dPedido.getById(pedidoId);
        if (pedido.isEmpty()) {
            throw new SQLException("Pedido no encontrado");
        }
        
        String estado = pedido.get(0)[5]; // El estado está en la posición 5
        if ("cancelado".equals(estado) || "entregado".equals(estado)) {
            throw new SQLException("No se puede registrar pago para un pedido " + estado);
        }
        
        return dPago.save(pedidoId, monto, tipoPago, metodoPagoId);
    }
    
    /**
     * Obtiene todos los pagos de un pedido
     * @param pedidoId ID del pedido
     * @return Lista con los pagos del pedido
     * @throws SQLException 
     */
    public List<String[]> getPagosByPedido(int pedidoId) throws SQLException {
        return dPago.getPagosByPedido(pedidoId);
    }
    
    /**
     * Obtiene el total pagado de un pedido
     * @param pedidoId ID del pedido
     * @return Monto total pagado
     * @throws SQLException 
     */
    public double getTotalPagadoPedido(int pedidoId) throws SQLException {
        return dPago.getTotalPagadoPedido(pedidoId);
    }
    
    public void disconnect() {
        dPago.disconnect();
        dPedido.disconnect();
    }
}
