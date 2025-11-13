package negocio;

import data.DProducto;
import java.sql.SQLException;
import java.util.List;

/**
 * Clase de negocio para la gestión de productos
 */
public class NProducto {
    
    private final DProducto dProducto;
    
    public NProducto() {
        this.dProducto = new DProducto();
    }
    
    /**
     * Obtiene todos los productos
     */
    public List<String[]> getAll() throws SQLException {
        return dProducto.getAll();
    }
    
    /**
     * Obtiene un producto por ID
     */
    public List<String[]> getById(int id) throws SQLException {
        return dProducto.getById(id);
    }
    
    /**
     * Guarda un nuevo producto
     */
    public List<String[]> save(String nombre, double precioUnitario, String imagen, String descripcion, int categoriaId, int stock, Integer stockMinimo, String tiempo) throws SQLException {
        return dProducto.save(nombre, precioUnitario, imagen, descripcion, categoriaId, stock, stockMinimo, tiempo);
    }
    
    /**
     * Actualiza un producto existente
     */
    public List<String[]> update(int id, String nombre, double precioUnitario, String imagen, String descripcion, int categoriaId, int stock, Integer stockMinimo, String tiempo) throws SQLException {
        return dProducto.update(id, nombre, precioUnitario, imagen, descripcion, categoriaId, stock, stockMinimo, tiempo);
    }
    
    /**
     * Elimina un producto por ID
     */
    public boolean delete(int id) throws SQLException {
        return dProducto.delete(id);
    }
    
    /**
     * Obtiene productos por categoría
     */
    public List<String[]> getByCategoria(int categoriaId) throws SQLException {
        return dProducto.getByCategoria(categoriaId);
    }
    
    /**
     * Obtiene productos por término de búsqueda
     */
    public List<String[]> searchByTerm(String searchTerm) throws SQLException {
        return dProducto.searchByTerm(searchTerm);
    }
    
    /**
     * Actualiza el stock de un producto
     */
    public List<String[]> updateStock(int id, int cantidad) throws SQLException {
        return dProducto.updateStock(id, cantidad);
    }
    
    /**
     * Verifica si hay suficiente stock para un producto
     */
    public boolean hasEnoughStock(int id, int cantidad) throws SQLException {
        List<String[]> producto = dProducto.getById(id);
        if (!producto.isEmpty()) {
            int stockActual = Integer.parseInt(producto.get(0)[7]); // Asumiendo que el stock está en la posición 7
            return stockActual >= cantidad;
        }
        return false;
    }
    
    public void disconnect() {
        dProducto.disconnect();
    }
}