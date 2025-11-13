package negocio;

import data.DCategoria;
import java.sql.SQLException;
import java.util.List;

/**
 * Clase de negocio para la gestión de categorías
 */
public class NCategoria {
    
    private final DCategoria dCategoria;
    
    public NCategoria() {
        this.dCategoria = new DCategoria();
    }
    
    /**
     * Obtiene todas las categorías
     */
    public List<String[]> getAll() throws SQLException {
        return dCategoria.getAll();
    }
    
    /**
     * Obtiene una categoría por ID
     */
    public List<String[]> getById(int id) throws SQLException {
        return dCategoria.getById(id);
    }
    
    /**
     * Guarda una nueva categoría
     */
    public List<String[]> save(String nombre, String descripcion, boolean activo, Integer subcategoriaId) throws SQLException {
        return dCategoria.save(nombre, descripcion, activo, subcategoriaId);
    }
    
    /**
     * Actualiza una categoría existente
     */
    public List<String[]> update(int id, String nombre, String descripcion, boolean activo, Integer subcategoriaId) throws SQLException {
        return dCategoria.update(id, nombre, descripcion, activo, subcategoriaId);
    }
    
    /**
     * Elimina una categoría por ID
     */
    public boolean delete(int id) throws SQLException {
        return dCategoria.delete(id);
    }
    
    /**
     * Obtiene el nombre de una categoría por ID
     */
    public String getNombreById(int id) throws SQLException {
        return dCategoria.getNombreById(id);
    }
    
    public void disconnect() {
        dCategoria.disconnect();
    }
} 