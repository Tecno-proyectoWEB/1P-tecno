package data;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import postgresConecction.DBConnection;
import postgresConecction.SqlConnection;

/**
 * Clase de acceso a datos para la tabla carrito
 */
public class DCarrito {
    
    public static final String[] HEADERS = {"id", "usuario_id", "fecha_creacion", "activo", "nombre", "email"};
    
    private final SqlConnection connection;
    
    public DCarrito() {
        this.connection = new SqlConnection(DBConnection.database, DBConnection.server, DBConnection.port, DBConnection.user, DBConnection.password);
    }
    
    /**
     * Obtiene el carrito activo del usuario, o crea uno nuevo si no existe
     * Solo permite carritos para usuarios con rol_id = 2 (clientes)
     */
    public List<String[]> getCarritoActivo(int usuarioId) throws SQLException {
        List<String[]> carritos = new ArrayList<>();
        
        // Buscar un carrito activo (solo para clientes)
        String query = "SELECT c.id, c.usuario_id, c.fecha_creacion, c.activo, " +
                      "u.nombre, u.email " +
                      "FROM carrito c " +
                      "INNER JOIN usuario u ON c.usuario_id = u.id " +
                      "WHERE c.usuario_id = ? AND c.activo = true AND u.rol_id = 2 " +
                      "ORDER BY c.id DESC " +
                      "LIMIT 1";
        
        try (PreparedStatement ps = connection.connect().prepareStatement(query)) {
            
            ps.setInt(1, usuarioId);
            
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    // Encontramos un carrito activo
                    carritos.add(new String[]{
                        String.valueOf(rs.getInt("id")),
                        String.valueOf(rs.getInt("usuario_id")),
                        rs.getString("fecha_creacion"),
                        String.valueOf(rs.getBoolean("activo")),
                        rs.getString("nombre"),
                        rs.getString("email")
                    });
                    return carritos;
                } else {
                    // No existe carrito activo, crear uno nuevo
                    return crearNuevoCarrito(usuarioId);
                }
            }
        }
    }
    
    /**
     * Obtiene un carrito por ID con información del usuario
     * Solo permite carritos de usuarios con rol_id = 2 (clientes)
     */
    public List<String[]> getById(int id) throws SQLException {
        List<String[]> carritos = new ArrayList<>();
        String query = "SELECT c.id, c.usuario_id, c.fecha_creacion, c.activo, " +
                      "u.nombre, u.email " +
                      "FROM carrito c " +
                      "INNER JOIN usuario u ON c.usuario_id = u.id " +
                      "WHERE c.id = ? AND u.rol_id = 2";
        
        try (PreparedStatement ps = connection.connect().prepareStatement(query)) {
            
            ps.setInt(1, id);
            
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    carritos.add(new String[]{
                        String.valueOf(rs.getInt("id")),
                        String.valueOf(rs.getInt("usuario_id")),
                        rs.getString("fecha_creacion"),
                        String.valueOf(rs.getBoolean("activo")),
                        rs.getString("nombre"),
                        rs.getString("email")
                    });
                }
            }
        }
        
        return carritos;
    }
    
    /**
     * Obtiene todos los carritos de un usuario
     * Solo permite carritos de usuarios con rol_id = 2 (clientes)
     */
    public List<String[]> getByUsuarioId(int usuarioId) throws SQLException {
        List<String[]> carritos = new ArrayList<>();
        String query = "SELECT c.id, c.usuario_id, c.fecha_creacion, c.activo, " +
                      "u.nombre, u.email " +
                      "FROM carrito c " +
                      "INNER JOIN usuario u ON c.usuario_id = u.id " +
                      "WHERE c.usuario_id = ? AND u.rol_id = 2 " +
                      "ORDER BY c.id DESC";
        
        try (PreparedStatement ps = connection.connect().prepareStatement(query)) {
            
            ps.setInt(1, usuarioId);
            
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    carritos.add(new String[]{
                        String.valueOf(rs.getInt("id")),
                        String.valueOf(rs.getInt("usuario_id")),
                        rs.getString("fecha_creacion"),
                        String.valueOf(rs.getBoolean("activo")),
                        rs.getString("nombre"),
                        rs.getString("email")
                    });
                }
            }
        }
        
        return carritos;
    }
    
    /**
     * Crea un nuevo carrito para el usuario
     * Valida que el usuario sea cliente (rol_id = 2) antes de crear el carrito
     */
    private List<String[]> crearNuevoCarrito(int usuarioId) throws SQLException {
        List<String[]> carritos = new ArrayList<>();
        
        // Primero validar que el usuario sea cliente
        String checkQuery = "SELECT id FROM usuario WHERE id = ? AND rol_id = 2";
        try (PreparedStatement checkPs = connection.connect().prepareStatement(checkQuery)) {
            checkPs.setInt(1, usuarioId);
            try (ResultSet checkRs = checkPs.executeQuery()) {
                if (!checkRs.next()) {
                    throw new SQLException("Solo los clientes pueden tener carritos de compra");
                }
            }
        }
        
        // Si es cliente, crear el carrito
        String query = "INSERT INTO carrito (usuario_id, fecha_creacion, activo) VALUES (?, CURRENT_TIMESTAMP, true) " +
                      "RETURNING id";
        
        try (PreparedStatement ps = connection.connect().prepareStatement(query)) {
            
            ps.setInt(1, usuarioId);
            
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    // Obtener información completa del carrito creado
                    return getById(rs.getInt("id"));
                }
            }
        }
        
        return carritos;
    }
    
    /**
     * Cambia el estado activo del carrito
     */
    public List<String[]> updateActivo(int carritoId, boolean activo) throws SQLException {
        List<String[]> carritos = new ArrayList<>();
        String query = "UPDATE carrito SET activo = ? WHERE id = ? RETURNING id, usuario_id, fecha_creacion, activo";
        
        try (PreparedStatement ps = connection.connect().prepareStatement(query)) {
            
            ps.setBoolean(1, activo);
            ps.setInt(2, carritoId);
            
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return getById(rs.getInt("id"));
                }
            }
        }
        
        return carritos;
    }
    
    /**
     * Elimina un carrito por ID
     */
    public boolean delete(int id) throws SQLException {
        String query = "DELETE FROM carrito WHERE id = ?";
        
        try (PreparedStatement ps = connection.connect().prepareStatement(query)) {
            
            ps.setInt(1, id);
            int rowsAffected = ps.executeUpdate();
            return rowsAffected > 0;
        }
    }
    
    /**
     * Verifica si existe un carrito por ID
     */
    public boolean existsById(int id) throws SQLException {
        String query = "SELECT COUNT(*) FROM carrito WHERE id = ?";
        
        try (PreparedStatement ps = connection.connect().prepareStatement(query)) {
            
            ps.setInt(1, id);
            
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