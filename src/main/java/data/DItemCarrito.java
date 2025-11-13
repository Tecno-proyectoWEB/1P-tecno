package data;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import postgresConecction.DBConnection;
import postgresConecction.SqlConnection;

/**
 * Clase de acceso a datos para la tabla item_carrito
 */
public class DItemCarrito {
    
    public static final String[] HEADERS = {"id", "carrito_id", "producto_id", "cantidad", "precio_unitario", "subtotal", "producto_nombre", "producto_descripcion", "stock"};
    
    private final SqlConnection connection;
    
    public DItemCarrito() {
        this.connection = new SqlConnection(DBConnection.database, DBConnection.server, DBConnection.port, DBConnection.user, DBConnection.password);
    }
    
    /**
     * Obtiene todos los items de un carrito con información del producto
     */
    public List<String[]> getByCarritoId(int carritoId) throws SQLException {
        List<String[]> items = new ArrayList<>();
        String query = "SELECT ic.id, ic.carrito_id, ic.producto_id, ic.cantidad, " +
                      "p.nombre as producto_nombre, p.descripcion as producto_descripcion, " +
                      "p.precio_unitario, p.stock, " +
                      "(ic.cantidad * p.precio_unitario) as subtotal " +
                      "FROM item_carrito ic " +
                      "INNER JOIN producto p ON ic.producto_id = p.id " +
                      "WHERE ic.carrito_id = ? " +
                      "ORDER BY ic.id DESC";
        
        try (PreparedStatement ps = connection.connect().prepareStatement(query)) {
            
            ps.setInt(1, carritoId);
            
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    items.add(new String[]{
                        String.valueOf(rs.getInt("id")),
                        String.valueOf(rs.getInt("carrito_id")),
                        String.valueOf(rs.getInt("producto_id")),
                        String.valueOf(rs.getInt("cantidad")),
                        String.valueOf(rs.getDouble("precio_unitario")),
                        String.valueOf(rs.getDouble("subtotal")),
                        rs.getString("producto_nombre"),
                        rs.getString("producto_descripcion"),
                        String.valueOf(rs.getInt("stock"))
                    });
                }
            }
        }
        
        return items;
    }
    
    /**
     * Obtiene un item por ID con información del producto
     */
    public List<String[]> getById(int id) throws SQLException {
        List<String[]> items = new ArrayList<>();
        String query = "SELECT ic.id, ic.carrito_id, ic.producto_id, ic.cantidad, " +
                      "p.nombre as producto_nombre, p.descripcion as producto_descripcion, " +
                      "p.precio_unitario, p.stock, " +
                      "(ic.cantidad * p.precio_unitario) as subtotal " +
                      "FROM item_carrito ic " +
                      "INNER JOIN producto p ON ic.producto_id = p.id " +
                      "WHERE ic.id = ?";
        
        try (PreparedStatement ps = connection.connect().prepareStatement(query)) {
            
            ps.setInt(1, id);
            
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    items.add(new String[]{
                        String.valueOf(rs.getInt("id")),
                        String.valueOf(rs.getInt("carrito_id")),
                        String.valueOf(rs.getInt("producto_id")),
                        String.valueOf(rs.getInt("cantidad")),
                        String.valueOf(rs.getDouble("precio_unitario")),
                        String.valueOf(rs.getDouble("subtotal")),
                        rs.getString("producto_nombre"),
                        rs.getString("producto_descripcion"),
                        String.valueOf(rs.getInt("stock"))
                    });
                }
            }
        }
        
        return items;
    }
    
    /**
     * Guarda un nuevo item en el carrito
     */
    public List<String[]> save(int carritoId, int productoId, int cantidad) throws SQLException {
        List<String[]> items = new ArrayList<>();
        
        String query = "INSERT INTO item_carrito (carrito_id, producto_id, cantidad) " +
                      "VALUES (?, ?, ?) " +
                      "ON CONFLICT (carrito_id, producto_id) " +
                      "DO UPDATE SET cantidad = item_carrito.cantidad + EXCLUDED.cantidad " +
                      "RETURNING id, carrito_id, producto_id, cantidad";
        
        try (PreparedStatement ps = connection.connect().prepareStatement(query)) {
            
            ps.setInt(1, carritoId);
            ps.setInt(2, productoId);
            ps.setInt(3, cantidad);
            
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    // Obtener el item completo con información del producto
                    return getById(rs.getInt("id"));
                }
            }
        }
        
        return items;
    }
    
    /**
     * Actualiza la cantidad de un item del carrito
     */
    public List<String[]> updateCantidad(int id, int cantidad) throws SQLException {
        List<String[]> items = new ArrayList<>();
        
        String query = "UPDATE item_carrito SET cantidad = ? " +
                      "WHERE id = ? RETURNING id, carrito_id, producto_id, cantidad";
        
        try (PreparedStatement ps = connection.connect().prepareStatement(query)) {
            
            ps.setInt(1, cantidad);
            ps.setInt(2, id);
            
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    // Obtener el item completo con información del producto
                    return getById(rs.getInt("id"));
                }
            }
        }
        
        return items;
    }
    
    /**
     * Elimina un item del carrito por ID
     */
    public boolean delete(int id) throws SQLException {
        String query = "DELETE FROM item_carrito WHERE id = ?";
        
        try (PreparedStatement ps = connection.connect().prepareStatement(query)) {
            
            ps.setInt(1, id);
            int rowsAffected = ps.executeUpdate();
            return rowsAffected > 0;
        }
    }
    
    /**
     * Elimina todos los items de un carrito
     */
    public boolean deleteByCarritoId(int carritoId) throws SQLException {
        String query = "DELETE FROM item_carrito WHERE carrito_id = ?";
        
        try (PreparedStatement ps = connection.connect().prepareStatement(query)) {
            
            ps.setInt(1, carritoId);
            int rowsAffected = ps.executeUpdate();
            return rowsAffected > 0;
        }
    }
    
    /**
     * Calcula el total de un carrito
     */
    public double calcularTotalCarrito(int carritoId) throws SQLException {
        String query = "SELECT COALESCE(SUM(ic.cantidad * p.precio_unitario), 0) as total " +
                      "FROM item_carrito ic " +
                      "INNER JOIN producto p ON ic.producto_id = p.id " +
                      "WHERE ic.carrito_id = ?";
        
        try (PreparedStatement ps = connection.connect().prepareStatement(query)) {
            
            ps.setInt(1, carritoId);
            
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getDouble("total");
                }
            }
        }
        
        return 0.0;
    }
    
    /**
     * Verifica si existe un item por ID
     */
    public boolean existsById(int id) throws SQLException {
        String query = "SELECT COUNT(*) FROM item_carrito WHERE id = ?";
        
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
