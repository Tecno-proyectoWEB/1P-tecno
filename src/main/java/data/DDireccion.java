package data;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import postgresConecction.DBConnection;
import postgresConecction.SqlConnection;

/**
 * Clase de acceso a datos para la tabla direccion
 */
public class DDireccion {
    
    public static final String[] HEADERS = {"id", "nombre", "longitud", "latitud", "referencia"};
    
    private final SqlConnection connection;
    
    public DDireccion() {
        this.connection = new SqlConnection(DBConnection.database, DBConnection.server, DBConnection.port, DBConnection.user, DBConnection.password);
    }
    
    /**
     * Obtiene todas las direcciones
     */
    public List<String[]> getAll() throws SQLException {
        List<String[]> direcciones = new ArrayList<>();
        String query = "SELECT id, nombre, longitud, latitud, referencia " +
                      "FROM direccion " +
                      "ORDER BY nombre";
        
        try (PreparedStatement ps = connection.connect().prepareStatement(query);
             ResultSet rs = ps.executeQuery()) {
            
            while (rs.next()) {
                direcciones.add(new String[]{
                    String.valueOf(rs.getInt("id")),
                    rs.getString("nombre"),
                    rs.getString("longitud"),
                    rs.getString("latitud"),
                    rs.getString("referencia")
                });
            }
        }
        
        return direcciones;
    }
    
    /**
     * Obtiene una dirección por ID
     */
    public List<String[]> getById(int id) throws SQLException {
        List<String[]> direcciones = new ArrayList<>();
        String query = "SELECT id, nombre, longitud, latitud, referencia " +
                      "FROM direccion " +
                      "WHERE id = ?";
        
        try (PreparedStatement ps = connection.connect().prepareStatement(query)) {
            
            ps.setInt(1, id);
            
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    direcciones.add(new String[]{
                        String.valueOf(rs.getInt("id")),
                        rs.getString("nombre"),
                        rs.getString("longitud"),
                        rs.getString("latitud"),
                        rs.getString("referencia")
                    });
                }
            }
        }
        
        return direcciones;
    }
    
    /**
     * Obtiene direcciones por nombre (búsqueda parcial)
     */
    public List<String[]> getByNombre(String nombre) throws SQLException {
        List<String[]> direcciones = new ArrayList<>();
        String query = "SELECT id, nombre, longitud, latitud, referencia " +
                      "FROM direccion " +
                      "WHERE LOWER(nombre) LIKE LOWER(?) " +
                      "ORDER BY nombre";
        
        try (PreparedStatement ps = connection.connect().prepareStatement(query)) {
            
            ps.setString(1, "%" + nombre + "%");
            
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    direcciones.add(new String[]{
                        String.valueOf(rs.getInt("id")),
                        rs.getString("nombre"),
                        rs.getString("longitud"),
                        rs.getString("latitud"),
                        rs.getString("referencia")
                    });
                }
            }
        }
        
        return direcciones;
    }
    
    /**
     * Guarda una nueva dirección
     */
    public List<String[]> save(String nombre, Double longitud, Double latitud, String referencia) throws SQLException {
        List<String[]> direcciones = new ArrayList<>();
        String query = "INSERT INTO direccion (nombre, longitud, latitud, referencia) " +
                      "VALUES (?, ?, ?, ?) " +
                      "RETURNING id, nombre, longitud, latitud, referencia";
        
        try (PreparedStatement ps = connection.connect().prepareStatement(query)) {
            
            ps.setString(1, nombre);
            if (longitud != null) {
                ps.setDouble(2, longitud);
            } else {
                ps.setNull(2, Types.DOUBLE);
            }
            if (latitud != null) {
                ps.setDouble(3, latitud);
            } else {
                ps.setNull(3, Types.DOUBLE);
            }
            ps.setString(4, referencia);
            
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    direcciones.add(new String[]{
                        String.valueOf(rs.getInt("id")),
                        rs.getString("nombre"),
                        rs.getString("longitud"),
                        rs.getString("latitud"),
                        rs.getString("referencia")
                    });
                }
            }
        }
        
        return direcciones;
    }
    
    /**
     * Actualiza una dirección existente
     */
    public List<String[]> update(int id, String nombre, Double longitud, Double latitud, String referencia) throws SQLException {
        List<String[]> direcciones = new ArrayList<>();
        String query = "UPDATE direccion SET nombre = ?, longitud = ?, latitud = ?, referencia = ? " +
                      "WHERE id = ? " +
                      "RETURNING id, nombre, longitud, latitud, referencia";
        
        try (PreparedStatement ps = connection.connect().prepareStatement(query)) {
            
            ps.setString(1, nombre);
            if (longitud != null) {
                ps.setDouble(2, longitud);
            } else {
                ps.setNull(2, Types.DOUBLE);
            }
            if (latitud != null) {
                ps.setDouble(3, latitud);
            } else {
                ps.setNull(3, Types.DOUBLE);
            }
            ps.setString(4, referencia);
            ps.setInt(5, id);
            
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    direcciones.add(new String[]{
                        String.valueOf(rs.getInt("id")),
                        rs.getString("nombre"),
                        rs.getString("longitud"),
                        rs.getString("latitud"),
                        rs.getString("referencia")
                    });
                }
            }
        }
        
        return direcciones;
    }
    
    /**
     * Elimina una dirección por ID
     */
    public boolean delete(int id) throws SQLException {
        String query = "DELETE FROM direccion WHERE id = ?";
        
        try (PreparedStatement ps = connection.connect().prepareStatement(query)) {
            
            ps.setInt(1, id);
            int rowsAffected = ps.executeUpdate();
            return rowsAffected > 0;
        }
    }
    
    /**
     * Genera URL de Google Maps para una dirección
     */
    public String generarUrlGoogleMaps(int id) throws SQLException {
        List<String[]> direccion = getById(id);
        if (direccion.isEmpty()) {
            return null;
        }
        
        String[] datos = direccion.get(0);
        String longitud = datos[2];
        String latitud = datos[3];
        
        if (longitud != null && latitud != null && !longitud.isEmpty() && !latitud.isEmpty()) {
            return "https://www.google.com/maps/@" + latitud + "," + longitud + ",16z?entry=ttu";
        }
        
        return null;
    }
    
    /**
     * Obtiene direcciones cercanas a unas coordenadas (radio de 1km)
     */
    public List<String[]> getDireccionesCercanas(double latitud, double longitud, double radioKm) throws SQLException {
        List<String[]> direcciones = new ArrayList<>();
        String query = "SELECT id, nombre, longitud, latitud, referencia, " +
                      "SQRT(POW(69.1 * (latitud - ?), 2) + POW(69.1 * (? - longitud) * COS(latitud / 57.3), 2)) AS distancia " +
                      "FROM direccion " +
                      "WHERE latitud IS NOT NULL AND longitud IS NOT NULL " +
                      "HAVING distancia <= ? " +
                      "ORDER BY distancia";
        
        try (PreparedStatement ps = connection.connect().prepareStatement(query)) {
            
            ps.setDouble(1, latitud);
            ps.setDouble(2, longitud);
            ps.setDouble(3, radioKm);
            
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    direcciones.add(new String[]{
                        String.valueOf(rs.getInt("id")),
                        rs.getString("nombre"),
                        rs.getString("longitud"),
                        rs.getString("latitud"),
                        rs.getString("referencia"),
                        String.valueOf(rs.getDouble("distancia"))
                    });
                }
            }
        }
        
        return direcciones;
    }
    
    /**
     * Obtiene estadísticas de direcciones
     */
    public List<String[]> getEstadisticasDirecciones() throws SQLException {
        List<String[]> estadisticas = new ArrayList<>();
        String query = "SELECT " +
                      "COUNT(*) as total_direcciones, " +
                      "COUNT(CASE WHEN longitud IS NOT NULL AND latitud IS NOT NULL THEN 1 END) as con_coordenadas, " +
                      "COUNT(CASE WHEN longitud IS NULL OR latitud IS NULL THEN 1 END) as sin_coordenadas " +
                      "FROM direccion";
        
        try (PreparedStatement ps = connection.connect().prepareStatement(query);
             ResultSet rs = ps.executeQuery()) {
            
            if (rs.next()) {
                estadisticas.add(new String[]{
                    String.valueOf(rs.getInt("total_direcciones")),
                    String.valueOf(rs.getInt("con_coordenadas")),
                    String.valueOf(rs.getInt("sin_coordenadas"))
                });
            }
        }
        
        return estadisticas;
    }
    
    /**
     * Extrae coordenadas de una URL de Google Maps
     */
    public static double[] extraerCoordenadasDeUrl(String url) {
        if (url == null || url.isEmpty()) {
            return null;
        }
        
        try {
            // Buscar el patrón @latitud,longitud en la URL
            int atIndex = url.indexOf("@");
            if (atIndex == -1) {
                return null;
            }
            
            String coordenadas = url.substring(atIndex + 1);
            
            // Buscar el final de las coordenadas (antes de ? o /)
            int endIndex = coordenadas.indexOf("?");
            if (endIndex == -1) {
                endIndex = coordenadas.indexOf("/");
            }
            if (endIndex != -1) {
                coordenadas = coordenadas.substring(0, endIndex);
            }
            
            // Dividir por comas y tomar los primeros dos valores
            String[] partes = coordenadas.split(",");
            if (partes.length < 2) {
                return null;
            }
            
            String latitudStr = partes[0];
            String longitudStr = partes[1];
            
            // Limpiar la longitud de cualquier caracter no numérico
            longitudStr = longitudStr.replaceAll("[^0-9.-]", "");
            
            double latitud = Double.parseDouble(latitudStr);
            double longitud = Double.parseDouble(longitudStr);
            
            return new double[]{latitud, longitud};
        } catch (Exception e) {
            System.err.println("Error extrayendo coordenadas de URL: " + url);
            System.err.println("Error: " + e.getMessage());
            return null;
        }
    }
    
    public void disconnect() {
        // La conexión se cierra automáticamente con try-with-resources
    }
} 