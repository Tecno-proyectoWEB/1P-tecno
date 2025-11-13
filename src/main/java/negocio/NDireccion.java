package negocio;

import data.DDireccion;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Clase de lógica de negocio para direccion
 */
public class NDireccion {
    
    private DDireccion dDireccion;
    
    public NDireccion() {
        this.dDireccion = new DDireccion();
    }
    
    /**
     * Obtiene todas las direcciones
     */
    public List<String[]> getAll() throws SQLException {
        return dDireccion.getAll();
    }
    
    /**
     * Obtiene una dirección por ID
     */
    public List<String[]> getById(int id) throws SQLException {
        return dDireccion.getById(id);
    }
    
    /**
     * Obtiene direcciones por nombre
     */
    public List<String[]> getByNombre(String nombre) throws SQLException {
        return dDireccion.getByNombre(nombre);
    }
    
    /**
     * Guarda una nueva dirección
     */
    public List<String[]> save(String nombre, Double longitud, Double latitud, String referencia) throws SQLException {
        return dDireccion.save(nombre, longitud, latitud, referencia);
    }
    
    /**
     * Crea una dirección desde una URL de Google Maps
     */
    public List<String[]> crearDesdeGoogleMaps(String nombre, String urlGoogleMaps, String referencia) throws SQLException {
        // Extraer coordenadas de la URL
        double[] coordenadas = DDireccion.extraerCoordenadasDeUrl(urlGoogleMaps);
        if (coordenadas == null) {
            throw new SQLException("No se pudieron extraer coordenadas de la URL de Google Maps");
        }
        
        double latitud = coordenadas[0];
        double longitud = coordenadas[1];
        
        return dDireccion.save(nombre, longitud, latitud, referencia);
    }
    
    /**
     * Actualiza una dirección existente
     */
    public List<String[]> update(int id, String nombre, Double longitud, Double latitud, String referencia) throws SQLException {
        return dDireccion.update(id, nombre, longitud, latitud, referencia);
    }
    
    /**
     * Elimina una dirección
     */
    public boolean delete(int id) throws SQLException {
        return dDireccion.delete(id);
    }
    
    /**
     * Genera URL de Google Maps para una dirección
     */
    public String generarUrlGoogleMaps(int id) throws SQLException {
        return dDireccion.generarUrlGoogleMaps(id);
    }
    
    /**
     * Obtiene direcciones cercanas a unas coordenadas
     */
    public List<String[]> getDireccionesCercanas(double latitud, double longitud, double radioKm) throws SQLException {
        return dDireccion.getDireccionesCercanas(latitud, longitud, radioKm);
    }
    
    /**
     * Obtiene estadísticas de direcciones
     */
    public List<String[]> getEstadisticasDirecciones() throws SQLException {
        return dDireccion.getEstadisticasDirecciones();
    }
    
    /**
     * Extrae coordenadas de una URL de Google Maps
     */
    public static double[] extraerCoordenadasDeUrl(String url) {
        return DDireccion.extraerCoordenadasDeUrl(url);
    }
    
    /**
     * Valida si una URL de Google Maps es válida
     */
    public static boolean esUrlGoogleMapsValida(String url) {
        if (url == null || url.isEmpty()) {
            return false;
        }
        
        return url.contains("google.com/maps") && url.contains("@");
    }
    
    /**
     * Obtiene direcciones con coordenadas
     */
    public List<String[]> getDireccionesConCoordenadas() throws SQLException {
        List<String[]> todasLasDirecciones = dDireccion.getAll();
        List<String[]> direccionesConCoordenadas = new ArrayList<>();
        
        for (String[] direccion : todasLasDirecciones) {
            String longitud = direccion[2];
            String latitud = direccion[3];
            
            if (longitud != null && latitud != null && !longitud.isEmpty() && !latitud.isEmpty()) {
                direccionesConCoordenadas.add(direccion);
            }
        }
        
        return direccionesConCoordenadas;
    }
    
    /**
     * Obtiene direcciones sin coordenadas
     */
    public List<String[]> getDireccionesSinCoordenadas() throws SQLException {
        List<String[]> todasLasDirecciones = dDireccion.getAll();
        List<String[]> direccionesSinCoordenadas = new ArrayList<>();
        
        for (String[] direccion : todasLasDirecciones) {
            String longitud = direccion[2];
            String latitud = direccion[3];
            
            if (longitud == null || latitud == null || longitud.isEmpty() || latitud.isEmpty()) {
                direccionesSinCoordenadas.add(direccion);
            }
        }
        
        return direccionesSinCoordenadas;
    }
    
    /**
     * Busca direcciones por texto (nombre o referencia)
     */
    public List<String[]> buscarDirecciones(String texto) throws SQLException {
        List<String[]> todasLasDirecciones = dDireccion.getAll();
        List<String[]> direccionesEncontradas = new ArrayList<>();
        
        String textoLower = texto.toLowerCase();
        
        for (String[] direccion : todasLasDirecciones) {
            String nombre = direccion[1];
            String referencia = direccion[4];
            
            if ((nombre != null && nombre.toLowerCase().contains(textoLower)) ||
                (referencia != null && referencia.toLowerCase().contains(textoLower))) {
                direccionesEncontradas.add(direccion);
            }
        }
        
        return direccionesEncontradas;
    }
    
    /**
     * Calcula la distancia entre dos puntos usando la fórmula de Haversine
     */
    public static double calcularDistancia(double lat1, double lon1, double lat2, double lon2) {
        final int R = 6371; // Radio de la Tierra en kilómetros
        
        double latDistance = Math.toRadians(lat2 - lat1);
        double lonDistance = Math.toRadians(lon2 - lon1);
        
        double a = Math.sin(latDistance / 2) * Math.sin(latDistance / 2)
                + Math.cos(Math.toRadians(lat1)) * Math.cos(Math.toRadians(lat2))
                * Math.sin(lonDistance / 2) * Math.sin(lonDistance / 2);
        
        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1 - a));
        
        return R * c;
    }
    
    /**
     * Obtiene la dirección más cercana a unas coordenadas
     */
    public List<String[]> getDireccionMasCercana(double latitud, double longitud) throws SQLException {
        List<String[]> direccionesConCoordenadas = getDireccionesConCoordenadas();
        
        if (direccionesConCoordenadas.isEmpty()) {
            return new ArrayList<>();
        }
        
        String[] direccionMasCercana = null;
        double distanciaMinima = Double.MAX_VALUE;
        
        for (String[] direccion : direccionesConCoordenadas) {
            double latDir = Double.parseDouble(direccion[3]);
            double lonDir = Double.parseDouble(direccion[2]);
            
            double distancia = calcularDistancia(latitud, longitud, latDir, lonDir);
            
            if (distancia < distanciaMinima) {
                distanciaMinima = distancia;
                direccionMasCercana = direccion;
            }
        }
        
        List<String[]> resultado = new ArrayList<>();
        if (direccionMasCercana != null) {
            // Agregar la distancia como un campo adicional
            String[] direccionConDistancia = new String[direccionMasCercana.length + 1];
            System.arraycopy(direccionMasCercana, 0, direccionConDistancia, 0, direccionMasCercana.length);
            direccionConDistancia[direccionMasCercana.length] = String.valueOf(distanciaMinima);
            resultado.add(direccionConDistancia);
        }
        
        return resultado;
    }
    
    public void disconnect() {
        dDireccion.disconnect();
    }
} 