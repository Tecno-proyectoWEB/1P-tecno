package data;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import postgresConecction.DBConnection;
import postgresConecction.SqlConnection;

/**
 * Clase de acceso a datos para la tabla promocion
 */
public class DPromocion {
    
    public static final String[] HEADERS = {"id", "nombre", "fecha_inicio", "fecha_fin", "descuento", "producto_id"};
    
    private final SqlConnection connection;
    
    public DPromocion() {
        this.connection = new SqlConnection(DBConnection.database, DBConnection.server, DBConnection.port, DBConnection.user, DBConnection.password);
    }
    
    /**
     * Obtiene todas las promociones con información del producto
     */
    public List<String[]> getAll() throws SQLException {
        List<String[]> promociones = new ArrayList<>();
        String query = "SELECT p.id, p.nombre, p.fecha_inicio, p.fecha_fin, p.descuento, p.producto_id, " +
                      "prod.nombre as producto_nombre, prod.precio_venta " +
                      "FROM promocion p " +
                      "LEFT JOIN producto prod ON p.producto_id = prod.id " +
                      "ORDER BY p.fecha_inicio DESC";
        
        try (PreparedStatement ps = connection.connect().prepareStatement(query);
             ResultSet rs = ps.executeQuery()) {
            
            while (rs.next()) {
                promociones.add(new String[]{
                    String.valueOf(rs.getInt("id")),
                    rs.getString("nombre"),
                    rs.getString("fecha_inicio"),
                    rs.getString("fecha_fin"),
                    rs.getString("descuento"),
                    rs.getString("producto_id"),
                    rs.getString("producto_nombre"),
                    rs.getString("precio_venta")
                });
            }
        }
        
        return promociones;
    }
    
    /**
     * Obtiene una promoción por ID con información del producto
     */
    public List<String[]> getById(int id) throws SQLException {
        List<String[]> promociones = new ArrayList<>();
        String query = "SELECT p.id, p.nombre, p.fecha_inicio, p.fecha_fin, p.descuento, p.producto_id, " +
                      "prod.nombre as producto_nombre, prod.precio_venta " +
                      "FROM promocion p " +
                      "LEFT JOIN producto prod ON p.producto_id = prod.id " +
                      "WHERE p.id = ?";
        
        try (PreparedStatement ps = connection.connect().prepareStatement(query)) {
            
            ps.setInt(1, id);
            
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    promociones.add(new String[]{
                        String.valueOf(rs.getInt("id")),
                        rs.getString("nombre"),
                        rs.getString("fecha_inicio"),
                        rs.getString("fecha_fin"),
                        rs.getString("descuento"),
                        rs.getString("producto_id"),
                        rs.getString("producto_nombre"),
                        rs.getString("precio_venta")
                    });
                }
            }
        }
        
        return promociones;
    }
    
    /**
     * Obtiene promociones por producto_id
     */
    public List<String[]> getByProductoId(int productoId) throws SQLException {
        List<String[]> promociones = new ArrayList<>();
        String query = "SELECT p.id, p.nombre, p.fecha_inicio, p.fecha_fin, p.descuento, p.producto_id, " +
                      "prod.nombre as producto_nombre, prod.precio_venta " +
                      "FROM promocion p " +
                      "LEFT JOIN producto prod ON p.producto_id = prod.id " +
                      "WHERE p.producto_id = ? " +
                      "ORDER BY p.fecha_inicio DESC";
        
        try (PreparedStatement ps = connection.connect().prepareStatement(query)) {
            
            ps.setInt(1, productoId);
            
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    promociones.add(new String[]{
                        String.valueOf(rs.getInt("id")),
                        rs.getString("nombre"),
                        rs.getString("fecha_inicio"),
                        rs.getString("fecha_fin"),
                        rs.getString("descuento"),
                        rs.getString("producto_id"),
                        rs.getString("producto_nombre"),
                        rs.getString("precio_venta")
                    });
                }
            }
        }
        
        return promociones;
    }
    
    /**
     * Obtiene promociones activas (fecha actual entre fecha_inicio y fecha_fin)
     */
    public List<String[]> getActivas() throws SQLException {
        List<String[]> promociones = new ArrayList<>();
        String query = "SELECT p.id, p.nombre, p.fecha_inicio, p.fecha_fin, p.descuento, p.producto_id, " +
                      "prod.nombre as producto_nombre, prod.precio_venta " +
                      "FROM promocion p " +
                      "LEFT JOIN producto prod ON p.producto_id = prod.id " +
                      "WHERE CURRENT_DATE BETWEEN p.fecha_inicio AND p.fecha_fin " +
                      "ORDER BY p.fecha_inicio DESC";
        
        try (PreparedStatement ps = connection.connect().prepareStatement(query);
             ResultSet rs = ps.executeQuery()) {
            
            while (rs.next()) {
                promociones.add(new String[]{
                    String.valueOf(rs.getInt("id")),
                    rs.getString("nombre"),
                    rs.getString("fecha_inicio"),
                    rs.getString("fecha_fin"),
                    rs.getString("descuento"),
                    rs.getString("producto_id"),
                    rs.getString("producto_nombre"),
                    rs.getString("precio_venta")
                });
            }
        }
        
        return promociones;
    }
    
    /**
     * Guarda una nueva promoción
     */
    public List<String[]> save(String nombre, String fechaInicio, String fechaFin, String descuento, Integer productoId) throws SQLException {
        List<String[]> promociones = new ArrayList<>();
        String query = "INSERT INTO promocion (nombre, fecha_inicio, fecha_fin, descuento, producto_id) VALUES (?, ?, ?, ?, ?) RETURNING id, nombre, fecha_inicio, fecha_fin, descuento, producto_id";
        
        try (PreparedStatement ps = connection.connect().prepareStatement(query)) {
            
            ps.setString(1, nombre);
            ps.setString(2, fechaInicio);
            ps.setString(3, fechaFin);
            ps.setString(4, descuento);
            if (productoId != null) {
                ps.setInt(5, productoId);
            } else {
                ps.setNull(5, Types.INTEGER);
            }
            
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    promociones.add(new String[]{
                        String.valueOf(rs.getInt("id")),
                        rs.getString("nombre"),
                        rs.getString("fecha_inicio"),
                        rs.getString("fecha_fin"),
                        rs.getString("descuento"),
                        rs.getString("producto_id")
                    });
                }
            }
        }
        
        return promociones;
    }
    
    /**
     * Actualiza una promoción existente
     */
    public List<String[]> update(int id, String nombre, String fechaInicio, String fechaFin, String descuento, Integer productoId) throws SQLException {
        List<String[]> promociones = new ArrayList<>();
        String query = "UPDATE promocion SET nombre = ?, fecha_inicio = ?, fecha_fin = ?, descuento = ?, producto_id = ? WHERE id = ? RETURNING id, nombre, fecha_inicio, fecha_fin, descuento, producto_id";
        
        try (PreparedStatement ps = connection.connect().prepareStatement(query)) {
            
            ps.setString(1, nombre);
            ps.setString(2, fechaInicio);
            ps.setString(3, fechaFin);
            ps.setString(4, descuento);
            if (productoId != null) {
                ps.setInt(5, productoId);
            } else {
                ps.setNull(5, Types.INTEGER);
            }
            ps.setInt(6, id);
            
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    promociones.add(new String[]{
                        String.valueOf(rs.getInt("id")),
                        rs.getString("nombre"),
                        rs.getString("fecha_inicio"),
                        rs.getString("fecha_fin"),
                        rs.getString("descuento"),
                        rs.getString("producto_id")
                    });
                }
            }
        }
        
        return promociones;
    }
    
    /**
     * Elimina una promoción por ID
     */
    public boolean delete(int id) throws SQLException {
        String query = "DELETE FROM promocion WHERE id = ?";
        
        try (PreparedStatement ps = connection.connect().prepareStatement(query)) {
            
            ps.setInt(1, id);
            int rowsAffected = ps.executeUpdate();
            return rowsAffected > 0;
        }
    }
    
    /**
     * Verifica si existe una promoción por ID
     */
    public boolean existsById(int id) throws SQLException {
        String query = "SELECT COUNT(*) FROM promocion WHERE id = ?";
        
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
