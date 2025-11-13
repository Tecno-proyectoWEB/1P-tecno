package interfaces;

import librerias.ParamsAction;

/**
 * Interfaz ICasoUsoListener define los métodos para los casos de uso soportados
 * por la aplicación en base a las tablas del modelo de dominio.
 */
public interface ICasoUsoListener {
    void usuario(ParamsAction event);
    void error(ParamsAction event); // Método general para manejar errores
    void help(ParamsAction event); // Método para mostrar ayuda
    void register(ParamsAction event); // Método para registro de usuarios
    void categoria(ParamsAction event);
    void producto(ParamsAction event);
    void metodoPago(ParamsAction event);
    void carrito(ParamsAction event);
    void notaVenta(ParamsAction event);
    void pedido(ParamsAction event);
    void comprar(ParamsAction event);
}
