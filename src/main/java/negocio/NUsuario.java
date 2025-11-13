package negocio;

import data.DUsuario;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.sql.Connection;
import java.sql.PreparedStatement;

public class NUsuario {

    private DUsuario dUsuario;

    public NUsuario() {
        this.dUsuario = new DUsuario();
    }

    public ArrayList<String[]> list() throws SQLException {
        return (ArrayList<String[]>) dUsuario.list();
    }

    public List<String[]> get(int id) throws SQLException {
        return dUsuario.get(id);
    }

    public  List<String[]> save(List<String> parametros) throws SQLException {
        return dUsuario.save(
                parametros.get(0), // nombre
                parametros.get(1), // apellido
                parametros.get(2), // email
                parametros.get(3), // telefono
                parametros.get(4)  // password
        );
    }

    public List<String[]> update(List<String> parametros) throws SQLException {
        return dUsuario.update(
                Integer.parseInt(parametros.get(0)), // id
                parametros.get(1), // nombre
                parametros.get(2), // apellido
                parametros.get(3), // email
                parametros.get(4)  // telefono
        );
    }

    public  List<String[]> delete(List<String> parametros) throws SQLException {
//        dUsuario.delete(Integer.parseInt(parametros.get(0)));
//        dUsuario.disconnect();
        return dUsuario.delete(Integer.parseInt(parametros.get(0)));
    }

    /**
     * Busca un usuario por su email
     * @param email Email del usuario a buscar
     * @return Lista con los datos del usuario si existe, lista vacía si no existe
     * @throws SQLException
     */
    public List<String[]> findByEmail(String email) throws SQLException {
        return dUsuario.findByEmail(email);
    }

    /**
     * Verifica si un usuario tiene registro de cliente
     * @param userId ID del usuario a verificar
     * @return true si tiene registro de cliente, false si no
     * @throws SQLException
     */
    public boolean isCliente(int userId) throws SQLException {
        return dUsuario.isCliente(userId);
    }

    /**
     * Verifica si un email existe en la base de datos
     * @param email Email a verificar
     * @return true si existe, false si no
     * @throws SQLException
     */
    public boolean emailExists(String email) throws SQLException {
        return dUsuario.emailExists(email);
    }

    /**
     * Registra un nuevo cliente asociado a un usuario existente
     * @param userId ID del usuario
     * @param nit Número de Identificación Tributaria
     * @return true si se registró correctamente, false en caso contrario
     * @throws SQLException
     */
    public boolean registerCliente(int userId, String nit) throws SQLException {
        return dUsuario.registerCliente(userId, nit);
    }

    /**
     * Registra un nuevo usuario y cliente en una sola transacción
     * @param nombre Nombre del usuario
     * @param apellido Apellido del usuario
     * @param email Email del usuario
     * @param telefono Número de teléfono del usuario
     * @param password Contraseña
     * @param nit Número de Identificación Tributaria
     * @return Lista con los datos del usuario registrado
     * @throws SQLException
     */
    public List<String[]> registerUserAndCliente(String nombre, String apellido, String email, String telefono, String password, String nit) throws SQLException {
        return dUsuario.registerUserAndCliente(nombre, apellido, email, telefono, password, nit);
    }
}
