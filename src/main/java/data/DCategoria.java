package data;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import postgresConecction.DBConnection;
import postgresConecction.SqlConnection;

/**
 * Clase de acceso a datos para la tabla categoria
 */
public class DCategoria {
    
    public static final String[] HEADERS = {"id", "nombre", "descripcion", "activo", "subcategoria_id"};
    
    private final SqlConnection connection;
    
    public DCategoria() {
        this.connection = new SqlConnection(DBConnection.database, DBConnection.server, DBConnection.port, DBConnection.user, DBConnection.password);
    }
    
    /**
     * Obtiene todas las categorías
     */
    public List<String[]> getAll() throws SQLException {
        List<String[]> categorias = new ArrayList<>();
        String query = "SELECT id, nombre, descripcion, activo, subcategoria_id FROM categoria ORDER BY nombre";
        
        try (PreparedStatement ps = connection.connect().prepareStatement(query);
             ResultSet rs = ps.executeQuery()) {
            
            while (rs.next()) {
                categorias.add(new String[]{
                    String.valueOf(rs.getInt("id")),
                    rs.getString("nombre"),
                    rs.getString("descripcion"),
                    String.valueOf(rs.getBoolean("activo")),
                    rs.getObject("subcategoria_id") != null ? String.valueOf(rs.getInt("subcategoria_id")) : "null"
                });
            }
        }
        
        return categorias;
    }
    
    /**
     * Obtiene una categoría por ID
     */
    public List<String[]> getById(int id) throws SQLException {
        List<String[]> categorias = new ArrayList<>();
        String query = "SELECT id, nombre, descripcion, activo, subcategoria_id FROM categoria WHERE id = ?";
        
        try (PreparedStatement ps = connection.connect().prepareStatement(query)) {
            
            ps.setInt(1, id);
            
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    categorias.add(new String[]{
                        String.valueOf(rs.getInt("id")),
                        rs.getString("nombre"),
                        rs.getString("descripcion"),
                        String.valueOf(rs.getBoolean("activo")),
                        rs.getObject("subcategoria_id") != null ? String.valueOf(rs.getInt("subcategoria_id")) : "null"
                    });
                }
            }
        }
        
        return categorias;
    }
    
    /**
     * Guarda una nueva categoría
     */
    public List<String[]> save(String nombre, String descripcion, boolean activo, Integer subcategoriaId) throws SQLException {
        List<String[]> categorias = new ArrayList<>();
        String query = "INSERT INTO categoria (nombre, descripcion, activo, subcategoria_id) VALUES (?, ?, ?, ?) RETURNING id, nombre, descripcion, activo, subcategoria_id";
        
        try (PreparedStatement ps = connection.connect().prepareStatement(query)) {
            
            ps.setString(1, nombre);
            ps.setString(2, descripcion);
            ps.setBoolean(3, activo);
            if (subcategoriaId != null) {
                ps.setInt(4, subcategoriaId);
            } else {
                ps.setNull(4, java.sql.Types.INTEGER);
            }
            
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    categorias.add(new String[]{
                        String.valueOf(rs.getInt("id")),
                        rs.getString("nombre"),
                        rs.getString("descripcion"),
                        String.valueOf(rs.getBoolean("activo")),
                        rs.getObject("subcategoria_id") != null ? String.valueOf(rs.getInt("subcategoria_id")) : "null"
                    });
                }
            }
        }
        
        return categorias;
    }
    
    /**
     * Actualiza una categoría existente
     */
    public List<String[]> update(int id, String nombre, String descripcion, boolean activo, Integer subcategoriaId) throws SQLException {
        List<String[]> categorias = new ArrayList<>();
        String query = "UPDATE categoria SET nombre = ?, descripcion = ?, activo = ?, subcategoria_id = ? WHERE id = ? RETURNING id, nombre, descripcion, activo, subcategoria_id";
        
        try (PreparedStatement ps = connection.connect().prepareStatement(query)) {
            
            ps.setString(1, nombre);
            ps.setString(2, descripcion);
            ps.setBoolean(3, activo);
            if (subcategoriaId != null) {
                ps.setInt(4, subcategoriaId);
            } else {
                ps.setNull(4, java.sql.Types.INTEGER);
            }
            ps.setInt(5, id);
            
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    categorias.add(new String[]{
                        String.valueOf(rs.getInt("id")),
                        rs.getString("nombre"),
                        rs.getString("descripcion"),
                        String.valueOf(rs.getBoolean("activo")),
                        rs.getObject("subcategoria_id") != null ? String.valueOf(rs.getInt("subcategoria_id")) : "null"
                    });
                }
            }
        }
        
        return categorias;
    }
    
    /**
     * Elimina una categoría por ID
     */
    public boolean delete(int id) throws SQLException {
        String query = "DELETE FROM categoria WHERE id = ?";
        
        try (PreparedStatement ps = connection.connect().prepareStatement(query)) {
            
            ps.setInt(1, id);
            int rowsAffected = ps.executeUpdate();
            return rowsAffected > 0;
        }
    }
    
    /**
     * Obtiene el nombre de una categoría por ID
     */
    public String getNombreById(int id) throws SQLException {
        String query = "SELECT nombre FROM categoria WHERE id = ?";
        
        try (PreparedStatement ps = connection.connect().prepareStatement(query)) {
            
            ps.setInt(1, id);
            
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("nombre");
                }
            }
        }
        
        return null;
    }
    
    public void disconnect() {
        connection.closeConnection();
    }
} 