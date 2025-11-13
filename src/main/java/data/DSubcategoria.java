package data;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import postgresConecction.DBConnection;
import postgresConecction.SqlConnection;

/**
 * Clase de acceso a datos para la tabla subcategoria
 */
public class DSubcategoria {
    
    public static final String[] HEADERS = {"id", "nombre", "descripcion"};
    
    private final SqlConnection connection;
    
    public DSubcategoria() {
        this.connection = new SqlConnection(DBConnection.database, DBConnection.server, DBConnection.port, DBConnection.user, DBConnection.password);
    }
    
    /**
     * Obtiene todas las subcategorías
     */
    public List<String[]> getAll() throws SQLException {
        List<String[]> subcategorias = new ArrayList<>();
        String query = "SELECT id, nombre, descripcion FROM subcategoria ORDER BY nombre";
        
        try (PreparedStatement ps = connection.connect().prepareStatement(query);
             ResultSet rs = ps.executeQuery()) {
            
            while (rs.next()) {
                subcategorias.add(new String[]{
                    String.valueOf(rs.getInt("id")),
                    rs.getString("nombre"),
                    rs.getString("descripcion")
                });
            }
        }
        
        return subcategorias;
    }
    
    /**
     * Obtiene una subcategoría por ID
     */
    public List<String[]> getById(int id) throws SQLException {
        List<String[]> subcategorias = new ArrayList<>();
        String query = "SELECT id, nombre, descripcion FROM subcategoria WHERE id = ?";
        
        try (PreparedStatement ps = connection.connect().prepareStatement(query)) {
            
            ps.setInt(1, id);
            
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    subcategorias.add(new String[]{
                        String.valueOf(rs.getInt("id")),
                        rs.getString("nombre"),
                        rs.getString("descripcion")
                    });
                }
            }
        }
        
        return subcategorias;
    }
    
    /**
     * Guarda una nueva subcategoría
     */
    public List<String[]> save(String nombre, String descripcion) throws SQLException {
        List<String[]> subcategorias = new ArrayList<>();
        String query = "INSERT INTO subcategoria (nombre, descripcion) VALUES (?, ?) RETURNING id, nombre, descripcion";
        
        try (PreparedStatement ps = connection.connect().prepareStatement(query)) {
            
            ps.setString(1, nombre);
            ps.setString(2, descripcion);
            
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    subcategorias.add(new String[]{
                        String.valueOf(rs.getInt("id")),
                        rs.getString("nombre"),
                        rs.getString("descripcion")
                    });
                }
            }
        }
        
        return subcategorias;
    }
    
    /**
     * Actualiza una subcategoría existente
     */
    public List<String[]> update(int id, String nombre, String descripcion) throws SQLException {
        List<String[]> subcategorias = new ArrayList<>();
        String query = "UPDATE subcategoria SET nombre = ?, descripcion = ? WHERE id = ? RETURNING id, nombre, descripcion";
        
        try (PreparedStatement ps = connection.connect().prepareStatement(query)) {
            
            ps.setString(1, nombre);
            ps.setString(2, descripcion);
            ps.setInt(3, id);
            
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    subcategorias.add(new String[]{
                        String.valueOf(rs.getInt("id")),
                        rs.getString("nombre"),
                        rs.getString("descripcion")
                    });
                }
            }
        }
        
        return subcategorias;
    }
    
    /**
     * Elimina una subcategoría por ID
     */
    public boolean delete(int id) throws SQLException {
        String query = "DELETE FROM subcategoria WHERE id = ?";
        
        try (PreparedStatement ps = connection.connect().prepareStatement(query)) {
            
            ps.setInt(1, id);
            int rowsAffected = ps.executeUpdate();
            return rowsAffected > 0;
        }
    }
    
    /**
     * Obtiene el nombre de una subcategoría por ID
     */
    public String getNombreById(int id) throws SQLException {
        String query = "SELECT nombre FROM subcategoria WHERE id = ?";
        
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