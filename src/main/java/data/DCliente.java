package data;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import postgresConecction.DBConnection;
import postgresConecction.SqlConnection;

/**
 * Clase de acceso a datos para la tabla cliente
 */
public class DCliente {
    
    public static final String[] HEADERS = {"id", "nit", "user_id", "nombre", "email", "celular", "genero"};
    
    private final SqlConnection connection;
    
    public DCliente() {
        this.connection = new SqlConnection(DBConnection.database, DBConnection.server, DBConnection.port, DBConnection.user, DBConnection.password);
    }
    
    /**
     * Obtiene todos los clientes con información del usuario
     */
    public List<String[]> getAll() throws SQLException {
        List<String[]> clientes = new ArrayList<>();
        String query = "SELECT c.id, c.nit, c.user_id, " +
                      "u.nombre, u.celular, u.email, u.genero " +
                      "FROM cliente c " +
                      "INNER JOIN \"user\" u ON c.user_id = u.id " +
                      "ORDER BY u.nombre";
        
        try (PreparedStatement ps = connection.connect().prepareStatement(query);
             ResultSet rs = ps.executeQuery()) {
            
            while (rs.next()) {
                clientes.add(new String[]{
                    String.valueOf(rs.getInt("id")),
                    rs.getString("nit"),
                    String.valueOf(rs.getInt("user_id")),
                    rs.getString("nombre"),
                    rs.getString("email"),
                    rs.getString("celular"),
                    rs.getString("genero")
                });
            }
        }
        
        return clientes;
    }
    
    /**
     * Obtiene un cliente por ID con información del usuario
     */
    public List<String[]> getById(int id) throws SQLException {
        List<String[]> clientes = new ArrayList<>();
        String query = "SELECT c.id, c.nit, c.user_id, " +
                      "u.nombre, u.celular, u.email, u.genero " +
                      "FROM cliente c " +
                      "INNER JOIN \"user\" u ON c.user_id = u.id " +
                      "WHERE c.id = ?";
        
        try (PreparedStatement ps = connection.connect().prepareStatement(query)) {
            
            ps.setInt(1, id);
            
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    clientes.add(new String[]{
                        String.valueOf(rs.getInt("id")),
                        rs.getString("nit"),
                        String.valueOf(rs.getInt("user_id")),
                        rs.getString("nombre"),
                        rs.getString("email"),
                        rs.getString("celular"),
                        rs.getString("genero")
                    });
                }
            }
        }
        
        return clientes;
    }
    
    /**
     * Obtiene un cliente por NIT con información del usuario
     */
    public List<String[]> getByNit(String nit) throws SQLException {
        List<String[]> clientes = new ArrayList<>();
        String query = "SELECT c.id, c.nit, c.user_id, " +
                      "u.nombre, u.celular, u.email, u.genero " +
                      "FROM cliente c " +
                      "INNER JOIN \"user\" u ON c.user_id = u.id " +
                      "WHERE c.nit = ?";
        
        try (PreparedStatement ps = connection.connect().prepareStatement(query)) {
            
            ps.setString(1, nit);
            
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    clientes.add(new String[]{
                        String.valueOf(rs.getInt("id")),
                        rs.getString("nit"),
                        String.valueOf(rs.getInt("user_id")),
                        rs.getString("nombre"),
                        rs.getString("email"),
                        rs.getString("celular"),
                        rs.getString("genero")
                    });
                }
            }
        }
        
        return clientes;
    }
    
    /**
     * Obtiene un cliente por user_id con información del usuario
     */
    public List<String[]> getByUserId(int userId) throws SQLException {
        List<String[]> clientes = new ArrayList<>();
        String query = "SELECT c.id, c.nit, c.user_id, " +
                      "u.nombre, u.celular, u.email, u.genero " +
                      "FROM cliente c " +
                      "INNER JOIN \"user\" u ON c.user_id = u.id " +
                      "WHERE c.user_id = ?";
        
        try (PreparedStatement ps = connection.connect().prepareStatement(query)) {
            
            ps.setInt(1, userId);
            
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    clientes.add(new String[]{
                        String.valueOf(rs.getInt("id")),
                        rs.getString("nit"),
                        String.valueOf(rs.getInt("user_id")),
                        rs.getString("nombre"),
                        rs.getString("email"),
                        rs.getString("celular"),
                        rs.getString("genero")
                    });
                }
            }
        }
        
        return clientes;
    }
    
    /**
     * Guarda un nuevo cliente
     */
    public List<String[]> save(String nit, int userId) throws SQLException {
        List<String[]> clientes = new ArrayList<>();
        String query = "INSERT INTO cliente (nit, user_id) VALUES (?, ?) RETURNING id, nit, user_id";
        
        try (PreparedStatement ps = connection.connect().prepareStatement(query)) {
            
            ps.setString(1, nit);
            ps.setInt(2, userId);
            
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    clientes.add(new String[]{
                        String.valueOf(rs.getInt("id")),
                        rs.getString("nit"),
                        String.valueOf(rs.getInt("user_id"))
                    });
                }
            }
        }
        
        return clientes;
    }
    
    /**
     * Actualiza un cliente existente
     */
    public List<String[]> update(int id, String nit) throws SQLException {
        List<String[]> clientes = new ArrayList<>();
        String query = "UPDATE cliente SET nit = ? WHERE id = ? RETURNING id, nit, user_id";
        
        try (PreparedStatement ps = connection.connect().prepareStatement(query)) {
            
            ps.setString(1, nit);
            ps.setInt(2, id);
            
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    clientes.add(new String[]{
                        String.valueOf(rs.getInt("id")),
                        rs.getString("nit"),
                        String.valueOf(rs.getInt("user_id"))
                    });
                }
            }
        }
        
        return clientes;
    }
    
    /**
     * Elimina un cliente por ID
     */
    public boolean delete(int id) throws SQLException {
        String query = "DELETE FROM cliente WHERE id = ?";
        
        try (PreparedStatement ps = connection.connect().prepareStatement(query)) {
            
            ps.setInt(1, id);
            int rowsAffected = ps.executeUpdate();
            return rowsAffected > 0;
        }
    }
    
    /**
     * Verifica si existe un cliente por NIT
     */
    public boolean existsByNit(String nit) throws SQLException {
        String query = "SELECT COUNT(*) FROM cliente WHERE nit = ?";
        
        try (PreparedStatement ps = connection.connect().prepareStatement(query)) {
            
            ps.setString(1, nit);
            
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        }
        
        return false;
    }
    
    /**
     * Verifica si existe un cliente por user_id
     */
    public boolean existsByUserId(int userId) throws SQLException {
        String query = "SELECT COUNT(*) FROM cliente WHERE user_id = ?";
        
        try (PreparedStatement ps = connection.connect().prepareStatement(query)) {
            
            ps.setInt(1, userId);
            
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        }
        
        return false;
    }
    
    public void disconnect() {
        connection.closeConnection();
    }
} 