package negocio;

import data.DMetodoPago;
import java.sql.SQLException;
import java.util.List;

/**
 * Clase de negocio para la gestión de métodos de pago
 */
public class NMetodoPago {
    
    private final DMetodoPago dMetodoPago;
    
    public NMetodoPago() {
        this.dMetodoPago = new DMetodoPago();
    }
    
    /**
     * Obtiene todos los métodos de pago
     */
    public List<String[]> getAll() throws SQLException {
        return dMetodoPago.getAll();
    }
    
    /**
     * Obtiene un método de pago por ID
     */
    public List<String[]> getById(int id) throws SQLException {
        return dMetodoPago.getById(id);
    }
    
    /**
     * Guarda un nuevo método de pago
     */
    public List<String[]> save(String nombre, String descripcion) throws SQLException {
        return dMetodoPago.save(nombre, descripcion);
    }
    
    /**
     * Actualiza un método de pago existente
     */
    public List<String[]> update(int id, String nombre, String descripcion) throws SQLException {
        return dMetodoPago.update(id, nombre, descripcion);
    }
    
    /**
     * Elimina un método de pago por ID
     */
    public boolean delete(int id) throws SQLException {
        return dMetodoPago.delete(id);
    }
    
    /**
     * Obtiene el nombre del método de pago por ID
     */
    public String getNombreById(int id) throws SQLException {
        return dMetodoPago.getNombreById(id);
    }
    
    public void disconnect() {
        dMetodoPago.disconnect();
    }
} 