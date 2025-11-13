package negocio;

import data.DCarrito;
import data.DItemCarrito;
import java.sql.SQLException;
import java.util.List;

/**
 * Clase de negocio para la gestión de carritos
 */
public class NCarrito {
    
    private final DCarrito dCarrito;
    private final DItemCarrito dItemCarrito;
    
    public NCarrito() {
        this.dCarrito = new DCarrito();
        this.dItemCarrito = new DItemCarrito();
    }
    
    /**
     * Obtiene el carrito activo del usuario con lógica inteligente
     */
    public List<String[]> getCarritoActivo(int usuarioId) throws SQLException {
        return dCarrito.getCarritoActivo(usuarioId);
    }
    
    /**
     * Obtiene un carrito por ID con información del usuario
     */
    public List<String[]> getById(int id) throws SQLException {
        return dCarrito.getById(id);
    }
    
    /**
     * Obtiene todos los carritos de un usuario
     */
    public List<String[]> getByUsuarioId(int usuarioId) throws SQLException {
        return dCarrito.getByUsuarioId(usuarioId);
    }
    
    /**
     * Cambia el estado activo del carrito
     */
    public List<String[]> updateActivo(int carritoId, boolean activo) throws SQLException {
        return dCarrito.updateActivo(carritoId, activo);
    }
    
    /**
     * Cambia el estado activo del carrito (método simplificado que retorna boolean)
     */
    public boolean cambiarEstadoCarrito(int carritoId, boolean activo) {
        try {
            List<String[]> resultado = dCarrito.updateActivo(carritoId, activo);
            return !resultado.isEmpty();
        } catch (SQLException e) {
            System.err.println("Error cambiando estado del carrito: " + e.getMessage());
            return false;
        }
    }
    
    /**
     * Elimina un carrito por ID
     */
    public boolean delete(int id) throws SQLException {
        return dCarrito.delete(id);
    }
    
    /**
     * Verifica si existe un carrito por ID
     */
    public boolean existsById(int id) throws SQLException {
        return dCarrito.existsById(id);
    }
    
    /**
     * Obtiene los detalles de un carrito con información de productos
     */
    public List<String[]> getDetallesCarrito(int carritoId) throws SQLException {
        return dItemCarrito.getByCarritoId(carritoId);
    }
    
    /**
     * Agrega un producto al carrito usando producto_id y cantidad
     */
    public List<String[]> agregarProducto(int carritoId, int productoId, int cantidad) throws SQLException {
        // Agregar el producto directamente al carrito (item_carrito)
        // El precio se obtiene dinámicamente de la tabla producto al consultar
        return dItemCarrito.save(carritoId, productoId, cantidad);
    }
    
    /**
     * Actualiza la cantidad de un producto en el carrito
     */
    public List<String[]> actualizarCantidad(int detalleId, int cantidad) throws SQLException {
        return dItemCarrito.updateCantidad(detalleId, cantidad);
    }
    
    /**
     * Elimina un producto del carrito
     */
    public boolean eliminarProducto(int detalleId) throws SQLException {
        return dItemCarrito.delete(detalleId);
    }
    
    /**
     * Vacía un carrito (elimina todos los productos)
     */
    public boolean vaciarCarrito(int carritoId) throws SQLException {
        return dItemCarrito.deleteByCarritoId(carritoId);
    }
    
    /**
     * Calcula el total de un carrito
     */
    public double calcularTotalCarrito(int carritoId) throws SQLException {
        return dItemCarrito.calcularTotalCarrito(carritoId);
    }
    
    public void disconnect() {
        dCarrito.disconnect();
        dItemCarrito.disconnect();
    }
} 