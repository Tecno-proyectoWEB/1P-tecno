package negocio;

import data.DNotaVenta;
import data.DDetalleVenta;
import data.DCarrito;
import data.DItemCarrito;
import data.DUsuario;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * Clase de lógica de negocio para nota_venta
 */
public class NNotaVenta {
    
    private DNotaVenta dNotaVenta;
    private DDetalleVenta dDetalleVenta;
    private DCarrito dCarrito;
    private DItemCarrito dItemCarrito;
    private DUsuario dUsuario;
    
    public NNotaVenta() {
        this.dNotaVenta = new DNotaVenta();
        this.dDetalleVenta = new DDetalleVenta();
        this.dCarrito = new DCarrito();
        this.dItemCarrito = new DItemCarrito();
        this.dUsuario = new DUsuario();
    }
    
    /**
     * Obtiene todas las notas de venta
     */
    public List<String[]> getAll() throws SQLException {
        return dNotaVenta.getAll();
    }
    
    /**
     * Obtiene una nota de venta por ID
     */
    public List<String[]> getById(int id) throws SQLException {
        return dNotaVenta.getById(id);
    }
    
    /**
     * Obtiene notas de venta por usuario
     */
    public List<String[]> getByUsuarioId(int usuarioId) throws SQLException {
        return dNotaVenta.getByUsuarioId(usuarioId);
    }
    
    /**
     * Obtiene notas de venta por email del usuario
     */
    public List<String[]> getByUsuarioEmail(String email) throws SQLException {
        // Primero obtener el usuario por email
        List<String[]> usuarioData = dUsuario.getByEmail(email);
        if (usuarioData.isEmpty()) {
            return new ArrayList<>();
        }
        
        int usuarioId = Integer.parseInt(usuarioData.get(0)[0]);
        return dNotaVenta.getByUsuarioId(usuarioId);
    }
    
    /**
     * Crea una nota de venta desde el carrito
     */
    public List<String[]> crearNotaVentaDesdeCarrito(String email, Integer pedidoId, String observaciones) throws SQLException {
        // 1. Obtener el usuario por email
        List<String[]> usuarioData = dUsuario.getByEmail(email);
        if (usuarioData.isEmpty()) {
            throw new SQLException("Usuario no encontrado para el email: " + email);
        }
        
        int usuarioId = Integer.parseInt(usuarioData.get(0)[0]);
        
        // 2. Obtener el carrito activo del usuario
        List<String[]> carritoData = dCarrito.getByUsuarioId(usuarioId);
        if (carritoData.isEmpty()) {
            throw new SQLException("No hay carrito activo para el usuario");
        }
        
        int carritoId = Integer.parseInt(carritoData.get(0)[0]);
        System.out.println("🔍 DEBUG NOTA VENTA: Usando carrito ID: " + carritoId + " para usuario: " + usuarioId);
        
        // 3. Obtener los detalles del carrito
        List<String[]> detallesCarrito = dItemCarrito.getByCarritoId(carritoId);
        System.out.println("🔍 DEBUG NOTA VENTA: Detalles del carrito encontrados: " + detallesCarrito.size());
        
        if (detallesCarrito.isEmpty()) {
            throw new SQLException("El carrito está vacío");
        }
        
        // 4. Calcular el total
        double total = 0.0;
        for (String[] detalle : detallesCarrito) {
            double subtotal = Double.parseDouble(detalle[5]); // subtotal (precio_unitario × cantidad)
            System.out.println("🔍 DEBUG NOTA VENTA: Producto " + detalle[6] + 
                " - Cantidad: " + detalle[3] + 
                " - Precio Unit: " + detalle[4] + 
                " - Subtotal: " + detalle[5]);
            total += subtotal;
        }
        System.out.println("🔍 DEBUG NOTA VENTA: Total calculado: " + total);
        
        // 5. Crear la nota de venta
        String fecha = LocalDate.now().format(DateTimeFormatter.ISO_LOCAL_DATE);
        List<String[]> notaVenta = dNotaVenta.save(usuarioId, pedidoId, fecha, total, "pendiente", observaciones);
        
        if (notaVenta.isEmpty()) {
            throw new SQLException("Error al crear la nota de venta");
        }
        
        int notaVentaId = Integer.parseInt(notaVenta.get(0)[0]);
        
        // 6. Crear los detalles de venta
        for (String[] detalle : detallesCarrito) {
            int productoId = Integer.parseInt(detalle[2]);
            int cantidad = Integer.parseInt(detalle[3]);
            double precioTotal = Double.parseDouble(detalle[5]); // subtotal correcto
            
            dDetalleVenta.save(notaVentaId, productoId, cantidad, precioTotal);
        }
        
        // 7. Desactivar el carrito actual
        dCarrito.updateActivo(carritoId, false);
        
        return notaVenta;
    }
    
    /**
     * Guarda una nueva nota de venta
     */
    public List<String[]> save(int usuarioId, Integer pedidoId, String fecha, double total, String estado, String observaciones) throws SQLException {
        return dNotaVenta.save(usuarioId, pedidoId, fecha, total, estado, observaciones);
    }
    
    /**
     * Actualiza una nota de venta
     */
    public List<String[]> update(int id, String estado, String observaciones) throws SQLException {
        return dNotaVenta.update(id, estado, observaciones);
    }
    
    /**
     * Elimina una nota de venta
     */
    /**
     * Obtiene el total de ventas por usuario
     */
    public double getTotalVentasByUsuario(int usuarioId) throws SQLException {
        return dNotaVenta.getTotalVentasByUsuario(usuarioId);
    }
    
    /**
     * Obtiene estadísticas de ventas
     */
    public List<String[]> getEstadisticasVentas() throws SQLException {
        return dNotaVenta.getEstadisticasVentas();
    }
    
    /**
     * Obtiene una nota de venta completa con detalles
     */
    public List<String[]> getNotaVentaCompleta(int notaVentaId) throws SQLException {
        List<String[]> notaVenta = dNotaVenta.getById(notaVentaId);
        if (notaVenta.isEmpty()) {
            return new ArrayList<>();
        }
        
        // Agregar los detalles de venta
        List<String[]> detallesVenta = dDetalleVenta.getByNotaVentaId(notaVentaId);
        
        // Combinar la información
        List<String[]> resultado = new ArrayList<>();
        resultado.addAll(notaVenta);
        
        // Agregar los detalles como filas adicionales
        for (String[] detalle : detallesVenta) {
            resultado.add(detalle);
        }
        
        return resultado;
    }
    
    /**
     * Procesa el pago de una nota de venta
     */
    public List<String[]> procesarPago(int notaVentaId) throws SQLException {
        // Actualizar el estado a "completada"
        return dNotaVenta.update(notaVentaId, "completada", "Pago procesado exitosamente");
    }
    
    /**
     * Cancela una nota de venta
     */
    public List<String[]> cancelarNotaVenta(int notaVentaId) throws SQLException {
        // Actualizar el estado a "cancelada"
        return dNotaVenta.update(notaVentaId, "cancelada", "Nota de venta cancelada");
    }
    
    /**
     * Obtiene notas de venta por estado
     */
    public List<String[]> getByEstado(String estado) throws SQLException {
        List<String[]> todasLasVentas = dNotaVenta.getAll();
        List<String[]> ventasFiltradas = new ArrayList<>();
        
        for (String[] venta : todasLasVentas) {
            if (venta[5].equals(estado)) { // índice 5 es el estado
                ventasFiltradas.add(venta);
            }
        }
        
        return ventasFiltradas;
    }
    
    /**
     * Obtiene ventas pendientes de pago
     */
    public List<String[]> getVentasPendientes() throws SQLException {
        return getByEstado("pendiente");
    }
    
    /**
     * Obtiene ventas completadas
     */
    public List<String[]> getVentasCompletadas() throws SQLException {
        return getByEstado("completada");
    }
    
    /**
     * Obtiene ventas canceladas
     */
    public List<String[]> getVentasCanceladas() throws SQLException {
        return getByEstado("cancelada");
    }
    
    public void disconnect() {
        dNotaVenta.disconnect();
        dDetalleVenta.disconnect();
        dCarrito.disconnect();
        dItemCarrito.disconnect();
        dUsuario.disconnect();
    }
} 