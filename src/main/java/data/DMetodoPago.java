package data;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import postgresConecction.DBConnection;
import postgresConecction.SqlConnection;

/**
 * Clase de acceso a datos para la tabla metodo_pago
 */
public class DMetodoPago {
    
    public static final String[] HEADERS = {"id", "nombre", "descripcion"};
    
    private final SqlConnection connection;
    
    public DMetodoPago() {
        this.connection = new SqlConnection(DBConnection.database, DBConnection.server, DBConnection.port, DBConnection.user, DBConnection.password);
    }
    
    /**
     * Obtiene todos los métodos de pago
     */
    public List<String[]> getAll() throws SQLException {
        List<String[]> metodosPago = new ArrayList<>();
        String query = "SELECT id, nombre, descripcion FROM metodo_pago ORDER BY nombre";
        
        try (PreparedStatement ps = connection.connect().prepareStatement(query);
             ResultSet rs = ps.executeQuery()) {
            
            while (rs.next()) {
                metodosPago.add(new String[]{
                    String.valueOf(rs.getInt("id")),
                    rs.getString("nombre"),
                    rs.getString("descripcion")
                });
            }
        }
        
        return metodosPago;
    }
    
    /**
     * Obtiene un método de pago por ID
     */
    public List<String[]> getById(int id) throws SQLException {
        List<String[]> metodosPago = new ArrayList<>();
        String query = "SELECT id, nombre, descripcion FROM metodo_pago WHERE id = ?";
        
        try (PreparedStatement ps = connection.connect().prepareStatement(query)) {
            
            ps.setInt(1, id);
            
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    metodosPago.add(new String[]{
                        String.valueOf(rs.getInt("id")),
                        rs.getString("nombre"),
                        rs.getString("descripcion")
                    });
                }
            }
        }
        
        return metodosPago;
    }
    
    /**
     * Guarda un nuevo método de pago
     */
    public List<String[]> save(String nombre, String descripcion) throws SQLException {
        List<String[]> metodosPago = new ArrayList<>();
        String query = "INSERT INTO metodo_pago (nombre, descripcion) VALUES (?, ?) RETURNING id, nombre, descripcion";
        
        try (PreparedStatement ps = connection.connect().prepareStatement(query)) {
            
            ps.setString(1, nombre);
            ps.setString(2, descripcion);
            
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    metodosPago.add(new String[]{
                        String.valueOf(rs.getInt("id")),
                        rs.getString("nombre"),
                        rs.getString("descripcion")
                    });
                }
            }
        }
        
        return metodosPago;
    }
    
    /**
     * Actualiza un método de pago existente
     */
    public List<String[]> update(int id, String nombre, String descripcion) throws SQLException {
        List<String[]> metodosPago = new ArrayList<>();
        String query = "UPDATE metodo_pago SET nombre = ?, descripcion = ? WHERE id = ? RETURNING id, nombre, descripcion";
        
        try (PreparedStatement ps = connection.connect().prepareStatement(query)) {
            
            ps.setString(1, nombre);
            ps.setString(2, descripcion);
            ps.setInt(3, id);
            
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    metodosPago.add(new String[]{
                        String.valueOf(rs.getInt("id")),
                        rs.getString("nombre"),
                        rs.getString("descripcion")
                    });
                }
            }
        }
        
        return metodosPago;
    }
    
    /**
     * Elimina un método de pago por ID
     */
    public boolean delete(int id) throws SQLException {
        String query = "DELETE FROM metodo_pago WHERE id = ?";
        
        try (PreparedStatement ps = connection.connect().prepareStatement(query)) {
            
            ps.setInt(1, id);
            int rowsAffected = ps.executeUpdate();
            return rowsAffected > 0;
        }
    }
    
    /**
     * Obtiene el nombre del método de pago por ID
     */
    public String getNombreById(int id) throws SQLException {
        String query = "SELECT nombre FROM metodo_pago WHERE id = ?";
        
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