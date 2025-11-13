package data;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import postgresConecction.DBConnection;
import postgresConecction.SqlConnection;

/**
 * Clase de acceso a datos para la tabla detalle_carrito
 */
public class DDetalleCarrito {
    
    public static final String[] HEADERS = {"id", "carrito_id", "producto_almacen_id", "cantidad", "precio_unitario", "subtotal", "producto_nombre", "producto_descripcion", "stock", "precio_venta"};
    
    private final SqlConnection connection;
    
    public DDetalleCarrito() {
        this.connection = new SqlConnection(DBConnection.database, DBConnection.server, DBConnection.port, DBConnection.user, DBConnection.password);
    }
    
    /**
     * Obtiene todos los detalles de un carrito con información del producto
     */
    public List<String[]> getByCarritoId(int carritoId) throws SQLException {
        List<String[]> detalles = new ArrayList<>();
        String query = "SELECT dc.id, dc.carrito_id, dc.producto_almacen_id, dc.cantidad, dc.precio_unitario, dc.subtotal, " +
                      "p.nombre as producto_nombre, p.descripcion as producto_descripcion, " +
                      "pa.stock, p.precio_venta " +
                      "FROM detalle_carrito dc " +
                      "INNER JOIN producto_almacen pa ON dc.producto_almacen_id = pa.id " +
                      "INNER JOIN producto p ON pa.producto_id = p.id " +
                      "WHERE dc.carrito_id = ? " +
                      "ORDER BY dc.id DESC";
        
        try (PreparedStatement ps = connection.connect().prepareStatement(query)) {
            
            ps.setInt(1, carritoId);
            
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    detalles.add(new String[]{
                        String.valueOf(rs.getInt("id")),
                        String.valueOf(rs.getInt("carrito_id")),
                        String.valueOf(rs.getInt("producto_almacen_id")),
                        String.valueOf(rs.getInt("cantidad")),
                        String.valueOf(rs.getBigDecimal("precio_unitario")),
                        String.valueOf(rs.getBigDecimal("subtotal")),
                        rs.getString("producto_nombre"),
                        rs.getString("producto_descripcion"),
                        String.valueOf(rs.getInt("stock")),
                        String.valueOf(rs.getBigDecimal("precio_venta"))
                    });
                }
            }
        }
        
        return detalles;
    }
    
    /**
     * Obtiene un detalle por ID con información del producto
     */
    public List<String[]> getById(int id) throws SQLException {
        List<String[]> detalles = new ArrayList<>();
        String query = "SELECT dc.id, dc.carrito_id, dc.producto_almacen_id, dc.cantidad, dc.precio_unitario, dc.subtotal, " +
                      "p.nombre as producto_nombre, p.descripcion as producto_descripcion, " +
                      "pa.stock, p.precio_venta " +
                      "FROM detalle_carrito dc " +
                      "INNER JOIN producto_almacen pa ON dc.producto_almacen_id = pa.id " +
                      "INNER JOIN producto p ON pa.producto_id = p.id " +
                      "WHERE dc.id = ?";
        
        try (PreparedStatement ps = connection.connect().prepareStatement(query)) {
            
            ps.setInt(1, id);
            
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    detalles.add(new String[]{
                        String.valueOf(rs.getInt("id")),
                        String.valueOf(rs.getInt("carrito_id")),
                        String.valueOf(rs.getInt("producto_almacen_id")),
                        String.valueOf(rs.getInt("cantidad")),
                        String.valueOf(rs.getBigDecimal("precio_unitario")),
                        String.valueOf(rs.getBigDecimal("subtotal")),
                        rs.getString("producto_nombre"),
                        rs.getString("producto_descripcion"),
                        String.valueOf(rs.getInt("stock")),
                        String.valueOf(rs.getBigDecimal("precio_venta"))
                    });
                }
            }
        }
        
        return detalles;
    }
    
    /**
     * Guarda un nuevo detalle de carrito
     */
    public List<String[]> save(int carritoId, int productoAlmacenId, int cantidad, double precioUnitario) throws SQLException {
        List<String[]> detalles = new ArrayList<>();
        double subtotal = cantidad * precioUnitario;
        
        String query = "INSERT INTO detalle_carrito (carrito_id, producto_almacen_id, cantidad, precio_unitario, subtotal) " +
                      "VALUES (?, ?, ?, ?, ?) " +
                      "ON CONFLICT (carrito_id, producto_almacen_id) " +
                      "DO UPDATE SET cantidad = detalle_carrito.cantidad + EXCLUDED.cantidad, " +
                      "subtotal = (detalle_carrito.cantidad + EXCLUDED.cantidad) * detalle_carrito.precio_unitario " +
                      "RETURNING id, carrito_id, producto_almacen_id, cantidad, precio_unitario, subtotal";
        
        try (PreparedStatement ps = connection.connect().prepareStatement(query)) {
            
            ps.setInt(1, carritoId);
            ps.setInt(2, productoAlmacenId);
            ps.setInt(3, cantidad);
            ps.setBigDecimal(4, java.math.BigDecimal.valueOf(precioUnitario));
            ps.setBigDecimal(5, java.math.BigDecimal.valueOf(subtotal));
            
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    detalles.add(new String[]{
                        String.valueOf(rs.getInt("id")),
                        String.valueOf(rs.getInt("carrito_id")),
                        String.valueOf(rs.getInt("producto_almacen_id")),
                        String.valueOf(rs.getInt("cantidad")),
                        String.valueOf(rs.getBigDecimal("precio_unitario")),
                        String.valueOf(rs.getBigDecimal("subtotal"))
                    });
                }
            }
        }
        
        return detalles;
    }
    
    /**
     * Actualiza la cantidad de un detalle de carrito
     */
    public List<String[]> updateCantidad(int id, int cantidad) throws SQLException {
        List<String[]> detalles = new ArrayList<>();
        
        // Primero obtener el precio_unitario actual
        String getQuery = "SELECT precio_unitario FROM detalle_carrito WHERE id = ?";
        double precioUnitario = 0.0;
        
        try (PreparedStatement ps = connection.connect().prepareStatement(getQuery)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    precioUnitario = rs.getBigDecimal("precio_unitario").doubleValue();
                }
            }
        }
        
        // Calcular el subtotal manualmente
        double subtotal = cantidad * precioUnitario;
        
        System.out.println("=== DEBUG UPDATE CANTIDAD ===");
        System.out.println("ID: " + id);
        System.out.println("Cantidad nueva: " + cantidad);
        System.out.println("Precio unitario: " + precioUnitario);
        System.out.println("Subtotal calculado: " + subtotal);
        
        String query = "UPDATE detalle_carrito SET cantidad = ?, subtotal = ? " +
                      "WHERE id = ? RETURNING id, carrito_id, producto_almacen_id, cantidad, precio_unitario, subtotal";
        
        try (PreparedStatement ps = connection.connect().prepareStatement(query)) {
            
            ps.setInt(1, cantidad);
            ps.setBigDecimal(2, java.math.BigDecimal.valueOf(subtotal));
            ps.setInt(3, id);
            
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    detalles.add(new String[]{
                        String.valueOf(rs.getInt("id")),
                        String.valueOf(rs.getInt("carrito_id")),
                        String.valueOf(rs.getInt("producto_almacen_id")),
                        String.valueOf(rs.getInt("cantidad")),
                        String.valueOf(rs.getBigDecimal("precio_unitario")),
                        String.valueOf(rs.getBigDecimal("subtotal"))
                    });
                    
                    System.out.println("Subtotal actualizado en BD: " + rs.getBigDecimal("subtotal"));
                }
            }
        }
        
        return detalles;
    }
    
    /**
     * Elimina un detalle de carrito por ID
     */
    public boolean delete(int id) throws SQLException {
        String query = "DELETE FROM detalle_carrito WHERE id = ?";
        
        try (PreparedStatement ps = connection.connect().prepareStatement(query)) {
            
            ps.setInt(1, id);
            int rowsAffected = ps.executeUpdate();
            return rowsAffected > 0;
        }
    }
    
    /**
     * Elimina todos los detalles de un carrito
     */
    public boolean deleteByCarritoId(int carritoId) throws SQLException {
        String query = "DELETE FROM detalle_carrito WHERE carrito_id = ?";
        
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
        String query = "SELECT COALESCE(SUM(subtotal), 0) as total FROM detalle_carrito WHERE carrito_id = ?";
        
        try (PreparedStatement ps = connection.connect().prepareStatement(query)) {
            
            ps.setInt(1, carritoId);
            
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getBigDecimal("total").doubleValue();
                }
            }
        }
        
        return 0.0;
    }
    
    /**
     * Obtiene información del producto (producto_almacen_id y precio) por producto_id
     */
    public List<String[]> getProductoInfo(int productoId) throws SQLException {
        List<String[]> productoInfo = new ArrayList<>();
        String query = "SELECT pa.id as producto_almacen_id, p.precio_venta " +
                      "FROM producto_almacen pa " +
                      "INNER JOIN producto p ON pa.producto_id = p.id " +
                      "WHERE p.id = ? " +
                      "LIMIT 1";
        
        try (PreparedStatement ps = connection.connect().prepareStatement(query)) {
            
            ps.setInt(1, productoId);
            
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    productoInfo.add(new String[]{
                        String.valueOf(rs.getInt("producto_almacen_id")),
                        String.valueOf(rs.getBigDecimal("precio_venta"))
                    });
                }
            }
        }
        
        return productoInfo;
    }
    
    /**
     * Verifica si existe un detalle por ID
     */
    public boolean existsById(int id) throws SQLException {
        String query = "SELECT COUNT(*) FROM detalle_carrito WHERE id = ?";
        
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