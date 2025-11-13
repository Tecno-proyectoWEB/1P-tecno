package negocio;

import data.DPromocion;
import java.sql.SQLException;
import java.util.List;

/**
 * Clase de negocio para la gestión de promociones
 */
public class NPromocion {
    
    private final DPromocion dPromocion;
    
    public NPromocion() {
        this.dPromocion = new DPromocion();
    }
    
    /**
     * Obtiene todas las promociones con información del producto
     */
    public List<String[]> getAll() throws SQLException {
        return dPromocion.getAll();
    }
    
    /**
     * Obtiene una promoción por ID con información del producto
     */
    public List<String[]> getById(int id) throws SQLException {
        return dPromocion.getById(id);
    }
    
    /**
     * Obtiene promociones por producto_id
     */
    public List<String[]> getByProductoId(int productoId) throws SQLException {
        return dPromocion.getByProductoId(productoId);
    }
    
    /**
     * Obtiene promociones activas (fecha actual entre fecha_inicio y fecha_fin)
     */
    public List<String[]> getActivas() throws SQLException {
        return dPromocion.getActivas();
    }
    
    /**
     * Guarda una nueva promoción
     */
    public List<String[]> save(String nombre, String fechaInicio, String fechaFin, String descuento, Integer productoId) throws SQLException {
        return dPromocion.save(nombre, fechaInicio, fechaFin, descuento, productoId);
    }
    
    /**
     * Actualiza una promoción existente
     */
    public List<String[]> update(int id, String nombre, String fechaInicio, String fechaFin, String descuento, Integer productoId) throws SQLException {
        return dPromocion.update(id, nombre, fechaInicio, fechaFin, descuento, productoId);
    }
    
    /**
     * Elimina una promoción por ID
     */
    public boolean delete(int id) throws SQLException {
        return dPromocion.delete(id);
    }
    
    /**
     * Verifica si existe una promoción por ID
     */
    public boolean existsById(int id) throws SQLException {
        return dPromocion.existsById(id);
    }
    
    public void disconnect() {
        dPromocion.disconnect();
    }
}
