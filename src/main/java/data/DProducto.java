package data;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import postgresConecction.DBConnection;
import postgresConecction.SqlConnection;

/**
 * Clase de acceso a datos para la tabla producto
 */
public class DProducto {
    
    public static final String[] HEADERS = {"id", "nombre", "precio_unitario", "imagen", "descripcion", "categoria", "stock", "stock_minimo", "tiempo"};
    
    private final SqlConnection connection;
    private final DCategoria dCategoria;
    
    public DProducto() {
        this.connection = new SqlConnection(DBConnection.database, DBConnection.server, DBConnection.port, DBConnection.user, DBConnection.password);
        this.dCategoria = new DCategoria();
    }
    
    /**
     * Obtiene todos los productos con nombre de categoría
     */
    public List<String[]> getAll() throws SQLException {
        List<String[]> productos = new ArrayList<>();
        String query = "SELECT p.id, p.nombre, p.precio_unitario, p.imagen, p.descripcion, p.categoria_id, p.stock, p.stock_minimo, p.tiempo " +
                      "FROM producto p ORDER BY p.nombre";
        
        try (PreparedStatement ps = connection.connect().prepareStatement(query);
             ResultSet rs = ps.executeQuery()) {
            
            while (rs.next()) {
                int categoriaId = rs.getInt("categoria_id");
                String nombreCategoria = dCategoria.getNombreById(categoriaId);
                
                productos.add(new String[]{
                    String.valueOf(rs.getInt("id")),
                    rs.getString("nombre"),
                    String.valueOf(rs.getBigDecimal("precio_unitario")),
                    rs.getString("imagen"),
                    rs.getString("descripcion"),
                    nombreCategoria != null ? nombreCategoria : "Sin categoría",
                    String.valueOf(rs.getInt("stock")),
                    rs.getObject("stock_minimo") != null ? String.valueOf(rs.getInt("stock_minimo")) : "0",
                    rs.getString("tiempo") != null ? rs.getString("tiempo") : ""
                });
            }
        }
        
        return productos;
    }
    
    /**
     * Obtiene un producto por ID con nombre de categoría
     */
    public List<String[]> getById(int id) throws SQLException {
        List<String[]> productos = new ArrayList<>();
        String query = "SELECT p.id, p.nombre, p.precio_unitario, p.imagen, p.descripcion, p.categoria_id, p.stock, p.stock_minimo, p.tiempo " +
                      "FROM producto p WHERE p.id = ?";
        
        try (PreparedStatement ps = connection.connect().prepareStatement(query)) {
            
            ps.setInt(1, id);
            
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    int categoriaId = rs.getInt("categoria_id");
                    String nombreCategoria = dCategoria.getNombreById(categoriaId);
                    
                    productos.add(new String[]{
                        String.valueOf(rs.getInt("id")),
                        rs.getString("nombre"),
                        String.valueOf(rs.getBigDecimal("precio_unitario")),
                        rs.getString("imagen"),
                        rs.getString("descripcion"),
                        nombreCategoria != null ? nombreCategoria : "Sin categoría",
                        String.valueOf(rs.getInt("stock")),
                        rs.getObject("stock_minimo") != null ? String.valueOf(rs.getInt("stock_minimo")) : "0",
                        rs.getString("tiempo") != null ? rs.getString("tiempo") : ""
                    });
                }
            }
        }
        
        return productos;
    }
    
    /**
     * Guarda un nuevo producto
     */
    public List<String[]> save(String nombre, double precioUnitario, String imagen, String descripcion, int categoriaId, int stock, Integer stockMinimo, String tiempo) throws SQLException {
        List<String[]> productos = new ArrayList<>();
        String query = "INSERT INTO producto (nombre, descripcion, stock, stock_minimo, imagen, tiempo, precio_unitario, categoria_id) " +
                      "VALUES (?, ?, ?, ?, ?, ?, ?, ?) RETURNING id, nombre, descripcion, stock, stock_minimo, imagen, tiempo, precio_unitario, categoria_id";
        
        try (PreparedStatement ps = connection.connect().prepareStatement(query)) {
            
            ps.setString(1, nombre);
            ps.setString(2, descripcion);
            ps.setInt(3, stock);
            if (stockMinimo != null) {
                ps.setInt(4, stockMinimo);
            } else {
                ps.setNull(4, java.sql.Types.INTEGER);
            }
            ps.setString(5, imagen);
            ps.setString(6, tiempo);
            ps.setDouble(7, precioUnitario);
            ps.setInt(8, categoriaId);

            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    String nombreCategoria = dCategoria.getNombreById(categoriaId);
                    
                    productos.add(new String[]{
                        String.valueOf(rs.getInt("id")),
                        rs.getString("nombre"),
                        String.valueOf(rs.getBigDecimal("precio_unitario")),
                        rs.getString("imagen"),
                        rs.getString("descripcion"),
                        nombreCategoria != null ? nombreCategoria : "Sin categoría",
                        String.valueOf(rs.getInt("stock")),
                        rs.getObject("stock_minimo") != null ? String.valueOf(rs.getInt("stock_minimo")) : "0",
                        rs.getString("tiempo") != null ? rs.getString("tiempo") : ""
                    });
                }
            }
        }
        
        return productos;
    }
    
    /**
     * Actualiza un producto existente
     */
    public List<String[]> update(int id, String nombre, double precioUnitario, String imagen, String descripcion, int categoriaId, int stock, Integer stockMinimo, String tiempo) throws SQLException {
        List<String[]> productos = new ArrayList<>();
        String query = "UPDATE producto SET nombre = ?, precio_unitario = ?, imagen = ?, descripcion = ?, categoria_id = ?, stock = ?, stock_minimo = ?, tiempo = ? " +
                      "WHERE id = ? RETURNING id, nombre, precio_unitario, imagen, descripcion, categoria_id, stock, stock_minimo, tiempo";
        
        try (PreparedStatement ps = connection.connect().prepareStatement(query)) {
            ps.setString(1, nombre);
            ps.setDouble(2, precioUnitario);
            ps.setString(3, imagen);
            ps.setString(4, descripcion);
            ps.setInt(5, categoriaId);
            ps.setInt(6, stock);
            if (stockMinimo != null) {
                ps.setInt(7, stockMinimo);
            } else {
                ps.setNull(7, java.sql.Types.INTEGER);
            }
            ps.setString(8, tiempo);
            ps.setInt(9, id);
            
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    String nombreCategoria = dCategoria.getNombreById(categoriaId);
                    
                    productos.add(new String[]{
                        String.valueOf(rs.getInt("id")),
                        rs.getString("nombre"),
                        String.valueOf(rs.getBigDecimal("precio_unitario")),
                        rs.getString("imagen"),
                        rs.getString("descripcion"),
                        nombreCategoria != null ? nombreCategoria : "Sin categoría",
                        String.valueOf(rs.getInt("stock")),
                        rs.getObject("stock_minimo") != null ? String.valueOf(rs.getInt("stock_minimo")) : "0",
                        rs.getString("tiempo") != null ? rs.getString("tiempo") : ""
                    });
                }
            }
        }
        
        return productos;
    }
    
    /**
     * Elimina un producto por ID
     */
    public boolean delete(int id) throws SQLException {
        String query = "DELETE FROM producto WHERE id = ?";
        
        try (PreparedStatement ps = connection.connect().prepareStatement(query)) {
            
            ps.setInt(1, id);
            int rowsAffected = ps.executeUpdate();
            return rowsAffected > 0;
        }
    }
    
    /**
     * Obtiene productos por categoría
     */
    public List<String[]> getByCategoria(int categoriaId) throws SQLException {
        List<String[]> productos = new ArrayList<>();
        String query = "SELECT p.id, p.nombre, p.precio_unitario, p.imagen, p.descripcion, p.categoria_id, p.stock, p.stock_minimo, p.tiempo " +
                      "FROM producto p WHERE p.categoria_id = ? ORDER BY p.nombre";
        
        try (PreparedStatement ps = connection.connect().prepareStatement(query)) {
            
            ps.setInt(1, categoriaId);
            
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    String nombreCategoria = dCategoria.getNombreById(categoriaId);
                    
                    productos.add(new String[]{
                        String.valueOf(rs.getInt("id")),
                        rs.getString("nombre"),
                        String.valueOf(rs.getBigDecimal("precio_unitario")),
                        rs.getString("imagen"),
                        rs.getString("descripcion"),
                        nombreCategoria != null ? nombreCategoria : "Sin categoría",
                        String.valueOf(rs.getInt("stock")),
                        rs.getObject("stock_minimo") != null ? String.valueOf(rs.getInt("stock_minimo")) : "0",
                        rs.getString("tiempo") != null ? rs.getString("tiempo") : ""
                    });
                }
            }
        }
        
        return productos;
    }

    /**
     * Actualiza el stock de un producto
     */
    public List<String[]> updateStock(int id, int newStock) throws SQLException {
        List<String[]> productos = new ArrayList<>();
        String query = "UPDATE producto SET stock = ? WHERE id = ? " +
                      "RETURNING id, nombre, precio_unitario, imagen, descripcion, categoria_id, stock, stock_minimo, tiempo";
        
        try (PreparedStatement ps = connection.connect().prepareStatement(query)) {
            ps.setInt(1, newStock);
            ps.setInt(2, id);
            
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    int categoriaId = rs.getInt("categoria_id");
                    String nombreCategoria = dCategoria.getNombreById(categoriaId);
                    
                    productos.add(new String[]{
                        String.valueOf(rs.getInt("id")),
                        rs.getString("nombre"),
                        String.valueOf(rs.getBigDecimal("precio_unitario")),
                        rs.getString("imagen"),
                        rs.getString("descripcion"),
                        nombreCategoria != null ? nombreCategoria : "Sin categoría",
                        String.valueOf(rs.getInt("stock")),
                        rs.getObject("stock_minimo") != null ? String.valueOf(rs.getInt("stock_minimo")) : "0",
                        rs.getString("tiempo") != null ? rs.getString("tiempo") : ""
                    });
                }
            }
        }
        return productos;
    }

    /**
     * Busca productos por término
     */
    public List<String[]> searchByTerm(String searchTerm) throws SQLException {
        List<String[]> productos = new ArrayList<>();
        String query = "SELECT p.id, p.nombre, p.precio_unitario, p.imagen, p.descripcion, p.categoria_id, p.stock, p.stock_minimo, p.tiempo " +
                      "FROM producto p WHERE LOWER(p.nombre) LIKE LOWER(?) OR LOWER(p.descripcion) LIKE LOWER(?) " +
                      "ORDER BY p.nombre";
        
        try (PreparedStatement ps = connection.connect().prepareStatement(query)) {
            String term = "%" + searchTerm + "%";
            ps.setString(1, term);
            ps.setString(2, term);
            
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    int categoriaId = rs.getInt("categoria_id");
                    String nombreCategoria = dCategoria.getNombreById(categoriaId);
                    
                    productos.add(new String[]{
                        String.valueOf(rs.getInt("id")),
                        rs.getString("nombre"),
                        String.valueOf(rs.getBigDecimal("precio_unitario")),
                        rs.getString("imagen"),
                        rs.getString("descripcion"),
                        nombreCategoria != null ? nombreCategoria : "Sin categoría",
                        String.valueOf(rs.getInt("stock")),
                        rs.getObject("stock_minimo") != null ? String.valueOf(rs.getInt("stock_minimo")) : "0",
                        rs.getString("tiempo") != null ? rs.getString("tiempo") : ""
                    });
                }
            }
        }
        return productos;
    }

    /**
     * Obtiene el stock actual de un producto
     */
    public int getStock(int id) throws SQLException {
        String query = "SELECT stock FROM producto WHERE id = ?";
        
        try (PreparedStatement ps = connection.connect().prepareStatement(query)) {
            ps.setInt(1, id);
            
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt("stock");
                }
            }
        }
        
        return 0;
    }
    
    public void disconnect() {
        connection.closeConnection();
        dCategoria.disconnect();
    }
}