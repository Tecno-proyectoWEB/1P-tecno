package data;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import postgresConecction.DBConnection;
import postgresConecction.SqlConnection;

/**
 * Clase de acceso a datos para la tabla detalle_venta
 */
public class DDetalleVenta {
    
    public static final String[] HEADERS = {"id", "nota_venta_id", "producto_almacen_id", "cantidad", "total", "producto_nombre", "producto_descripcion", "precio_venta", "stock"};
    
    private final SqlConnection connection;
    
    public DDetalleVenta() {
        this.connection = new SqlConnection(DBConnection.database, DBConnection.server, DBConnection.port, DBConnection.user, DBConnection.password);
    }
    
    /**
     * Obtiene todos los detalles de venta con información del producto
     */
    public List<String[]> getAll() throws SQLException {
        List<String[]> detallesVenta = new ArrayList<>();
        String query = "SELECT dv.id, dv.nota_venta_id, dv.producto_almacen_id, dv.cantidad, dv.total, " +
                      "p.nombre as producto_nombre, p.descripcion as producto_descripcion, " +
                      "pa.precio_venta, pa.stock " +
                      "FROM detalle_venta dv " +
                      "INNER JOIN producto_almacen pa ON dv.producto_almacen_id = pa.id " +
                      "INNER JOIN producto p ON pa.producto_id = p.id " +
                      "ORDER BY dv.nota_venta_id, dv.id";
        
        try (PreparedStatement ps = connection.connect().prepareStatement(query);
             ResultSet rs = ps.executeQuery()) {
            
            while (rs.next()) {
                detallesVenta.add(new String[]{
                    String.valueOf(rs.getInt("id")),
                    String.valueOf(rs.getInt("nota_venta_id")),
                    String.valueOf(rs.getInt("producto_almacen_id")),
                    String.valueOf(rs.getInt("cantidad")),
                    String.valueOf(rs.getBigDecimal("total")),
                    rs.getString("producto_nombre"),
                    rs.getString("producto_descripcion"),
                    String.valueOf(rs.getBigDecimal("precio_venta")),
                    String.valueOf(rs.getInt("stock"))
                });
            }
        }
        
        return detallesVenta;
    }
    
    /**
     * Obtiene detalles de venta por ID de nota de venta
     */
    public List<String[]> getByNotaVentaId(int notaVentaId) throws SQLException {
        List<String[]> detallesVenta = new ArrayList<>();
        String query = "SELECT dv.id, dv.nota_venta_id, dv.producto_almacen_id, dv.cantidad, dv.total, " +
                      "p.nombre as producto_nombre, p.descripcion as producto_descripcion, " +
                      "pa.precio_venta, pa.stock " +
                      "FROM detalle_venta dv " +
                      "INNER JOIN producto_almacen pa ON dv.producto_almacen_id = pa.id " +
                      "INNER JOIN producto p ON pa.producto_id = p.id " +
                      "WHERE dv.nota_venta_id = ? " +
                      "ORDER BY dv.id";
        
        try (PreparedStatement ps = connection.connect().prepareStatement(query)) {
            
            ps.setInt(1, notaVentaId);
            
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    detallesVenta.add(new String[]{
                        String.valueOf(rs.getInt("id")),
                        String.valueOf(rs.getInt("nota_venta_id")),
                        String.valueOf(rs.getInt("producto_almacen_id")),
                        String.valueOf(rs.getInt("cantidad")),
                        String.valueOf(rs.getBigDecimal("total")),
                        rs.getString("producto_nombre"),
                        rs.getString("producto_descripcion"),
                        String.valueOf(rs.getBigDecimal("precio_venta")),
                        String.valueOf(rs.getInt("stock"))
                    });
                }
            }
        }
        
        return detallesVenta;
    }
    
    /**
     * Obtiene un detalle de venta por ID
     */
    public List<String[]> getById(int id) throws SQLException {
        List<String[]> detallesVenta = new ArrayList<>();
        String query = "SELECT dv.id, dv.nota_venta_id, dv.producto_almacen_id, dv.cantidad, dv.total, " +
                      "p.nombre as producto_nombre, p.descripcion as producto_descripcion, " +
                      "pa.precio_venta, pa.stock " +
                      "FROM detalle_venta dv " +
                      "INNER JOIN producto_almacen pa ON dv.producto_almacen_id = pa.id " +
                      "INNER JOIN producto p ON pa.producto_id = p.id " +
                      "WHERE dv.id = ?";
        
        try (PreparedStatement ps = connection.connect().prepareStatement(query)) {
            
            ps.setInt(1, id);
            
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    detallesVenta.add(new String[]{
                        String.valueOf(rs.getInt("id")),
                        String.valueOf(rs.getInt("nota_venta_id")),
                        String.valueOf(rs.getInt("producto_almacen_id")),
                        String.valueOf(rs.getInt("cantidad")),
                        String.valueOf(rs.getBigDecimal("total")),
                        rs.getString("producto_nombre"),
                        rs.getString("producto_descripcion"),
                        String.valueOf(rs.getBigDecimal("precio_venta")),
                        String.valueOf(rs.getInt("stock"))
                    });
                }
            }
        }
        
        return detallesVenta;
    }
    
    /**
     * Guarda un nuevo detalle de venta
     */
    public List<String[]> save(int notaVentaId, int productoAlmacenId, int cantidad, double total) throws SQLException {
        List<String[]> detallesVenta = new ArrayList<>();
        String query = "INSERT INTO detalle_venta (nota_venta_id, producto_almacen_id, cantidad, total) " +
                      "VALUES (?, ?, ?, ?) " +
                      "RETURNING id, nota_venta_id, producto_almacen_id, cantidad, total";
        
        try (PreparedStatement ps = connection.connect().prepareStatement(query)) {
            
            ps.setInt(1, notaVentaId);
            ps.setInt(2, productoAlmacenId);
            ps.setInt(3, cantidad);
            ps.setDouble(4, total);
            
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    detallesVenta.add(new String[]{
                        String.valueOf(rs.getInt("id")),
                        String.valueOf(rs.getInt("nota_venta_id")),
                        String.valueOf(rs.getInt("producto_almacen_id")),
                        String.valueOf(rs.getInt("cantidad")),
                        String.valueOf(rs.getBigDecimal("total"))
                    });
                }
            }
        }
        
        return detallesVenta;
    }
    
    /**
     * Actualiza un detalle de venta existente
     */
    public List<String[]> update(int id, int cantidad, double total) throws SQLException {
        List<String[]> detallesVenta = new ArrayList<>();
        String query = "UPDATE detalle_venta SET cantidad = ?, total = ? " +
                      "WHERE id = ? " +
                      "RETURNING id, nota_venta_id, producto_almacen_id, cantidad, total";
        
        try (PreparedStatement ps = connection.connect().prepareStatement(query)) {
            
            ps.setInt(1, cantidad);
            ps.setDouble(2, total);
            ps.setInt(3, id);
            
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    detallesVenta.add(new String[]{
                        String.valueOf(rs.getInt("id")),
                        String.valueOf(rs.getInt("nota_venta_id")),
                        String.valueOf(rs.getInt("producto_almacen_id")),
                        String.valueOf(rs.getInt("cantidad")),
                        String.valueOf(rs.getBigDecimal("total"))
                    });
                }
            }
        }
        
        return detallesVenta;
    }
    
    /**
     * Elimina un detalle de venta por ID
     */
    public boolean delete(int id) throws SQLException {
        String query = "DELETE FROM detalle_venta WHERE id = ?";
        
        try (PreparedStatement ps = connection.connect().prepareStatement(query)) {
            
            ps.setInt(1, id);
            int rowsAffected = ps.executeUpdate();
            return rowsAffected > 0;
        }
    }
    
    /**
     * Obtiene el total de ventas por producto
     */
    public List<String[]> getVentasPorProducto() throws SQLException {
        List<String[]> ventasProducto = new ArrayList<>();
        String query = "SELECT p.nombre as producto_nombre, " +
                      "COUNT(dv.id) as veces_vendido, " +
                      "SUM(dv.cantidad) as cantidad_total_vendida, " +
                      "COALESCE(SUM(dv.total), 0) as total_ventas " +
                      "FROM detalle_venta dv " +
                      "INNER JOIN producto_almacen pa ON dv.producto_almacen_id = pa.id " +
                      "INNER JOIN producto p ON pa.producto_id = p.id " +
                      "GROUP BY p.id, p.nombre " +
                      "ORDER BY total_ventas DESC";
        
        try (PreparedStatement ps = connection.connect().prepareStatement(query);
             ResultSet rs = ps.executeQuery()) {
            
            while (rs.next()) {
                ventasProducto.add(new String[]{
                    rs.getString("producto_nombre"),
                    String.valueOf(rs.getInt("veces_vendido")),
                    String.valueOf(rs.getInt("cantidad_total_vendida")),
                    String.valueOf(rs.getBigDecimal("total_ventas"))
                });
            }
        }
        
        return ventasProducto;
    }
    
    /**
     * Obtiene el total de una nota de venta
     */
    public double getTotalNotaVenta(int notaVentaId) throws SQLException {
        String query = "SELECT COALESCE(SUM(total), 0) as total_nota FROM detalle_venta WHERE nota_venta_id = ?";
        
        try (PreparedStatement ps = connection.connect().prepareStatement(query)) {
            
            ps.setInt(1, notaVentaId);
            
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getDouble("total_nota");
                }
            }
        }
        
        return 0.0;
    }
    
    public void disconnect() {
        // La conexión se cierra automáticamente con try-with-resources
    }
} 