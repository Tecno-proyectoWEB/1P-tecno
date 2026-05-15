package data;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import postgresConecction.DBConnection;
import postgresConecction.SqlConnection;

public class DPago {
    public static final String[] HEADERS = {"id", "pedido_id", "fecha", "monto", "tipo_pago", "estado", "metodo_pago_id", "metodo_pago"};
    
    private final SqlConnection connection;
    private final DMetodoPago dMetodoPago;
    
    public DPago() {
        this.connection = new SqlConnection(DBConnection.database, DBConnection.server, DBConnection.port, DBConnection.user, DBConnection.password);
        this.dMetodoPago = new DMetodoPago();
    }
    
    public List<String[]> save(int pedidoId, double monto, String tipoPago, int metodoPagoId) throws SQLException {
        List<String[]> pagos = new ArrayList<>();
        String query = "INSERT INTO pago (pedido_id, monto, tipo_pago, metodo_pago_id) " +
                      "VALUES (?, ?, ?, ?) RETURNING id, pedido_id, fecha, monto, tipo_pago, estado, metodo_pago_id";
        
        try (PreparedStatement ps = connection.connect().prepareStatement(query)) {
            ps.setInt(1, pedidoId);
            ps.setDouble(2, monto);
            ps.setString(3, tipoPago);
            ps.setInt(4, metodoPagoId);
            
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    pagos.add(new String[]{
                        String.valueOf(rs.getInt("id")),
                        String.valueOf(rs.getInt("pedido_id")),
                        rs.getString("fecha"),
                        String.valueOf(rs.getDouble("monto")),
                        rs.getString("tipo_pago"),
                        rs.getBoolean("estado") ? "Activo" : "Inactivo",
                        String.valueOf(rs.getInt("metodo_pago_id")),
                        dMetodoPago.getNombreById(rs.getInt("metodo_pago_id"))
                    });
                }
            }
        }
        return pagos;
    }
    
    // Método sobrecargado para usar una conexión existente (para transacciones)
    public List<String[]> save(Connection conn, int pedidoId, double monto, String tipoPago, int metodoPagoId) throws SQLException {
        List<String[]> pagos = new ArrayList<>();
        String query = "INSERT INTO pago (pedido_id, monto, tipo_pago, metodo_pago_id) " +
                      "VALUES (?, ?, ?, ?) RETURNING id, pedido_id, fecha, monto, tipo_pago, estado, metodo_pago_id";
        
        try (PreparedStatement ps = conn.prepareStatement(query)) {
            ps.setInt(1, pedidoId);
            ps.setDouble(2, monto);
            ps.setString(3, tipoPago);
            ps.setInt(4, metodoPagoId);
            
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    pagos.add(new String[]{
                        String.valueOf(rs.getInt("id")),
                        String.valueOf(rs.getInt("pedido_id")),
                        rs.getString("fecha"),
                        String.valueOf(rs.getDouble("monto")),
                        rs.getString("tipo_pago"),
                        rs.getBoolean("estado") ? "Activo" : "Inactivo",
                        String.valueOf(rs.getInt("metodo_pago_id")),
                        dMetodoPago.getNombreById(rs.getInt("metodo_pago_id"))
                    });
                }
            }
        }
        return pagos;
    }
    
    public List<String[]> getPagosByPedido(int pedidoId) throws SQLException {
        List<String[]> pagos = new ArrayList<>();
        String query = "SELECT p.*, mp.nombre as metodo_pago " +
                      "FROM pago p " +
                      "INNER JOIN metodo_pago mp ON p.metodo_pago_id = mp.id " +
                      "WHERE p.pedido_id = ? " +
                      "ORDER BY p.fecha";
        
        try (PreparedStatement ps = connection.connect().prepareStatement(query)) {
            ps.setInt(1, pedidoId);
            
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    pagos.add(new String[]{
                        String.valueOf(rs.getInt("id")),
                        String.valueOf(rs.getInt("pedido_id")),
                        rs.getString("fecha"),
                        String.valueOf(rs.getDouble("monto")),
                        rs.getString("tipo_pago"),
                        rs.getBoolean("estado") ? "Activo" : "Inactivo",
                        String.valueOf(rs.getInt("metodo_pago_id")),
                        rs.getString("metodo_pago")
                    });
                }
            }
        }
        return pagos;
    }

    public double getTotalPagadoPedido(int pedidoId) throws SQLException {
        String query = "SELECT COALESCE(SUM(monto), 0) as total_pagado " +
                      "FROM pago WHERE pedido_id = ? AND estado = true";
        
        try (PreparedStatement ps = connection.connect().prepareStatement(query)) {
            ps.setInt(1, pedidoId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getDouble("total_pagado");
                }
            }
        }
        return 0.0;
    }
    
    public void disconnect() {
        // La conexión se cierra automáticamente con try-with-resources
    }
}