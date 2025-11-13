package data;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import postgresConecction.DBConnection;
import postgresConecction.SqlConnection;

/**
 * Clase de acceso a datos para la tabla pedido
 */
public class DPedido {
    
    public static final String[] HEADERS = {"id", "fecha", "descripcion", "importe_total", "importe_total_desc", "estado", "metodo_pago_id", "usuario_id", "metodo_pago", "usuario"};
    
    private final SqlConnection connection;
    private final DMetodoPago dMetodoPago;
    private final DUsuario dUsuario;
    
    public DPedido() {
        this.connection = new SqlConnection(DBConnection.database, DBConnection.server, DBConnection.port, DBConnection.user, DBConnection.password);
        this.dMetodoPago = new DMetodoPago();
        this.dUsuario = new DUsuario();
    }
    
    /**
     * Obtiene todos los pedidos con información del método de pago y usuario
     */
    public List<String[]> getAll() throws SQLException {
        List<String[]> pedidos = new ArrayList<>();
        String query = "SELECT p.id, p.fecha, p.descripcion, p.importe_total, p.importe_total_desc, " +
                      "p.estado, p.metodo_pago_id, p.usuario_id, " +
                      "mp.nombre as metodo_pago, u.nombre as usuario " +
                      "FROM pedido p " +
                      "INNER JOIN metodo_pago mp ON p.metodo_pago_id = mp.id " +
                      "INNER JOIN usuario u ON p.usuario_id = u.id " +
                      "ORDER BY p.fecha DESC";
        
        try (PreparedStatement ps = connection.connect().prepareStatement(query);
             ResultSet rs = ps.executeQuery()) {
            
            while (rs.next()) {
                pedidos.add(new String[]{
                    String.valueOf(rs.getInt("id")),
                    rs.getString("fecha"),
                    rs.getString("descripcion"),
                    String.valueOf(rs.getDouble("importe_total")),
                    String.valueOf(rs.getDouble("importe_total_desc")),
                    rs.getString("estado"),
                    String.valueOf(rs.getInt("metodo_pago_id")),
                    String.valueOf(rs.getInt("usuario_id")),
                    rs.getString("metodo_pago"),
                    rs.getString("usuario")
                });
            }
        }
        
        return pedidos;
    }
    
    /**
     * Obtiene un pedido por ID con información del método de pago y usuario
     */
    public List<String[]> getById(int id) throws SQLException {
        List<String[]> pedidos = new ArrayList<>();
        String query = "SELECT p.id, p.fecha, p.descripcion, p.importe_total, p.importe_total_desc, " +
                      "p.estado, p.metodo_pago_id, p.usuario_id, " +
                      "mp.nombre as metodo_pago, u.nombre as usuario " +
                      "FROM pedido p " +
                      "INNER JOIN metodo_pago mp ON p.metodo_pago_id = mp.id " +
                      "INNER JOIN usuario u ON p.usuario_id = u.id " +
                      "WHERE p.id = ?";
        
        try (PreparedStatement ps = connection.connect().prepareStatement(query)) {
            ps.setInt(1, id);
            
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    pedidos.add(new String[]{
                        String.valueOf(rs.getInt("id")),
                        rs.getString("fecha"),
                        rs.getString("descripcion"),
                        String.valueOf(rs.getDouble("importe_total")),
                        String.valueOf(rs.getDouble("importe_total_desc")),
                        rs.getBoolean("estado") ? "Activo" : "Inactivo",
                        String.valueOf(rs.getInt("metodo_pago_id")),
                        String.valueOf(rs.getInt("usuario_id")),
                        rs.getString("metodo_pago"),
                        rs.getString("usuario")
                    });
                }
            }
        }
        
        return pedidos;
    }
    
    /**
     * Obtiene pedidos por estado
     */
    public List<String[]> getByEstado(boolean estado) throws SQLException {
        List<String[]> pedidos = new ArrayList<>();
        String query = "SELECT p.id, p.fecha, p.descripcion, p.importe_total, p.importe_total_desc, " +
                      "p.estado, p.metodo_pago_id, p.usuario_id, " +
                      "mp.nombre as metodo_pago, u.nombre as usuario " +
                      "FROM pedido p " +
                      "INNER JOIN metodo_pago mp ON p.metodo_pago_id = mp.id " +
                      "INNER JOIN usuario u ON p.usuario_id = u.id " +
                      "WHERE p.estado = ? " +
                      "ORDER BY p.fecha DESC";
        
        try (PreparedStatement ps = connection.connect().prepareStatement(query)) {
            ps.setBoolean(1, estado);
            
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    pedidos.add(new String[]{
                        String.valueOf(rs.getInt("id")),
                        rs.getString("fecha"),
                        rs.getString("descripcion"),
                        String.valueOf(rs.getDouble("importe_total")),
                        String.valueOf(rs.getDouble("importe_total_desc")),
                        rs.getBoolean("estado") ? "Activo" : "Inactivo",
                        String.valueOf(rs.getInt("metodo_pago_id")),
                        String.valueOf(rs.getInt("usuario_id")),
                        rs.getString("metodo_pago"),
                        rs.getString("usuario")
                    });
                }
            }
        }
        
        return pedidos;
    }
    
    /**
     * Obtiene pedidos por email de usuario
     */
    public List<String[]> getByUsuarioEmail(String email) throws SQLException {
        List<String[]> pedidos = new ArrayList<>();
        String query = "SELECT p.id, p.fecha, p.descripcion, p.importe_total, p.importe_total_desc, " +
                      "p.estado, p.metodo_pago_id, p.usuario_id, " +
                      "mp.nombre as metodo_pago, u.nombre as usuario " +
                      "FROM pedido p " +
                      "INNER JOIN metodo_pago mp ON p.metodo_pago_id = mp.id " +
                      "INNER JOIN usuario u ON p.usuario_id = u.id " +
                      "WHERE u.email = ? " +
                      "ORDER BY p.fecha DESC";
        
        try (PreparedStatement ps = connection.connect().prepareStatement(query)) {
            ps.setString(1, email);
            
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    pedidos.add(new String[]{
                        String.valueOf(rs.getInt("id")),
                        rs.getString("fecha"),
                        rs.getString("descripcion"),
                        String.valueOf(rs.getDouble("importe_total")),
                        String.valueOf(rs.getDouble("importe_total_desc")),
                        rs.getBoolean("estado") ? "Activo" : "Inactivo",
                        String.valueOf(rs.getInt("metodo_pago_id")),
                        String.valueOf(rs.getInt("usuario_id")),
                        rs.getString("metodo_pago"),
                        rs.getString("usuario")
                    });
                }
            }
        }
        
        return pedidos;
    }
    
    /**
     * Guarda un nuevo pedido
     
    public List<String[]> save(String descripcion, double importeTotal, double importeTotalDesc, int metodoPagoId, int usuarioId) throws SQLException {
        List<String[]> pedidos = new ArrayList<>();
        String query = "INSERT INTO pedido (fecha, descripcion, importe_total, importe_total_desc, estado, metodo_pago_id, usuario_id) " +
                      "VALUES (CURRENT_TIMESTAMP, ?, ?, ?, true, ?, ?) " +
                      "RETURNING id, fecha, descripcion, importe_total, importe_total_desc, estado, metodo_pago_id, usuario_id";
        
        try (PreparedStatement ps = connection.connect().prepareStatement(query)) {
            ps.setString(1, descripcion);
            ps.setDouble(2, importeTotal);
            ps.setDouble(3, importeTotalDesc);
            ps.setInt(4, metodoPagoId);
            ps.setInt(5, usuarioId);
            
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    pedidos.add(new String[]{
                        String.valueOf(rs.getInt("id")),
                        rs.getString("fecha"),
                        rs.getString("descripcion"),
                        String.valueOf(rs.getDouble("importe_total")),
                        String.valueOf(rs.getDouble("importe_total_desc")),
                        rs.getBoolean("estado") ? "Activo" : "Inactivo",
                        String.valueOf(rs.getInt("metodo_pago_id")),
                        String.valueOf(rs.getInt("usuario_id"))
                    });
                }
            }
        }
        
        return pedidos;
    }
    */

    // En DPedido.java, modificar el método save:
public List<String[]> save(String descripcion, double importeTotal, double importeTotalDesc, 
                          int metodoPagoId, int usuarioId) throws SQLException {
    Connection conn = null;
    try {
        conn = connection.connect();
        conn.setAutoCommit(false);
        
        // Crear el pedido
        List<String[]> pedidos = new ArrayList<>();
        String query = "INSERT INTO pedido (fecha, descripcion, importe_total, importe_total_desc, estado, metodo_pago_id, usuario_id) " +
                      "VALUES (CURRENT_TIMESTAMP, ?, ?, ?, 'pendiente', ?, ?) " +
                      "RETURNING id, fecha, descripcion, importe_total, importe_total_desc, estado, metodo_pago_id, usuario_id";
        
        int pedidoId;
        try (PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setString(1, descripcion);
            ps.setDouble(2, importeTotal);
            ps.setDouble(3, importeTotalDesc);
            ps.setInt(4, metodoPagoId);
            ps.setInt(5, usuarioId);
            
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    pedidoId = rs.getInt("id");
                    pedidos.add(new String[]{
                        String.valueOf(pedidoId),
                        rs.getString("fecha"),
                        rs.getString("descripcion"),
                        String.valueOf(rs.getDouble("importe_total")),
                        String.valueOf(rs.getDouble("importe_total_desc")),
                        "Activo",
                        String.valueOf(rs.getInt("metodo_pago_id")),
                        String.valueOf(rs.getInt("usuario_id"))
                    });
                } else {
                    throw new SQLException("Error al crear el pedido");
                }
            }
        }
        
        // Registrar el pago inicial (50%)
        DPago dPago = new DPago();
        double montoInicial = importeTotalDesc / 2;
        dPago.save(pedidoId, montoInicial, "INICIAL", metodoPagoId);
        
        conn.commit();
        return pedidos;
        
    } catch (SQLException e) {
        if (conn != null) {
            try {
                conn.rollback();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
        throw e;
    } finally {
        if (conn != null) {
            try {
                conn.setAutoCommit(true);
                conn.close();
            } catch (SQLException ex) {
                ex.printStackTrace();
            }
        }
    }
}
    /**
     * Actualiza el estado de un pedido
     */
    public List<String[]> updateEstado(int id, boolean estado) throws SQLException {
        List<String[]> pedidos = new ArrayList<>();
        String query = "UPDATE pedido SET estado = ? WHERE id = ? " +
                      "RETURNING id, fecha, descripcion, importe_total, importe_total_desc, estado, metodo_pago_id, usuario_id";
        
        try (PreparedStatement ps = connection.connect().prepareStatement(query)) {
            ps.setBoolean(1, estado);
            ps.setInt(2, id);
            
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    pedidos.add(new String[]{
                        String.valueOf(rs.getInt("id")),
                        rs.getString("fecha"),
                        rs.getString("descripcion"),
                        String.valueOf(rs.getDouble("importe_total")),
                        String.valueOf(rs.getDouble("importe_total_desc")),
                        rs.getBoolean("estado") ? "Activo" : "Inactivo",
                        String.valueOf(rs.getInt("metodo_pago_id")),
                        String.valueOf(rs.getInt("usuario_id"))
                    });
                }
            }
        }
        
        return pedidos;
    }
    
    /**
     * Obtiene pedidos por usuario
     */
    public List<String[]> getByUsuario(int usuarioId) throws SQLException {
        List<String[]> pedidos = new ArrayList<>();
        String query = "SELECT p.id, p.fecha, p.descripcion, p.importe_total, p.importe_total_desc, " +
                      "p.estado, p.metodo_pago_id, p.usuario_id, " +
                      "mp.nombre as metodo_pago, u.nombre as usuario " +
                      "FROM pedido p " +
                      "INNER JOIN metodo_pago mp ON p.metodo_pago_id = mp.id " +
                      "INNER JOIN usuario u ON p.usuario_id = u.id " +
                      "WHERE p.usuario_id = ? " +
                      "ORDER BY p.fecha DESC";
        
        try (PreparedStatement ps = connection.connect().prepareStatement(query)) {
            ps.setInt(1, usuarioId);
            
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    pedidos.add(new String[]{
                        String.valueOf(rs.getInt("id")),
                        rs.getString("fecha"),
                        rs.getString("descripcion"),
                        String.valueOf(rs.getDouble("importe_total")),
                        String.valueOf(rs.getDouble("importe_total_desc")),
                        rs.getBoolean("estado") ? "Activo" : "Inactivo",
                        String.valueOf(rs.getInt("metodo_pago_id")),
                        String.valueOf(rs.getInt("usuario_id")),
                        rs.getString("metodo_pago"),
                        rs.getString("usuario")
                    });
                }
            }
        }
        
        return pedidos;
    }
    
    /**
     * Obtiene pedidos por método de pago
     */
    public List<String[]> getByMetodoPago(int metodoPagoId) throws SQLException {
        List<String[]> pedidos = new ArrayList<>();
        String query = "SELECT p.id, p.fecha, p.descripcion, p.importe_total, p.importe_total_desc, " +
                      "p.estado, p.metodo_pago_id, p.usuario_id, " +
                      "mp.nombre as metodo_pago, u.nombre as usuario " +
                      "FROM pedido p " +
                      "INNER JOIN metodo_pago mp ON p.metodo_pago_id = mp.id " +
                      "INNER JOIN usuario u ON p.usuario_id = u.id " +
                      "WHERE p.metodo_pago_id = ? " +
                      "ORDER BY p.fecha DESC";
        
        try (PreparedStatement ps = connection.connect().prepareStatement(query)) {
            ps.setInt(1, metodoPagoId);
            
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    pedidos.add(new String[]{
                        String.valueOf(rs.getInt("id")),
                        rs.getString("fecha"),
                        rs.getString("descripcion"),
                        String.valueOf(rs.getDouble("importe_total")),
                        String.valueOf(rs.getDouble("importe_total_desc")),
                        rs.getBoolean("estado") ? "Activo" : "Inactivo",
                        String.valueOf(rs.getInt("metodo_pago_id")),
                        String.valueOf(rs.getInt("usuario_id")),
                        rs.getString("metodo_pago"),
                        rs.getString("usuario")
                    });
                }
            }
        }
        
        return pedidos;
    }
    
    /**
     * Elimina un pedido por ID
     */
    public boolean delete(int id) throws SQLException {
        String query = "DELETE FROM pedido WHERE id = ?";
        
        try (PreparedStatement ps = connection.connect().prepareStatement(query)) {
            
            ps.setInt(1, id);
            int rowsAffected = ps.executeUpdate();
            return rowsAffected > 0;
        }
    }
    
    /**
     * Obtiene estadísticas de pedidos
     */
    public List<String[]> getEstadisticasPedidos() throws SQLException {
        List<String[]> estadisticas = new ArrayList<>();
        String query = "SELECT " +
                      "COUNT(*) as total_pedidos, " +
                      "COALESCE(SUM(importe_total), 0) as monto_total, " +
                      "COALESCE(AVG(importe_total), 0) as promedio_pedido, " +
                      "COALESCE(SUM(importe_total_desc), 0) as monto_total_descuento, " +
                      "COUNT(CASE WHEN estado = true THEN 1 END) as pedidos_activos, " +
                      "COUNT(CASE WHEN estado = false THEN 1 END) as pedidos_inactivos " +
                      "FROM pedido";
        
        try (PreparedStatement ps = connection.connect().prepareStatement(query);
             ResultSet rs = ps.executeQuery()) {
            
            if (rs.next()) {
                estadisticas.add(new String[]{
                    String.valueOf(rs.getInt("total_pedidos")),
                    String.valueOf(rs.getDouble("monto_total")),
                    String.valueOf(rs.getDouble("promedio_pedido")),
                    String.valueOf(rs.getDouble("monto_total_descuento")),
                    String.valueOf(rs.getInt("pedidos_activos")),
                    String.valueOf(rs.getInt("pedidos_inactivos"))
                });
            }
        }
        
        return estadisticas;
    }
    
    /**
     * Obtiene pedidos activos
     */
    public List<String[]> getPedidosActivos() throws SQLException {
        return getByEstado(true);
    }
    
    /**
     * Obtiene pedidos inactivos
     */
    public List<String[]> getPedidosInactivos() throws SQLException {
        return getByEstado(false);
    }
    
    /**
     * Obtiene pedidos por email de usuario
     */
    public List<String[]> getByClienteEmail(String email) throws SQLException {
        List<String[]> pedidos = new ArrayList<>();
        String query = "SELECT p.id, p.fecha, p.descripcion, p.importe_total, p.importe_total_desc, " +
                      "p.estado, p.metodo_pago_id, p.usuario_id, " +
                      "mp.nombre as metodo_pago, u.nombre as usuario " +
                      "FROM pedido p " +
                      "INNER JOIN metodo_pago mp ON p.metodo_pago_id = mp.id " +
                      "INNER JOIN usuario u ON p.usuario_id = u.id " +
                      "WHERE u.email = ? " +
                      "ORDER BY p.fecha DESC";
        
        try (PreparedStatement ps = connection.connect().prepareStatement(query)) {
            ps.setString(1, email);
            
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    pedidos.add(new String[]{
                        String.valueOf(rs.getInt("id")),
                        rs.getString("fecha"),
                        rs.getString("descripcion"),
                        String.valueOf(rs.getDouble("importe_total")),
                        String.valueOf(rs.getDouble("importe_total_desc")),
                        rs.getString("estado"),
                        String.valueOf(rs.getInt("metodo_pago_id")),
                        String.valueOf(rs.getInt("usuario_id")),
                        rs.getString("metodo_pago"),
                        rs.getString("usuario")
                    });
                }
            }
        }
        return pedidos;
    }
    
    /**
     * Obtiene pedidos pendientes de envío
     */
    public List<String[]> getPedidosPendientesEnvio() throws SQLException {
        List<String[]> pedidos = new ArrayList<>();
        String query = "SELECT p.id, p.fecha, p.descripcion, p.importe_total, p.importe_total_desc, " +
                      "p.estado, p.metodo_pago_id, p.usuario_id, " +
                      "mp.nombre as metodo_pago, u.nombre as usuario " +
                      "FROM pedido p " +
                      "INNER JOIN metodo_pago mp ON p.metodo_pago_id = mp.id " +
                      "INNER JOIN usuario u ON p.usuario_id = u.id " +
                      "WHERE p.estado = 'procesando' " +
                      "ORDER BY p.fecha DESC";
        
        try (PreparedStatement ps = connection.connect().prepareStatement(query);
             ResultSet rs = ps.executeQuery()) {
            
            while (rs.next()) {
                pedidos.add(new String[]{
                    String.valueOf(rs.getInt("id")),
                    rs.getString("fecha"),
                    rs.getString("descripcion"),
                    String.valueOf(rs.getDouble("importe_total")),
                    String.valueOf(rs.getDouble("importe_total_desc")),
                    rs.getString("estado"),
                    String.valueOf(rs.getInt("metodo_pago_id")),
                    String.valueOf(rs.getInt("usuario_id")),
                    rs.getString("metodo_pago"),
                    rs.getString("usuario")
                });
            }
        }
        return pedidos;
    }
    
    /**
     * Actualiza el estado de un pedido
     */
    public List<String[]> updateEstado(int id, String estado) throws SQLException {
        List<String[]> pedidos = new ArrayList<>();
        String query = "UPDATE pedido SET estado = ? WHERE id = ? " +
                      "RETURNING id, fecha, descripcion, importe_total, importe_total_desc, " +
                      "estado, metodo_pago_id, usuario_id";
        
        try (PreparedStatement ps = connection.connect().prepareStatement(query)) {
            ps.setString(1, estado);
            ps.setInt(2, id);
            
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    pedidos.add(new String[]{
                        String.valueOf(rs.getInt("id")),
                        rs.getString("fecha"),
                        rs.getString("descripcion"),
                        String.valueOf(rs.getDouble("importe_total")),
                        String.valueOf(rs.getDouble("importe_total_desc")),
                        rs.getString("estado"),
                        String.valueOf(rs.getInt("metodo_pago_id")),
                        String.valueOf(rs.getInt("usuario_id"))
                    });
                }
            }
        }
        return pedidos;
    }

    public void disconnect() {
        // La conexión se cierra automáticamente con try-with-resources
    }
} 