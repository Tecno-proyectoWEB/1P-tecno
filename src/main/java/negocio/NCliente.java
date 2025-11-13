package negocio;

import data.DCliente;
import java.sql.SQLException;
import java.util.List;

/**
 * Clase de negocio para la gestión de clientes
 */
public class NCliente {
    
    private final DCliente dCliente;
    
    public NCliente() {
        this.dCliente = new DCliente();
    }
    
    /**
     * Obtiene todos los clientes con información del usuario
     */
    public List<String[]> getAll() throws SQLException {
        return dCliente.getAll();
    }
    
    /**
     * Obtiene un cliente por ID con información del usuario
     */
    public List<String[]> getById(int id) throws SQLException {
        return dCliente.getById(id);
    }
    
    /**
     * Obtiene un cliente por NIT con información del usuario
     */
    public List<String[]> getByNit(String nit) throws SQLException {
        return dCliente.getByNit(nit);
    }
    
    /**
     * Obtiene un cliente por user_id con información del usuario
     */
    public List<String[]> getByUserId(int userId) throws SQLException {
        return dCliente.getByUserId(userId);
    }
    
    /**
     * Guarda un nuevo cliente
     */
    public List<String[]> save(String nit, int userId) throws SQLException {
        return dCliente.save(nit, userId);
    }
    
    /**
     * Actualiza un cliente existente
     */
    public List<String[]> update(int id, String nit) throws SQLException {
        return dCliente.update(id, nit);
    }
    
    /**
     * Elimina un cliente por ID
     */
    public boolean delete(int id) throws SQLException {
        return dCliente.delete(id);
    }
    
    /**
     * Verifica si existe un cliente por NIT
     */
    public boolean existsByNit(String nit) throws SQLException {
        return dCliente.existsByNit(nit);
    }
    
    /**
     * Verifica si existe un cliente por user_id
     */
    public boolean existsByUserId(int userId) throws SQLException {
        return dCliente.existsByUserId(userId);
    }
    
    public void disconnect() {
        dCliente.disconnect();
    }
} 