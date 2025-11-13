package com.mycompany.parcial1.tecnoweb;

import data.*;
import interfaces.ICasoUsoListener;
import interfaces.IEmailListener;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import librerias.Email;
import librerias.HtmlRes;
import librerias.Interpreter;
import librerias.ParamsAction;
import librerias.analex.Token;
import negocio.*;
import postgresConecction.EmailReceipt;
import postgresConecction.EmailSend;
import postgresConecction.SqlConnection;
import postgresConecction.DBConnection;

public class EmailApp implements ICasoUsoListener, IEmailListener {

    private static final int CONSTRAINTS_ERROR = -2;
    private static final int NUMBER_FORMAT_ERROR = -3;
    private static final int INDEX_OUT_OF_BOUND_ERROR = -4;
    private static final int PARSE_ERROR = -5;
    private static final int AUTHORIZATION_ERROR = -6;

    private EmailReceipt emailReceipt;
    private NUsuario nUsuario;
    // private NPromocion nPromocion; // COMENTADO - No existe tabla promocion
    private NCategoria nCategoria;
    private NProducto nProducto;
    private NMetodoPago nMetodoPago;
    // private NCliente nCliente; // COMENTADO - No existe tabla cliente (se usa usuario con rol_id)
    private NCarrito nCarrito;
    private NNotaVenta nNotaVenta;
    private NPedido nPedido;
    // private NDireccion nDireccion; // COMENTADO - No existe tabla direccion

    public EmailApp() {
        this.emailReceipt = new EmailReceipt();
        this.emailReceipt.setEmailListener(this);
        this.nUsuario = new NUsuario();
        // this.nPromocion = new NPromocion(); // COMENTADO - No existe tabla promocion
        this.nCategoria = new NCategoria();
        this.nProducto = new NProducto();
        this.nMetodoPago = new NMetodoPago();
        // this.nCliente = new NCliente(); // COMENTADO - No existe tabla cliente
        this.nCarrito = new NCarrito();
        this.nNotaVenta = new NNotaVenta();
        this.nPedido = new NPedido();
        // this.nDireccion = new NDireccion(); // COMENTADO - No existe tabla direccion
    }

    public void start() {
        Thread thread = new Thread(emailReceipt);
        thread.setName("Mail Receipt");
        thread.start();
    }

    // Implementacion para cada caso de uso específico
    @Override
    public void usuario(ParamsAction event) {
        try {
            switch (event.getAction()) {
                case Token.ADD:
                    // nUsuario.save(event.getParams());
                    List<String[]> userDataSaved = nUsuario.save(event.getParams());
                    // simpleNotifySuccess(event.getSender(), "Usuario guardado correctamente");
                    tableNotifySuccess(event.getSender(), "Usuario guardado correctamente", DUsuario.HEADERS, (ArrayList<String[]>) userDataSaved, event.getCommand());
                    break;
                case Token.GET:
                    if (event.getParams() != null && !event.getParams().isEmpty()) {
                        // Si hay parámetros, se asume que es una solicitud de usuario por ID
                        String idParam = event.getParams().get(0); // Supone que el ID es el primer parámetro
                        try {
                            int id = Integer.parseInt(idParam);
                            List<String[]> userData = nUsuario.get(id);
                            if (userData != null) {
                                //simpleNotifySuccess(event.getSender(), "Usuario encontrado: " + Arrays.toString(userData));
                                tableNotifySuccess(event.getSender(), "Usuario encontrado", DUsuario.HEADERS, (ArrayList<String[]>) userData, event.getCommand());
                            } else {
                                simpleNotify(event.getSender(), "Error", "Usuario no encontrado.");
                            }
                        } catch (NumberFormatException e) {
                            simpleNotify(event.getSender(), "Error", "ID inválido.");
                        }
                    } else {
                        // Si no hay parámetros, se asume que es una solicitud de todos los usuarios
                        tableNotifySuccess(event.getSender(), "Lista de Usuarios", DUsuario.HEADERS, nUsuario.list());
                    }
                    break;
                case Token.MODIFY:
                    // nUsuario.update(event.getParams());
                    List<String[]> userDataUpdated = nUsuario.update(event.getParams());
                    // simpleNotifySuccess(event.getSender(), "Usuario actualizado correctamente");
                    tableNotifySuccess(event.getSender(), "Usuario actualizado correctamente", DUsuario.HEADERS, (ArrayList<String[]>) userDataUpdated, event.getCommand());
                    break;
                case Token.DELETE:
                    // nUsuario.delete(event.getParams());
                    List<String[]> userDataDeleted = nUsuario.delete(event.getParams());
                    // simpleNotifySuccess(event.getSender(), "Usuario eliminado correctamente");
                    tableNotifySuccess(event.getSender(), "Usuario eliminado correctamente", DUsuario.HEADERS, (ArrayList<String[]>) userDataDeleted, event.getCommand());
                    break;
            }
        } catch (SQLException ex) {
            handleError(CONSTRAINTS_ERROR, event.getSender(), Collections.singletonList("Error SQL: " + ex.getMessage()));
        } catch (IndexOutOfBoundsException ex) {
            handleError(INDEX_OUT_OF_BOUND_ERROR, event.getSender(), Collections.singletonList("Error de índice: " + ex.getMessage()));
        }
    }










    /* MÉTODO PROMOCION COMENTADO - TABLA NO EXISTE EN BD
    @Override
    public void promocion(ParamsAction event) {
        try {
            switch (event.getAction()) {
                case Token.GET:
                    if (event.getParams() != null && event.getParams().size() >= 1) {
                        String param = event.getParams().get(0);
                        
                        // Intentar como ID numérico primero
                        try {
                            int id = Integer.parseInt(param);
                            List<String[]> promocionData = nPromocion.getById(id);
                            
                            if (!promocionData.isEmpty()) {
                                String[] enhancedHeaders = {"ID", "Nombre", "Fecha Inicio", "Fecha Fin", "Descuento", "Producto ID", "Producto", "Precio Venta"};
                                tableNotifySuccess(event.getSender(), "Promoción encontrada", enhancedHeaders, (ArrayList<String[]>) promocionData, event.getCommand());
                            } else {
                                simpleNotify(event.getSender(), "Promoción no encontrada", 
                                    "❌ **No se encontró la promoción con ID: " + id + "**");
                            }
                        } catch (NumberFormatException e) {
                            // Si no es número, intentar como producto_id
                            int productoId = Integer.parseInt(param);
                            List<String[]> promocionesData = nPromocion.getByProductoId(productoId);
                            
                            if (!promocionesData.isEmpty()) {
                                String[] enhancedHeaders = {"ID", "Nombre", "Fecha Inicio", "Fecha Fin", "Descuento", "Producto ID", "Producto", "Precio Venta"};
                                tableNotifySuccess(event.getSender(), "Promociones del Producto", enhancedHeaders, (ArrayList<String[]>) promocionesData, event.getCommand());
                            } else {
                                simpleNotify(event.getSender(), "No hay promociones", 
                                    "📝 **No hay promociones para el producto con ID: " + productoId + "**");
                            }
                        }
                    } else {
                        // Comando: promocion get (todas las promociones)
                        List<String[]> promocionesData = nPromocion.getAll();
                        
                        if (!promocionesData.isEmpty()) {
                            String[] enhancedHeaders = {"ID", "Nombre", "Fecha Inicio", "Fecha Fin", "Descuento", "Producto ID", "Producto", "Precio Venta"};
                            tableNotifySuccess(event.getSender(), "Lista de Promociones", enhancedHeaders, (ArrayList<String[]>) promocionesData, event.getCommand());
                        } else {
                            simpleNotify(event.getSender(), "No hay promociones", 
                                "📝 **No hay promociones registradas en el sistema.**");
                        }
                    }
                    break;
                    
                case Token.ADD:
                    if (event.getParams() != null && event.getParams().size() >= 4) {
                        // Comando: promocion add <nombre, fecha_inicio, fecha_fin, descuento, producto_id>
                        String nombre = event.getParams().get(0);
                        String fechaInicio = event.getParams().get(1);
                        String fechaFin = event.getParams().get(2);
                        String descuento = event.getParams().get(3);
                        Integer productoId = null;
                        
                        if (event.getParams().size() >= 5) {
                            productoId = Integer.parseInt(event.getParams().get(4));
                        }
                        
                        List<String[]> promocionData = nPromocion.save(nombre, fechaInicio, fechaFin, descuento, productoId);
                        tableNotifySuccess(event.getSender(), "Promoción guardada correctamente", DPromocion.HEADERS, (ArrayList<String[]>) promocionData, event.getCommand());
                        
                    } else {
                        simpleNotify(event.getSender(), "Error de Parámetros", 
                            "❌ **Parámetros incorrectos.**\n\n" +
                            "📋 **Formato correcto:**\n" +
                            "promocion add &lt;nombre, fecha_inicio, fecha_fin, descuento, producto_id&gt;\n\n" +
                            "📧 **Ejemplo:**\n" +
                            "promocion add Descuento Verano, 2024-06-01, 2024-08-31, 20%, 1");
                    }
                    break;
                    
                case Token.MODIFY:
                    if (event.getParams() != null && event.getParams().size() >= 5) {
                        // Comando: promocion modify <id, nombre, fecha_inicio, fecha_fin, descuento, producto_id>
                        int id = Integer.parseInt(event.getParams().get(0));
                        String nombre = event.getParams().get(1);
                        String fechaInicio = event.getParams().get(2);
                        String fechaFin = event.getParams().get(3);
                        String descuento = event.getParams().get(4);
                        Integer productoId = null;
                        
                        if (event.getParams().size() >= 6) {
                            productoId = Integer.parseInt(event.getParams().get(5));
                        }
                        
                        List<String[]> promocionData = nPromocion.update(id, nombre, fechaInicio, fechaFin, descuento, productoId);
                        tableNotifySuccess(event.getSender(), "Promoción modificada correctamente", DPromocion.HEADERS, (ArrayList<String[]>) promocionData, event.getCommand());
                        
                    } else {
                        simpleNotify(event.getSender(), "Error de Parámetros", 
                            "❌ **Parámetros incorrectos.**\n\n" +
                            "📋 **Formato correcto:**\n" +
                            "promocion modify &lt;id, nombre, fecha_inicio, fecha_fin, descuento, producto_id&gt;\n\n" +
                            "📧 **Ejemplo:**\n" +
                            "promocion modify 1, Descuento Verano, 2024-06-01, 2024-08-31, 25%, 1");
                    }
                    break;
                    
                case Token.DELETE:
                    if (event.getParams() != null && event.getParams().size() >= 1) {
                        // Comando: promocion delete <id>
                        int id = Integer.parseInt(event.getParams().get(0));
                        
                        boolean success = nPromocion.delete(id);
                        if (success) {
                            simpleNotifySuccess(event.getSender(), 
                                "✅ **Promoción eliminada exitosamente**\n\n" +
                                "🗑️ **ID eliminado:** " + id);
                        } else {
                            simpleNotify(event.getSender(), "Error al eliminar", 
                                "❌ **No se pudo eliminar la promoción con ID: " + id + "**\n\n" +
                                "📝 **Verifique que la promoción existe.**");
                        }
                        
                    } else {
                        simpleNotify(event.getSender(), "Error de Parámetros", 
                            "❌ **Parámetros incorrectos.**\n\n" +
                            "📋 **Formato correcto:**\n" +
                            "promocion delete &lt;id&gt;\n\n" +
                            "📧 **Ejemplo:**\n" +
                            "promocion delete 1");
                    }
                    break;
                    
                default:
                    simpleNotify(event.getSender(), "Comando no reconocido", 
                        "❌ **Comando de promoción no reconocido.**\n\n" +
                        "📋 **Comandos disponibles:**\n" +
                        "• promocion get\n" +
                        "• promocion get &lt;id&gt;\n" +
                        "• promocion get &lt;producto_id&gt;\n" +
                        "• promocion add &lt;nombre, fecha_inicio, fecha_fin, descuento, producto_id&gt;\n" +
                        "• promocion modify &lt;id, nombre, fecha_inicio, fecha_fin, descuento, producto_id&gt;\n" +
                        "• promocion delete &lt;id&gt;");
                    break;
            }
        } catch (SQLException e) {
            Logger.getLogger(EmailApp.class.getName()).log(Level.SEVERE, "Error en método promocion para usuario: " + event.getSender(), e);
            simpleNotify(event.getSender(), "Error de Sistema", 
                "❌ **Se ha producido un error al procesar su solicitud.**\n\n" +
                "🔧 **Error en promocion:** " + e.getMessage());
        }
    }
    FIN COMENTARIO MÉTODO PROMOCION */











    @Override
    public void error(ParamsAction event) {
        System.out.println("=== ERROR HANDLER ===");
        System.out.println("Sender: " + event.getSender());
        System.out.println("Command: " + event.getCommand());
        System.out.println("Action: " + event.getAction());
        System.out.println("Params: " + event.getParams());
        
        handleError(event.getAction(), event.getSender(), event.getParams());
    }

    @Override
    public void help(ParamsAction event) {
        try {
            if (event.getAction() == Token.GET) {
                String userEmail = event.getSender();
                
                // Log para debugging
                Logger.getLogger(EmailApp.class.getName()).info("Help solicitado por: " + userEmail);
                System.out.println("=== HELP SOLICITADO ===");
                System.out.println("Usuario: " + userEmail);
                System.out.println("Action: " + event.getAction());
                
                // Verificar si el usuario existe en la base de datos
                /*if (!nUsuario.emailExists(userEmail)) {
                    System.out.println("=== USUARIO NO REGISTRADO ===");
                    // El usuario no existe en la base de datos
                    simpleNotify(userEmail, "Acceso Denegado", 
                        "❌ **Su correo electrónico no está registrado en nuestro sistema.**\n\n" +
                        "🔐 **Para acceder a los comandos del sistema, debe registrarse primero.**\n\n" +
                        "📧 **Use el siguiente comando para registrarse:**\n" +
                        "**register user &lt;nombre, celular, email, genero, password, nit&gt;**\n\n" +
                        "📋 **Ejemplo:**\n" +
                        "register user Juan Pérez, 70012345, juan@email.com, masculino, miPassword123, 1234567890\n\n" +
                        "📧 **O contacte al administrador:**\n" +
                        "• Email: admin@tecnoweb.org.bo\n" +
                        "• Teléfono: +591-2-1234567\n\n" +
                        "✅ **Una vez registrado, podrá acceder a todos los comandos del sistema.**");
                    return;
                }*/
                
                // El usuario existe, verificar si tiene registro de cliente
                List<String[]> userData = nUsuario.findByEmail(userEmail);
                /*if (!userData.isEmpty()) {
                    int userId = Integer.parseInt(userData.get(0)[0]); // Obtener el ID del usuario
                    
                    if (!nUsuario.isCliente(userId)) {
                        System.out.println("=== USUARIO SIN REGISTRO DE CLIENTE ===");
                        // El usuario existe pero no tiene registro de cliente
                        simpleNotify(userEmail, "Registro de Cliente Requerido", 
                            "⚠️ **Su cuenta de usuario existe pero no tiene registro de cliente.**\n\n" +
                            "🔐 **Para acceder a los comandos del sistema, debe completar su registro como cliente.**\n\n" +
                            "📧 **Use el siguiente comando para registrarse como cliente:**\n" +
                            "**register cliente &lt;nit&gt;**\n\n" +
                            "📋 **Ejemplo:**\n" +
                            "register cliente 1234567890\n\n" +
                            "📧 **O contacte al administrador:**\n" +
                            "• Email: admin@tecnoweb.org.bo\n" +
                            "• Teléfono: +591-2-1234567\n\n" +
                            "✅ **Una vez completado el registro como cliente, podrá acceder a todos los comandos.**");
                        return;
                    }
                }*/
                
                System.out.println("=== USUARIO AUTORIZADO - MOSTRANDO AYUDA ===");
                // El usuario existe y tiene registro de cliente, mostrar ayuda completa
                String[] headers = {"Categoría", "Comando", "Descripción"};
                ArrayList<String[]> data = new ArrayList<>();

                // Comandos de Registro
                data.add(new String[]{"Registro", "register user &lt;nombre, celular, email, genero, password, nit&gt;", "Registra un nuevo usuario y cliente"});

                // Usuarios
                data.add(new String[]{"Usuarios", "usuario get", "Obtiene todos los usuarios"});

                // Promociones
                data.add(new String[]{"Promociones", "promocion get", "Obtener todas las promociones"});

        // Comandos de Categorías
        data.add(new String[]{"Categorías", "categoria get", "Obtiene todas las categorías"});
        
        // Comandos de Productos
        data.add(new String[]{"Productos", "producto get", "Obtiene todos los productos"});
        data.add(new String[]{"Productos", "producto get id &lt;producto_id&gt;", "Obtiene un producto específico por ID"});
        data.add(new String[]{"Productos", "producto get categoria &lt;categoria_id&gt;", "Obtiene productos de una categoría específica"});
        
        // Comandos de Métodos de Pago
        data.add(new String[]{"Métodos de Pago", "tipopago get", "Obtiene todos los métodos de pago"});
        
        // Comandos de Clientes
        data.add(new String[]{"Clientes", "cliente get", "Obtiene todos los clientes"});
        
        // Comandos de Carrito
        data.add(new String[]{"Carrito", "carrito get", "Obtiene tu carrito activo con productos"});
        data.add(new String[]{"Carrito", "carrito add &lt;producto_id, cantidad&gt;", "Agrega producto al carrito"});
        data.add(new String[]{"Carrito", "carrito modify &lt;detalle_id, cantidad&gt;", "Modifica cantidad de producto"});
        data.add(new String[]{"Carrito", "carrito delete &lt;detalle_id&gt;", "Elimina producto del carrito"});

        // Sistema de Ventas
        data.add(new String[]{"Nota de Venta", "notaventa get", "Obtiene mis notas de venta"});
        data.add(new String[]{"Nota de Venta", "notaventa get &lt;id&gt;", "Obtiene una nota de venta específica"});
        data.add(new String[]{"Nota de Venta", "notaventa productos &lt;id&gt;", "Ver productos comprados en una nota de venta"});

        data.add(new String[]{"Pedido", "pedido get", "Obtiene mis pedidos"});
        data.add(new String[]{"Pedido", "pedido get &lt;id&gt;", "Obtiene un pedido específico"});

        data.add(new String[]{"Dirección", "direccion get", "Obtiene todas las direcciones"});

        // Comando de Compra Completa
                        data.add(new String[]{"Compra", "comprar &lt;tipo_pago_id, url_google_maps&gt;", "Realiza compra completa desde carrito"});

                System.out.println("=== ENVIANDO RESPUESTA HELP ===");
                System.out.println("Filas de datos: " + data.size());
                System.out.println("Headers: " + headers.length);
                
                // Mostrar todos los comandos disponibles de manera organizada
                tableNotifySuccess(event.getSender(), "✅ **Comandos disponibles** - Acceso autorizado", headers, data);
                
                System.out.println("=== HELP ENVIADO EXITOSAMENTE ===");
            }
        } catch (Exception ex) {
            Logger.getLogger(EmailApp.class.getName()).log(Level.SEVERE, 
                "Error en método help para usuario: " + event.getSender(), ex);
            System.err.println("=== ERROR EN HELP ===");
            System.err.println("Usuario: " + event.getSender());
            System.err.println("Error: " + ex.getMessage());
            ex.printStackTrace();
            handleError(CONSTRAINTS_ERROR, event.getSender(), Collections.singletonList("Error en help: " + ex.getMessage()));
        }
    }

    @Override
    public void register(ParamsAction event) {
        try {
            switch (event.getAction()) {
                case Token.ADD:
                case Token.USER:
                    if (event.getParams() != null && event.getParams().size() >= 6) {
                        // Comando: register user <nombre, celular, email, genero, password, nit>
                        String nombre = event.getParams().get(0);
                        String celular = event.getParams().get(1);
                        String email = event.getParams().get(2);
                        String genero = event.getParams().get(3).toLowerCase(); // Normalizar a minúsculas
                        String password = event.getParams().get(4);
                        String nit = event.getParams().get(5);
                        
                        // Verificar que el email no exista ya
                        if (nUsuario.emailExists(email)) {
                            simpleNotify(event.getSender(), "Error de Registro", 
                                "❌ **El email ya está registrado en el sistema.**\n\n" +
                                "🔐 **Si ya tiene una cuenta, use el comando:**\n" +
                                "help get\n\n" +
                                "📧 **Si olvidó su contraseña, contacte al administrador:**\n" +
                                "• Email: admin@tecnoweb.org.bo");
                            return;
                        }
                        
                        // Registrar el usuario y cliente en una sola transacción
                        List<String[]> userData = nUsuario.registerUserAndCliente(nombre, celular, email, genero, password, nit);
                        
                        simpleNotifySuccess(event.getSender(), 
                            "✅ **Usuario y Cliente registrados exitosamente**\n\n" +
                            "📋 **Datos registrados:**\n" +
                            "• Nombre: " + nombre + "\n" +
                            "• Email: " + email + "\n" +
                            "• Celular: " + celular + "\n" +
                            "• Género: " + genero + " (normalizado)\n" +
                            "• NIT: " + nit + "\n\n" +
                            "🔐 **Ahora tiene acceso completo a todos los comandos del sistema.**\n\n" +
                            "📧 **Use el comando para ver todos los comandos disponibles:**\n" +
                            "help get");
                        
                    } else {
                        simpleNotify(event.getSender(), "Error de Registro", 
                            "❌ **Parámetros incorrectos.**\n\n" +
                            "📋 **Formato correcto:**\n" +
                            "register user &lt;nombre, celular, email, genero, password, nit&gt;\n\n" +
                            "📧 **Ejemplo:**\n" +
                            "register user Juan Pérez, 70012345, juan@email.com, masculino, miPassword123, 1234567890\n\n" +
                            "📝 **Nota:** El género debe ser en minúsculas: masculino, femenino, otro");
                    }
                    break;
                    
                case Token.MODIFY:
                    if (event.getParams() != null && event.getParams().size() >= 1) {
                        // Comando: register cliente <nit>
                        String nit = event.getParams().get(0);
                        String userEmail = event.getSender();
                        
                        // Verificar que el usuario existe
                        if (!nUsuario.emailExists(userEmail)) {
                            simpleNotify(event.getSender(), "Error de Registro", 
                                "❌ **Su email no está registrado en el sistema.**\n\n" +
                                "🔐 **Primero debe registrarse como usuario:**\n" +
                                "register user &lt;nombre, celular, email, genero, password, nit&gt;");
                            return;
                        }
                        
                        // Obtener el ID del usuario
                        List<String[]> userData = nUsuario.findByEmail(userEmail);
                        if (userData.isEmpty()) {
                            simpleNotify(event.getSender(), "Error de Registro", 
                                "❌ **Error al obtener datos del usuario.**");
                            return;
                        }
                        
                        int userId = Integer.parseInt(userData.get(0)[0]);
                        
                        // Verificar que no sea ya cliente
                        if (nUsuario.isCliente(userId)) {
                            simpleNotify(event.getSender(), "Error de Registro", 
                                "⚠️ **Ya está registrado como cliente.**\n\n" +
                                "🔐 **Use el comando para ver todos los comandos disponibles:**\n" +
                                "help get");
                            return;
                        }
                        
                        // Registrar como cliente
                        boolean success = nUsuario.registerCliente(userId, nit);
                        
                        if (success) {
                            simpleNotifySuccess(event.getSender(), 
                                "✅ **Cliente registrado exitosamente**\n\n" +
                                "📋 **Datos registrados:**\n" +
                                "• NIT: " + nit + "\n" +
                                "• Email: " + userEmail + "\n\n" +
                                "🔐 **Ahora tiene acceso completo a todos los comandos.**\n\n" +
                                "📧 **Use el comando para ver todos los comandos disponibles:**\n" +
                                "help get");
                        } else {
                            simpleNotify(event.getSender(), "Error de Registro", 
                                "❌ **Error al registrar como cliente.**\n\n" +
                                "📧 **Contacte al administrador:**\n" +
                                "• Email: admin@tecnoweb.org.bo");
                        }
                        
                    } else {
                        simpleNotify(event.getSender(), "Error de Registro", 
                            "❌ **Parámetros incorrectos.**\n\n" +
                            "📋 **Formato correcto:**\n" +
                            "register cliente &lt;nit&gt;\n\n" +
                            "📧 **Ejemplo:**\n" +
                            "register cliente 1234567890");
                    }
                    break;
                    
                default:
                    simpleNotify(event.getSender(), "Error de Registro", 
                        "❌ **Comando de registro no reconocido.**\n\n" +
                        "📋 **Comandos disponibles:**\n" +
                        "• register user &lt;nombre, celular, email, genero, password, nit&gt;\n" +
                        "• register cliente &lt;nit&gt;");
                    break;
            }
        } catch (SQLException ex) {
            Logger.getLogger(EmailApp.class.getName()).log(Level.SEVERE, 
                "Error en método register para usuario: " + event.getSender(), ex);
            handleError(CONSTRAINTS_ERROR, event.getSender(), Collections.singletonList("Error en registro: " + ex.getMessage()));
        } catch (Exception ex) {
            Logger.getLogger(EmailApp.class.getName()).log(Level.SEVERE, 
                "Error inesperado en método register para usuario: " + event.getSender(), ex);
            handleError(CONSTRAINTS_ERROR, event.getSender(), Collections.singletonList("Error inesperado en registro: " + ex.getMessage()));
        }
    }

    @Override
    public void categoria(ParamsAction event) {
        try {
            // Verificar si este comando debería ser interpretado como producto get categoria
            String command = event.getCommand();
            if (command != null && command.toLowerCase().contains("producto") && 
                command.toLowerCase().contains("categoria") && 
                event.getParams() != null && event.getParams().size() >= 1) {
                
                System.out.println("=== DETECTADO COMANDO PRODUCTO GET CATEGORIA ===");
                System.out.println("Comando original: " + command);
                System.out.println("Redirigiendo a producto get categoria...");
                
                // Redirigir al comando correcto
                try {
                    int categoriaId = Integer.parseInt(event.getParams().get(0));
                    List<String[]> productosData = nProducto.getByCategoria(categoriaId);
                    
                    if (!productosData.isEmpty()) {
                        System.out.println("Productos encontrados, procesando...");
                        
                        // Headers amigables para el cliente
                        String[] clienteHeaders = {"ID", "Código", "Producto", "Precio", "Descripción", "Categoría"};
                        
                        // Filtrar datos para el cliente
                        List<String[]> productosCliente = filtrarDatosParaCliente(productosData);
                        System.out.println("Productos filtrados para cliente: " + productosCliente.size());
                        
                        // Obtener nombre de la categoría para el título
                        String nombreCategoria = productosData.get(0)[7];
                        System.out.println("Nombre de categoría: " + nombreCategoria);
                        
                        tableNotifySuccess(event.getSender(), "📦 Productos - Categoría: " + nombreCategoria, 
                            clienteHeaders, (ArrayList<String[]>) productosCliente, event.getCommand());
                        
                        // Mensaje informativo específico para categoría
                        mostrarMensajeInformativo(event.getSender(), true);
                        
                    } else {
                        System.out.println("No se encontraron productos en la categoría " + categoriaId);
                        
                        // Obtener información de la categoría para mostrar un mensaje más informativo
                        try {
                            List<String[]> categoriaData = nCategoria.getById(categoriaId);
                            String nombreCategoria = "ID " + categoriaId;
                            if (!categoriaData.isEmpty()) {
                                nombreCategoria = categoriaData.get(0)[1]; // nombre de la categoría
                                System.out.println("Categoría encontrada: " + nombreCategoria);
                            }
                            
                            simpleNotify(event.getSender(), "Sin productos en esta categoría", 
                                "📦 **No hay productos registrados en la categoría:** " + nombreCategoria + "\n\n" +
                                "📋 **Para ver todas las categorías disponibles:**\n" +
                                "categoria get\n\n" +
                                "📋 **Para ver todos los productos:**\n" +
                                "producto get");
                        } catch (SQLException e) {
                            simpleNotify(event.getSender(), "Sin productos en esta categoría", 
                                "❌ **No hay productos registrados en la categoría con ID: " + categoriaId + "**\n\n" +
                                "📋 **Para ver todas las categorías disponibles:**\n" +
                                "categoria get");
                        }
                    }
                    return; // Salir sin procesar como comando de categoría
                    
                } catch (NumberFormatException e) {
                    simpleNotify(event.getSender(), "ID inválido", 
                        "❌ **El ID de categoría debe ser un número válido.**\n\n" +
                        "📋 **Uso correcto:** `producto get categoria <numero>`\n" +
                        "📝 **Ejemplo:** `producto get categoria 1`");
                    return;
                }
            }
            
            // Procesamiento normal del comando categoria
            switch (event.getAction()) {
                case Token.GET:
                    if (event.getParams() != null && event.getParams().size() >= 1) {
                        // Comando: categoria get <id>
                        int id = Integer.parseInt(event.getParams().get(0));
                        List<String[]> categoriaData = nCategoria.getById(id);
                        
                        if (!categoriaData.isEmpty()) {
                            tableNotifySuccess(event.getSender(), "Categoría encontrada", DCategoria.HEADERS, (ArrayList<String[]>) categoriaData, event.getCommand());
                        } else {
                            simpleNotify(event.getSender(), "Categoría no encontrada", 
                                "❌ **No se encontró la categoría con ID: " + id + "**");
                        }
                    } else {
                        // Comando: categoria get (todas las categorías)
                        List<String[]> categoriasData = nCategoria.getAll();
                        
                        if (!categoriasData.isEmpty()) {
                            tableNotifySuccess(event.getSender(), "Lista de Categorías", DCategoria.HEADERS, (ArrayList<String[]>) categoriasData, event.getCommand());
                        } else {
                            simpleNotify(event.getSender(), "No hay categorías", 
                                "📝 **No hay categorías registradas en el sistema.**");
                        }
                    }
                    break;
                    
                case Token.ADD:
                    if (event.getParams() != null && event.getParams().size() >= 2) {
                        // Comando: categoria add <nombre, descripcion>
                        String nombre = event.getParams().get(0);
                        String descripcion = event.getParams().get(1);
                        
                        List<String[]> categoriaData = nCategoria.save(nombre, descripcion, true, null);
                        tableNotifySuccess(event.getSender(), "Categoría guardada correctamente", DCategoria.HEADERS, (ArrayList<String[]>) categoriaData, event.getCommand());
                        
                    } else {
                        simpleNotify(event.getSender(), "Error de Parámetros", 
                            "❌ **Parámetros incorrectos.**\n\n" +
                            "📋 **Formato correcto:**\n" +
                            "categoria add &lt;nombre, descripcion&gt;\n\n" +
                            "📧 **Ejemplo:**\n" +
                            "categoria add Electrónicos, Productos electrónicos y tecnología");
                    }
                    break;
                    
                case Token.MODIFY:
                    if (event.getParams() != null && event.getParams().size() >= 3) {
                        // Comando: categoria modify <id, nombre, descripcion>
                        int id = Integer.parseInt(event.getParams().get(0));
                        String nombre = event.getParams().get(1);
                        String descripcion = event.getParams().get(2);
                        
                        List<String[]> categoriaData = nCategoria.update(id, nombre, descripcion, true, null);
                        tableNotifySuccess(event.getSender(), "Categoría modificada correctamente", DCategoria.HEADERS, (ArrayList<String[]>) categoriaData, event.getCommand());
                        
                    } else {
                        simpleNotify(event.getSender(), "Error de Parámetros", 
                            "❌ **Parámetros incorrectos.**\n\n" +
                            "📋 **Formato correcto:**\n" +
                            "categoria modify &lt;id, nombre, descripcion&gt;\n\n" +
                            "📧 **Ejemplo:**\n" +
                            "categoria modify 1, Electrónicos, Productos electrónicos y tecnología actualizada");
                    }
                    break;
                    
                case Token.DELETE:
                    if (event.getParams() != null && event.getParams().size() >= 1) {
                        // Comando: categoria delete <id>
                        int id = Integer.parseInt(event.getParams().get(0));
                        
                        boolean success = nCategoria.delete(id);
                        if (success) {
                            simpleNotifySuccess(event.getSender(), 
                                "✅ **Categoría eliminada exitosamente**\n\n" +
                                "🗑️ **ID eliminado:** " + id);
                        } else {
                            simpleNotify(event.getSender(), "Error al eliminar", 
                                "❌ **No se pudo eliminar la categoría con ID: " + id + "**\n\n" +
                                "📝 **Verifique que la categoría existe y no tiene productos asociados.**");
                        }
                        
                    } else {
                        simpleNotify(event.getSender(), "Error de Parámetros", 
                            "❌ **Parámetros incorrectos.**\n\n" +
                            "📋 **Formato correcto:**\n" +
                            "categoria delete &lt;id&gt;\n\n" +
                            "📧 **Ejemplo:**\n" +
                            "categoria delete 1");
                    }
                    break;
                    
                default:
                    simpleNotify(event.getSender(), "Comando no reconocido", 
                        "❌ **Comando de categoría no reconocido.**\n\n" +
                        "📋 **Comandos disponibles:**\n" +
                        "• categoria get\n" +
                        "• categoria get &lt;id&gt;\n" +
                        "• categoria add &lt;nombre, descripcion&gt;\n" +
                        "• categoria modify &lt;id, nombre, descripcion&gt;\n" +
                        "• categoria delete &lt;id&gt;");
                    break;
            }
        } catch (SQLException e) {
            Logger.getLogger(EmailApp.class.getName()).log(Level.SEVERE, "Error en método categoria para usuario: " + event.getSender(), e);
            simpleNotify(event.getSender(), "Error de Sistema", 
                "❌ **Se ha producido un error al procesar su solicitud.**\n\n" +
                "🔧 **Error en categoría:** " + e.getMessage());
        }
    }

    @Override
    public void producto(ParamsAction event) {
        try {
            switch (event.getAction()) {
                case Token.GET:
                    // Headers amigables para el cliente (sin precio_compra e imagen)
                    String[] clienteHeaders = {"ID", "Código", "Producto", "Precio", "Descripción", "Categoría"};
                    
                    if (event.getParams() != null && event.getParams().size() >= 2) {
                        String subcomando = event.getParams().get(0).toLowerCase();
                        
                        if ("id".equals(subcomando)) {
                            // Comando: producto get id <producto_id>
                            try {
                                int productoId = Integer.parseInt(event.getParams().get(1));
                                List<String[]> productoData = nProducto.getById(productoId);
                        
                        if (!productoData.isEmpty()) {
                                    // Filtrar datos para el cliente
                                    List<String[]> productosCliente = filtrarDatosParaCliente(productoData);
                                    
                                    tableNotifySuccess(event.getSender(), "📦 Producto ID: " + productoId, 
                                        clienteHeaders, (ArrayList<String[]>) productosCliente, event.getCommand());
                                    
                                    // Mensaje informativo para producto específico
                                    simpleNotify(event.getSender(), "💡 Consejo", 
                                        "🛒 **Para agregar este producto al carrito:**\n\n" +
                                        "📌 **Comando:** `carrito add " + productoId + ", <cantidad>`\n\n" +
                                        "📝 **Ejemplo:** `carrito add " + productoId + ", 2` (agrega 2 unidades)\n\n" +
                                        "🔙 **Ver más productos:**\n" +
                                        "• `producto get` - Ver catálogo completo\n" +
                                        "• `producto get categoria <categoria_id>` - Ver por categoría");
                                    
                        } else {
                            simpleNotify(event.getSender(), "Producto no encontrado", 
                                        "❌ **No se encontró el producto con ID: " + productoId + "**\n\n" +
                                        "🔍 **Sugerencias:**\n" +
                                        "• `producto get` - Ver todos los productos disponibles\n" +
                                        "• `categoria get` - Ver categorías disponibles");
                                }
                                
                            } catch (NumberFormatException e) {
                                simpleNotify(event.getSender(), "ID inválido", 
                                    "❌ **El ID del producto debe ser un número válido.**\n\n" +
                                    "📋 **Uso correcto:** `producto get id <numero>`\n" +
                                    "📝 **Ejemplo:** `producto get id 1`");
                            }
                            
                        } else if ("categoria".equals(subcomando)) {
                            // Comando: producto get categoria <categoria_id>
                            System.out.println("=== PROCESANDO PRODUCTO GET CATEGORIA ===");
                            System.out.println("Subcomando: " + subcomando);
                            System.out.println("Parámetros: " + event.getParams());
                            
                            try {
                                int categoriaId = Integer.parseInt(event.getParams().get(1));
                                System.out.println("Categoría ID: " + categoriaId);
                                
                                List<String[]> productosData = nProducto.getByCategoria(categoriaId);
                                System.out.println("Productos encontrados: " + productosData.size());
                                
                                if (!productosData.isEmpty()) {
                                    System.out.println("Productos encontrados, procesando...");
                                    
                                    // Filtrar datos para el cliente
                                    List<String[]> productosCliente = filtrarDatosParaCliente(productosData);
                                    System.out.println("Productos filtrados para cliente: " + productosCliente.size());
                                    
                                    // Obtener nombre de la categoría para el título
                                    String nombreCategoria = productosData.get(0)[7];
                                    System.out.println("Nombre de categoría: " + nombreCategoria);
                                    
                                    tableNotifySuccess(event.getSender(), "📦 Productos - Categoría: " + nombreCategoria, 
                                        clienteHeaders, (ArrayList<String[]>) productosCliente, event.getCommand());
                                    
                                    // Mensaje informativo específico para categoría
                                    mostrarMensajeInformativo(event.getSender(), true);
                                    
                                } else {
                                    System.out.println("No se encontraron productos en la categoría " + categoriaId);
                                    
                                    // Obtener información de la categoría para mostrar un mensaje más informativo
                                    try {
                                        List<String[]> categoriaData = nCategoria.getById(categoriaId);
                                        String nombreCategoria = "ID " + categoriaId;
                                        if (!categoriaData.isEmpty()) {
                                            nombreCategoria = categoriaData.get(0)[1]; // nombre de la categoría
                                            System.out.println("Categoría encontrada: " + nombreCategoria);
                                        }
                                        
                                        simpleNotify(event.getSender(), "Sin productos en esta categoría", 
                                            "📦 **No hay productos registrados en la categoría:** " + nombreCategoria + "\n\n" +
                                            "📋 **Para ver todas las categorías disponibles:**\n" +
                                            "categoria get\n\n" +
                                            "📋 **Para ver todos los productos:**\n" +
                                            "producto get");
                                    } catch (SQLException e) {
                                        simpleNotify(event.getSender(), "Sin productos en esta categoría", 
                                            "❌ **No hay productos registrados en la categoría con ID: " + categoriaId + "**\n\n" +
                                            "📋 **Para ver todas las categorías disponibles:**\n" +
                                            "categoria get");
                                    }
                                }
                                
                            } catch (NumberFormatException e) {
                                simpleNotify(event.getSender(), "ID inválido", 
                                    "❌ **El ID de categoría debe ser un número válido.**\n\n" +
                                    "📋 **Uso correcto:** `producto get categoria <numero>`\n" +
                                    "📝 **Ejemplo:** `producto get categoria 1`");
                            }
                            
                        } else {
                            // Comando no reconocido
                            simpleNotify(event.getSender(), "Comando no reconocido", 
                                "❌ **Subcomando no válido: " + subcomando + "**\n\n" +
                                "📋 **Comandos disponibles:**\n" +
                                "• `producto get` - Ver todos los productos\n" +
                                "• `producto get id <producto_id>` - Ver producto específico\n" +
                                "• `producto get categoria <categoria_id>` - Ver productos por categoría\n\n" +
                                "💡 **Para ver categorías:** `categoria get`");
                        }
                        
                    } else if (event.getParams() != null && event.getParams().size() == 1) {
                        // Comando: producto get <numero> (ID directo)
                        String param = event.getParams().get(0);
                        try {
                            int productoId = Integer.parseInt(param);
                            List<String[]> productoData = nProducto.getById(productoId);
                            
                            if (!productoData.isEmpty()) {
                                // Filtrar datos para el cliente
                                List<String[]> productosCliente = filtrarDatosParaCliente(productoData);
                                
                                tableNotifySuccess(event.getSender(), "📦 Producto ID: " + productoId, 
                                    clienteHeaders, (ArrayList<String[]>) productosCliente, event.getCommand());
                                
                                // Mensaje informativo para producto específico
                                simpleNotify(event.getSender(), "💡 Consejo", 
                                    "🛒 **Para agregar este producto al carrito:**\n\n" +
                                    "📌 **Comando:** `carrito add " + productoId + ", <cantidad>`\n\n" +
                                    "📝 **Ejemplo:** `carrito add " + productoId + ", 2` (agrega 2 unidades)\n\n" +
                                    "🔙 **Ver más productos:**\n" +
                                    "• `producto get` - Ver catálogo completo\n" +
                                    "• `producto get categoria <categoria_id>` - Ver por categoría");
                                
                            } else {
                                simpleNotify(event.getSender(), "Producto no encontrado", 
                                    "❌ **No se encontró el producto con ID: " + productoId + "**\n\n" +
                                    "🔍 **Sugerencias:**\n" +
                                    "• `producto get` - Ver todos los productos disponibles\n" +
                                    "• `categoria get` - Ver categorías disponibles");
                            }
                            
                        } catch (NumberFormatException e) {
                            // Si no es un número, mostrar ayuda
                            simpleNotify(event.getSender(), "Parámetros incorrectos", 
                                "❌ **Parámetro no reconocido: " + param + "**\n\n" +
                                "📋 **Uso correcto:**\n" +
                                "• `producto get` - Ver todos los productos\n" +
                                "• `producto get <numero>` - Ver producto específico\n" +
                                "• `producto get id <producto_id>` - Ver producto específico\n" +
                                "• `producto get categoria <categoria_id>` - Ver productos por categoría\n\n" +
                                "📝 **Ejemplos:**\n" +
                                "• `producto get 1`\n" +
                                "• `producto get id 1`\n" +
                                "• `producto get categoria 2`");
                        }
                            
                    } else {
                        // Comando: producto get (todos los productos)
                        List<String[]> productosData = nProducto.getAll();
                        
                        if (!productosData.isEmpty()) {
                            // Filtrar datos para el cliente (quitar precio_compra e imagen)
                            List<String[]> productosCliente = filtrarDatosParaCliente(productosData);
                            
                            tableNotifySuccess(event.getSender(), "📦 Catálogo de Productos", 
                                clienteHeaders, (ArrayList<String[]>) productosCliente, event.getCommand());
                            
                            // Mensaje informativo general
                            mostrarMensajeInformativo(event.getSender(), false);
                            
                        } else {
                            simpleNotify(event.getSender(), "No hay productos", 
                                "📝 **No hay productos registrados en el sistema.**");
                        }
                    }
                    break;
                    
                case Token.ADD:
                    if (event.getParams() != null && event.getParams().size() >= 7) {
                        // Comando: producto add <nombre, precio_unitario, imagen, descripcion, categoria_id, stock, stock_minimo>
                        // El tiempo se calcula automáticamente
                        String nombre = event.getParams().get(0);
                        double precioUnitario = Double.parseDouble(event.getParams().get(1));
                        String imagen = event.getParams().get(2);
                        String descripcion = event.getParams().get(3);
                        int categoriaId = Integer.parseInt(event.getParams().get(4));
                        int stock = Integer.parseInt(event.getParams().get(5));
                        Integer stockMinimo = Integer.parseInt(event.getParams().get(6));
                        String tiempo = "Disponible"; // Valor por defecto
                        
                        List<String[]> productoData = nProducto.save(nombre, precioUnitario, imagen, descripcion, categoriaId, stock, stockMinimo, tiempo);
                        tableNotifySuccess(event.getSender(), "Producto guardado correctamente", DProducto.HEADERS, (ArrayList<String[]>) productoData, event.getCommand());
                        
                    } else {
                        simpleNotify(event.getSender(), "Error de Parámetros", 
                            "❌ **Parámetros incorrectos.**\n\n" +
                            "📋 **Formato correcto:**\n" +
                            "producto add &lt;nombre, precio_unitario, imagen, descripcion, categoria_id, stock, stock_minimo&gt;\n\n" +
                            "📧 **Ejemplo:**\n" +
                            "producto add Laptop HP, 1200.00, laptop.jpg, Laptop HP Pavilion, 1, 10, 2");
                    }
                    break;
                    
                case Token.MODIFY:
                    if (event.getParams() != null && event.getParams().size() >= 8) {
                        // Comando: producto modify <id, nombre, precio_unitario, imagen, descripcion, categoria_id, stock, stock_minimo>
                        int id = Integer.parseInt(event.getParams().get(0));
                        String nombre = event.getParams().get(1);
                        double precioUnitario = Double.parseDouble(event.getParams().get(2));
                        String imagen = event.getParams().get(3);
                        String descripcion = event.getParams().get(4);
                        int categoriaId = Integer.parseInt(event.getParams().get(5));
                        int stock = Integer.parseInt(event.getParams().get(6));
                        Integer stockMinimo = Integer.parseInt(event.getParams().get(7));
                        String tiempo = "Disponible"; // Valor por defecto
                        
                        List<String[]> productoData = nProducto.update(id, nombre, precioUnitario, imagen, descripcion, categoriaId, stock, stockMinimo, tiempo);
                        tableNotifySuccess(event.getSender(), "Producto modificado correctamente", DProducto.HEADERS, (ArrayList<String[]>) productoData, event.getCommand());
                        
                    } else {
                        simpleNotify(event.getSender(), "Error de Parámetros", 
                            "❌ **Parámetros incorrectos.**\n\n" +
                            "📋 **Formato correcto:**\n" +
                            "producto modify &lt;id, nombre, precio_unitario, imagen, descripcion, categoria_id, stock, stock_minimo&gt;\n\n" +
                            "📧 **Ejemplo:**\n" +
                            "producto modify 1, Laptop HP Actualizada, 1250.00, laptop_new.jpg, Laptop HP Pavilion actualizada, 1, 8, 2");
                    }
                    break;
                    
                case Token.DELETE:
                    if (event.getParams() != null && event.getParams().size() >= 1) {
                        // Comando: producto delete <id>
                        int id = Integer.parseInt(event.getParams().get(0));
                        
                        boolean success = nProducto.delete(id);
                        if (success) {
                            simpleNotifySuccess(event.getSender(), 
                                "✅ **Producto eliminado exitosamente**\n\n" +
                                "🗑️ **ID eliminado:** " + id);
                        } else {
                            simpleNotify(event.getSender(), "Error al eliminar", 
                                "❌ **No se pudo eliminar el producto con ID: " + id + "**\n\n" +
                                "📝 **Verifique que el producto existe.**");
                        }
                        
                    } else {
                        simpleNotify(event.getSender(), "Error de Parámetros", 
                            "❌ **Parámetros incorrectos.**\n\n" +
                            "📋 **Formato correcto:**\n" +
                            "producto delete &lt;id&gt;\n\n" +
                            "📧 **Ejemplo:**\n" +
                            "producto delete 1");
                    }
                    break;
                
                case Token.ID: // Manejar el caso donde "id" se interpreta como acción (producto get id <numero>)
                    // Headers amigables para el cliente (sin precio_compra e imagen)
                    String[] clienteHeadersId = {"ID", "Código", "Producto", "Precio", "Descripción", "Categoría"};
                    
                    if (event.getParams() != null && event.getParams().size() >= 1) {
                        // Comando: producto get id <producto_id> (donde "id" es la acción)
                        try {
                            int productoId = Integer.parseInt(event.getParams().get(0));
                            List<String[]> productoData = nProducto.getById(productoId);
                            
                            if (!productoData.isEmpty()) {
                                // Filtrar datos para el cliente
                                List<String[]> productosCliente = filtrarDatosParaCliente(productoData);
                                
                                tableNotifySuccess(event.getSender(), "📦 Producto ID: " + productoId, 
                                    clienteHeadersId, (ArrayList<String[]>) productosCliente, event.getCommand());
                                
                                // Mensaje informativo para producto específico
                                simpleNotify(event.getSender(), "💡 Consejo", 
                                    "🛒 **Para agregar este producto al carrito:**\n\n" +
                                    "📌 **Comando:** `carrito add " + productoId + ", <cantidad>`\n\n" +
                                    "📝 **Ejemplo:** `carrito add " + productoId + ", 2` (agrega 2 unidades)\n\n" +
                                    "🔙 **Ver más productos:**\n" +
                                    "• `producto get` - Ver catálogo completo\n" +
                                    "• `producto get categoria <categoria_id>` - Ver por categoría");
                                
                            } else {
                                simpleNotify(event.getSender(), "Producto no encontrado", 
                                    "❌ **No se encontró el producto con ID: " + productoId + "**\n\n" +
                                    "🔍 **Sugerencias:**\n" +
                                    "• `producto get` - Ver todos los productos disponibles\n" +
                                    "• `categoria get` - Ver categorías disponibles");
                            }
                            
                        } catch (NumberFormatException e) {
                            simpleNotify(event.getSender(), "ID inválido", 
                                "❌ **El ID del producto debe ser un número válido.**\n\n" +
                                "📋 **Uso correcto:** `producto get id <numero>`\n" +
                                "📝 **Ejemplo:** `producto get id 1`");
                        }
                    } else {
                        simpleNotify(event.getSender(), "Parámetros incompletos", 
                            "❌ **Faltan parámetros.**\n\n" +
                            "📋 **Uso correcto:**\n" +
                            "• `producto get` - Ver todos los productos\n" +
                            "• `producto get id <producto_id>` - Ver producto específico\n" +
                            "• `producto get categoria <categoria_id>` - Ver productos por categoría\n\n" +
                            "📝 **Ejemplos:**\n" +
                            "• `producto get id 1`\n" +
                            "• `producto get categoria 2`");
                    }
                    break;
                    
                default:
                    simpleNotify(event.getSender(), "Comando no reconocido", 
                        "❌ **Comando de producto no reconocido.**\n\n" +
                        "📋 **Comandos disponibles:**\n" +
                        "• producto get\n" +
                        "• producto get &lt;id&gt;\n" +
                        "• producto add &lt;cod_producto, nombre, precio_compra, precio_venta, imagen, descripcion, categoria_id&gt;\n" +
                        "• producto modify &lt;id, cod_producto, nombre, precio_compra, precio_venta, imagen, descripcion, categoria_id&gt;\n" +
                        "• producto delete &lt;id&gt;");
                    break;
            }
        } catch (SQLException e) {
            Logger.getLogger(EmailApp.class.getName()).log(Level.SEVERE, "Error en método producto para usuario: " + event.getSender(), e);
            simpleNotify(event.getSender(), "Error de Sistema", 
                "❌ **Se ha producido un error al procesar su solicitud.**\n\n" +
                "🔧 **Error en producto:** " + e.getMessage());
        }
    }

    @Override
    public void metodoPago(ParamsAction event) {
        try {
            switch (event.getAction()) {
                case Token.GET:
                    if (event.getParams() != null && event.getParams().size() >= 1) {
                        // Comando: metodoPago get <id>
                        int id = Integer.parseInt(event.getParams().get(0));
                        List<String[]> metodoPagoData = nMetodoPago.getById(id);

                        if (!metodoPagoData.isEmpty()) {
                            tableNotifySuccess(event.getSender(), "Método de Pago encontrado", DMetodoPago.HEADERS, (ArrayList<String[]>) metodoPagoData, event.getCommand());
                        } else {
                            simpleNotify(event.getSender(), "Método de Pago no encontrado", 
                                "❌ **No se encontró el método de pago con ID: " + id + "**");
                        }
                    } else {
                        // Comando: metodoPago get (todos los métodos de pago)
                        List<String[]> metodoPagosData = nMetodoPago.getAll();

                        if (!metodoPagosData.isEmpty()) {
                            tableNotifySuccess(event.getSender(), "Lista de Métodos de Pago", DMetodoPago.HEADERS, (ArrayList<String[]>) metodoPagosData, event.getCommand());
                        } else {
                            simpleNotify(event.getSender(), "No hay tipos de pago", 
                                "📝 **No hay métodos de pago registrados en el sistema.**");
                        }
                    }
                    break;
                    
                case Token.ADD:
                    if (event.getParams() != null && event.getParams().size() >= 2) {
                        // Comando: metodoPago add <nombre, descripcion>
                        String nombre = event.getParams().get(0);
                        String descripcion = event.getParams().get(1);
                        List<String[]> metodoPagoData = nMetodoPago.save(nombre, descripcion);
                        tableNotifySuccess(event.getSender(), "Método de Pago guardado correctamente", DMetodoPago.HEADERS, (ArrayList<String[]>) metodoPagoData, event.getCommand());

                    } else {
                        simpleNotify(event.getSender(), "Error de Parámetros", 
                            "❌ **Parámetros incorrectos.**\n\n" +
                            "📋 **Formato correcto:**\n" +
                            "metodoPago add &lt;nombre, descripcion&gt;\n\n" +
                            "📧 **Ejemplo:**\n" +
                            "metodoPago add Efectivo, Pago en efectivo");
                    }
                    break;
                    
                case Token.MODIFY:
                    if (event.getParams() != null && event.getParams().size() >= 3) {
                        // Comando: tipopago modify <id, nombre, descripcion>
                        int id = Integer.parseInt(event.getParams().get(0));
                        String nombre = event.getParams().get(1);
                        String descripcion = event.getParams().get(2);

                        List<String[]> metodoPagoData = nMetodoPago.update(id, nombre, descripcion);
                        tableNotifySuccess(event.getSender(), "Método de Pago modificado correctamente", DMetodoPago.HEADERS, (ArrayList<String[]>) metodoPagoData, event.getCommand());

                    } else {
                        simpleNotify(event.getSender(), "Error de Parámetros", 
                            "❌ **Parámetros incorrectos.**\n\n" +
                            "📋 **Formato correcto:**\n" +
                            "metodoPago modify &lt;id, nombre, descripcion&gt;\n\n" +
                            "📧 **Ejemplo:**\n" +
                            "metodoPago modify 1, Efectivo, Pago en efectivo");
                    }
                    break;
                    
                case Token.DELETE:
                    if (event.getParams() != null && event.getParams().size() >= 1) {
                        // Comando: tipopago delete <id>
                        int id = Integer.parseInt(event.getParams().get(0));
                        
                        boolean success = nMetodoPago.delete(id);
                        if (success) {
                            simpleNotifySuccess(event.getSender(), 
                                "✅ **Método de Pago eliminado exitosamente**\n\n" +
                                "🗑️ **ID eliminado:** " + id);
                        } else {
                            simpleNotify(event.getSender(), "Error al eliminar", 
                                "❌ **No se pudo eliminar el tipo de pago con ID: " + id + "**\n\n" +
                                "📝 **Verifique que el tipo de pago existe.**");
                        }
                        
                    } else {
                        simpleNotify(event.getSender(), "Error de Parámetros", 
                            "❌ **Parámetros incorrectos.**\n\n" +
                            "📋 **Formato correcto:**\n" +
                            "tipopago delete &lt;id&gt;\n\n" +
                            "📧 **Ejemplo:**\n" +
                            "tipopago delete 1");
                    }
                    break;
                    
                default:
                    simpleNotify(event.getSender(), "Comando no reconocido", 
                        "❌ **Comando de tipopago no reconocido.**\n\n" +
                        "📋 **Comandos disponibles:**\n" +
                        "• tipopago get\n" +
                        "• tipopago get &lt;id&gt;\n" +
                        "• tipopago add &lt;nombre, descripcion&gt;\n" +
                        "• tipopago modify &lt;id, nombre, descripcion&gt;\n" +
                        "• tipopago delete &lt;id&gt;\n" +
                        "• **Nota:** Este comando gestiona los métodos de pago.");
                    break;
            }
        } catch (SQLException e) {
            Logger.getLogger(EmailApp.class.getName()).log(Level.SEVERE, "Error en método tipopago para usuario: " + event.getSender(), e);
            simpleNotify(event.getSender(), "Error de Sistema", 
                "❌ **Se ha producido un error al procesar su solicitud.**\n\n" +
                "🔧 **Error en tipopago:** " + e.getMessage());
        }
    }


    @Override
    public void onReceiptEmail(List<Email> emails) {
        for (Email email : emails) {
            System.out.println("=== PROCESANDO EMAIL ===");
            System.out.println("From: " + email.getFrom());
            System.out.println("Subject: " + email.getSubject());
            System.out.println("Message: " + email.getMessage());
            
            Interpreter interpreter = new Interpreter(email.getSubject(), email.getFrom());
            interpreter.setCasoUsoListener(this);
            Thread thread = new Thread(interpreter);
            thread.start();
        }
    }

    private void handleError(int type, String email, List<String> args) {
        Email emailObject = new Email(email, "❌ Error de Sistema",
                HtmlRes.generateErrorMessage("Error de Sistema", 
                    "Se ha producido un error al procesar su solicitud." + 
                    (args != null && !args.isEmpty() ? "\n\n" + args.get(0) : "")));
        sendEmail(emailObject);
    }

    private void simpleNotifySuccess(String email, String message) {
        Email emailObject = new Email(email, "✅ Operación Exitosa",
                HtmlRes.generateSuccessMessage("Operación Exitosa", message));
        sendEmail(emailObject);
    }

    private void tableNotifySuccess(String email, String title, String[] headers, ArrayList<String[]> data) {
        System.out.println("=== TABLE NOTIFY SUCCESS ===");
        System.out.println("Email: " + email);
        System.out.println("Title: " + title);
        System.out.println("Headers count: " + headers.length);
        System.out.println("Data rows: " + data.size());
        
        try {
            Email emailObject = new Email(email, Email.SUBJECT,
                    HtmlRes.generateTable(title, headers, data));
            System.out.println("Email object created successfully");
            sendEmail(emailObject);
            System.out.println("Email sent successfully");
        } catch (Exception ex) {
            System.err.println("Error in tableNotifySuccess: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    private void tableNotifySuccess(String email, String title, String[] headers, ArrayList<String[]> data, String command) {
        System.out.println("=== TABLE NOTIFY SUCCESS (WITH COMMAND) ===");
        System.out.println("Email: " + email);
        System.out.println("Title: " + title);
        System.out.println("Command: " + command);
        System.out.println("Headers count: " + headers.length);
        System.out.println("Data rows: " + data.size());
        
        try {
            Email emailObject = new Email(email, command,
                    HtmlRes.generateTable(title, headers, data));
            System.out.println("Email object created successfully");
            sendEmail(emailObject);
            System.out.println("Email sent successfully");
        } catch (Exception ex) {
            System.err.println("Error in tableNotifySuccess with command: " + ex.getMessage());
            ex.printStackTrace();
        }
    }

    private void sendEmail(Email email) {
        EmailSend sendEmail = new EmailSend(email);
        Thread thread = new Thread(sendEmail);
        thread.start();
    }

    private void simpleNotify(String email, String title, String message) {
        Email emailObject = new Email(email, title,
                HtmlRes.generateText(new String[]{
                        title,
                        message
                }));
        sendEmail(emailObject);
    }

    /* MÉTODO CLIENTE COMENTADO - TABLA NO EXISTE EN BD (solo usuario con rol)
    @Override
    public void cliente(ParamsAction event) {
        try {
            switch (event.getAction()) {
                case Token.GET:
                    if (event.getParams() != null && event.getParams().size() >= 1) {
                        String param = event.getParams().get(0);
                        
                        // Intentar como ID numérico primero
                        try {
                            int id = Integer.parseInt(param);
                            List<String[]> clienteData = nCliente.getById(id);
                            
                            if (!clienteData.isEmpty()) {
                                String[] enhancedHeaders = {"ID", "NIT", "User ID", "Nombre", "Email", "Celular", "Género"};
                                tableNotifySuccess(event.getSender(), "Cliente encontrado", enhancedHeaders, (ArrayList<String[]>) clienteData, event.getCommand());
                            } else {
                                simpleNotify(event.getSender(), "Cliente no encontrado", 
                                    "❌ **No se encontró el cliente con ID: " + id + "**");
                            }
                        } catch (NumberFormatException e) {
                            // Si no es número, intentar como NIT
                            List<String[]> clienteData = nCliente.getByNit(param);
                            
                            if (!clienteData.isEmpty()) {
                                String[] enhancedHeaders = {"ID", "NIT", "User ID", "Nombre", "Email", "Celular", "Género"};
                                tableNotifySuccess(event.getSender(), "Cliente encontrado por NIT", enhancedHeaders, (ArrayList<String[]>) clienteData, event.getCommand());
                            } else {
                                simpleNotify(event.getSender(), "Cliente no encontrado", 
                                    "❌ **No se encontró el cliente con NIT: " + param + "**");
                            }
                        }
                    } else {
                        // Comando: cliente get (todos los clientes)
                        List<String[]> clientesData = nCliente.getAll();
                        
                        if (!clientesData.isEmpty()) {
                            String[] enhancedHeaders = {"ID", "NIT", "User ID", "Nombre", "Email", "Celular", "Género"};
                            tableNotifySuccess(event.getSender(), "Lista de Clientes", enhancedHeaders, (ArrayList<String[]>) clientesData, event.getCommand());
                        } else {
                            simpleNotify(event.getSender(), "No hay clientes", 
                                "📝 **No hay clientes registrados en el sistema.**");
                        }
                    }
                    break;
                    
                case Token.ADD:
                    if (event.getParams() != null && event.getParams().size() >= 2) {
                        // Comando: cliente add <nit, user_id>
                        String nit = event.getParams().get(0);
                        int userId = Integer.parseInt(event.getParams().get(1));
                        
                        List<String[]> clienteData = nCliente.save(nit, userId);
                        tableNotifySuccess(event.getSender(), "Cliente guardado correctamente", DCliente.HEADERS, (ArrayList<String[]>) clienteData, event.getCommand());
                        
                    } else {
                        simpleNotify(event.getSender(), "Error de Parámetros", 
                            "❌ **Parámetros incorrectos.**\n\n" +
                            "📋 **Formato correcto:**\n" +
                            "cliente add &lt;nit, user_id&gt;\n\n" +
                            "📧 **Ejemplo:**\n" +
                            "cliente add 1234567890, 1");
                    }
                    break;
                    
                case Token.MODIFY:
                    if (event.getParams() != null && event.getParams().size() >= 2) {
                        // Comando: cliente modify <id, nit>
                        int id = Integer.parseInt(event.getParams().get(0));
                        String nit = event.getParams().get(1);
                        
                        List<String[]> clienteData = nCliente.update(id, nit);
                        tableNotifySuccess(event.getSender(), "Cliente modificado correctamente", DCliente.HEADERS, (ArrayList<String[]>) clienteData, event.getCommand());
                        
                    } else {
                        simpleNotify(event.getSender(), "Error de Parámetros", 
                            "❌ **Parámetros incorrectos.**\n\n" +
                            "📋 **Formato correcto:**\n" +
                            "cliente modify &lt;id, nit&gt;\n\n" +
                            "📧 **Ejemplo:**\n" +
                            "cliente modify 1, 9876543210");
                    }
                    break;
                    
                case Token.DELETE:
                    if (event.getParams() != null && event.getParams().size() >= 1) {
                        // Comando: cliente delete <id>
                        int id = Integer.parseInt(event.getParams().get(0));
                        
                        boolean success = nCliente.delete(id);
                        if (success) {
                            simpleNotifySuccess(event.getSender(), 
                                "✅ **Cliente eliminado exitosamente**\n\n" +
                                "🗑️ **ID eliminado:** " + id);
                        } else {
                            simpleNotify(event.getSender(), "Error al eliminar", 
                                "❌ **No se pudo eliminar el cliente con ID: " + id + "**\n\n" +
                                "📝 **Verifique que el cliente existe.**");
                        }
                        
                    } else {
                        simpleNotify(event.getSender(), "Error de Parámetros", 
                            "❌ **Parámetros incorrectos.**\n\n" +
                            "📋 **Formato correcto:**\n" +
                            "cliente delete &lt;id&gt;\n\n" +
                            "📧 **Ejemplo:**\n" +
                            "cliente delete 1");
                    }
                    break;
                    
                default:
                    simpleNotify(event.getSender(), "Comando no reconocido", 
                        "❌ **Comando de cliente no reconocido.**\n\n" +
                        "📋 **Comandos disponibles:**\n" +
                        "• cliente get\n" +
                        "• cliente get &lt;id&gt;\n" +
                        "• cliente get &lt;nit&gt;\n" +
                        "• cliente add &lt;nit, user_id&gt;\n" +
                        "• cliente modify &lt;id, nit&gt;\n" +
                        "• cliente delete &lt;id&gt;");
                    break;
            }
        } catch (SQLException e) {
            Logger.getLogger(EmailApp.class.getName()).log(Level.SEVERE, "Error en método cliente para usuario: " + event.getSender(), e);
            simpleNotify(event.getSender(), "Error de Sistema", 
                "❌ **Se ha producido un error al procesar su solicitud.**\n\n" +
                "🔧 **Error en cliente:** " + e.getMessage());
        }
    }
    FIN COMENTARIO MÉTODO CLIENTE */

    @Override
    public void notaVenta(ParamsAction event) {
        try {
            System.out.println("=== PROCESANDO NOTA VENTA PARA USUARIO ===");
            System.out.println("Usuario: " + event.getSender());
            System.out.println("Action: " + event.getAction());
            System.out.println("Params: " + event.getParams());
            
            // Comando especial: notaventa productos <id>
            if (event.getParams() != null && event.getParams().size() >= 2 && 
                "productos".equals(event.getParams().get(0).toLowerCase())) {
                
                try {
                    int notaVentaId = Integer.parseInt(event.getParams().get(1));
                    
                    // Verificar que la nota de venta existe y pertenece al usuario
                    List<String[]> notaVentaData = nNotaVenta.getById(notaVentaId);
                    if (notaVentaData.isEmpty()) {
                        simpleNotify(event.getSender(), "Nota de venta no encontrada", 
                            "❌ **No se encontró la nota de venta con ID: " + notaVentaId + "**");
                        return;
                    }
                    
                    // Obtener productos de la nota de venta
                    DDetalleVenta dDetalleVenta = new DDetalleVenta();
                    List<String[]> productosData = dDetalleVenta.getByNotaVentaId(notaVentaId);
                    
                    if (!productosData.isEmpty()) {
                        // Headers amigables para productos de nota de venta
                        String[] productosHeaders = {"Detalle ID", "Producto ID", "Cantidad", "Total", "Producto", "Descripción", "Precio Unitario", "Stock"};
                        
                        // Filtrar datos para el cliente (quitar nota_venta_id)
                        List<String[]> productosCliente = new ArrayList<>();
                        for (String[] producto : productosData) {
                            productosCliente.add(new String[]{
                                producto[0], // id → Detalle ID
                                producto[2], // producto_almacen_id → Producto ID
                                producto[3], // cantidad → Cantidad
                                producto[4], // total → Total
                                producto[5], // producto_nombre → Producto
                                producto[6], // producto_descripcion → Descripción
                                producto[7], // precio_venta → Precio Unitario
                                producto[8]  // stock → Stock
                            });
                        }
                        
                        tableNotifySuccess(event.getSender(), "📦 Productos - Nota de Venta ID: " + notaVentaId, 
                            productosHeaders, (ArrayList<String[]>) productosCliente, event.getCommand());
                        
                        // Información adicional sobre la nota de venta
                        String[] notaInfo = notaVentaData.get(0);
                        simpleNotify(event.getSender(), "ℹ️ Detalles de la Compra", 
                            "📋 **Información de la nota de venta:**\n\n" +
                            "🆔 **ID:** " + notaInfo[0] + "\n" +
                            "📅 **Fecha:** " + notaInfo[3] + "\n" +
                            "💰 **Total:** Bs" + notaInfo[4] + "\n" +
                            "📋 **Estado:** " + notaInfo[5] + "\n" +
                            "📝 **Observaciones:** " + notaInfo[6] + "\n\n" +
                            "🔙 **Comandos relacionados:**\n" +
                            "• `notaventa get " + notaVentaId + "` - Ver detalles completos\n" +
                            "• `notaventa get` - Ver todas mis notas de venta");
                        
                    } else {
                        simpleNotify(event.getSender(), "Sin productos", 
                            "❌ **Esta nota de venta no tiene productos registrados.**");
                    }
                    
                } catch (NumberFormatException e) {
                    simpleNotify(event.getSender(), "ID inválido", 
                        "❌ **El ID de la nota de venta debe ser un número válido.**\n\n" +
                        "📋 **Uso correcto:** `notaventa productos <numero>`\n" +
                        "📝 **Ejemplo:** `notaventa productos 1`");
                }
                
                return; // Salir del método después de manejar el comando productos
            }
            
            switch (event.getAction()) {
                case Token.GET:
                    if (event.getParams() != null && event.getParams().size() >= 1) {
                        String param = event.getParams().get(0);
                        
                        // Intentar como ID numérico primero
                        try {
                            int id = Integer.parseInt(param);
                            List<String[]> notaVentaData = nNotaVenta.getById(id);
                            
                            if (!notaVentaData.isEmpty()) {
                                // Headers amigables para el cliente (sin cliente_id)
                                String[] clienteHeaders = {"ID", "Pedido ID", "Fecha", "Total", "Estado", "Observaciones", "NIT", "Cliente", "Email"};
                                
                                // Filtrar datos para el cliente (quitar cliente_id)
                                List<String[]> notaVentaCliente = new ArrayList<>();
                                for (String[] nota : notaVentaData) {
                                    notaVentaCliente.add(new String[]{
                                        nota[0], // id → ID
                                        nota[2], // pedido_id → Pedido ID  
                                        nota[3], // fecha → Fecha
                                        nota[4], // total → Total
                                        nota[5], // estado → Estado
                                        nota[6], // observaciones → Observaciones
                                        nota[7], // nit → NIT
                                        nota[8], // nombre → Cliente
                                        nota[9]  // email → Email
                                    });
                                }
                                
                                tableNotifySuccess(event.getSender(), "🧾 Nota de Venta ID: " + id, clienteHeaders, (ArrayList<String[]>) notaVentaCliente, event.getCommand());
                                
                                // Agregar mensaje informativo sobre ver productos
                                simpleNotify(event.getSender(), "💡 Información Adicional", 
                                    "📦 **Para ver los productos comprados en esta nota de venta:**\n\n" +
                                    "📌 **Comando:** `notaventa productos " + id + "`\n\n" +
                                    "ℹ️ **Otros comandos útiles:**\n" +
                                    "• `notaventa get` - Ver todas mis notas de venta\n" +
                                    "• `pedido get` - Ver mis pedidos");
                                
                            } else {
                                simpleNotify(event.getSender(), "Nota de Venta no encontrada", 
                                    "❌ **No se encontró la nota de venta con ID: " + id + "**");
                            }
                        } catch (NumberFormatException e) {
                            simpleNotify(event.getSender(), "Error de formato", 
                                "❌ **El ID debe ser un número válido**");
                        }
                    } else {
                        // Obtener todas las notas de venta del usuario
                        List<String[]> notasVenta = nNotaVenta.getByUsuarioEmail(event.getSender());
                        
                        if (!notasVenta.isEmpty()) {
                            // Headers amigables para el cliente (sin cliente_id)
                            String[] clienteHeaders = {"ID", "Pedido ID", "Fecha", "Total", "Estado", "Observaciones", "NIT", "Cliente", "Email"};
                            
                            // Filtrar datos para el cliente (quitar cliente_id)
                            List<String[]> notasVentaCliente = new ArrayList<>();
                            for (String[] nota : notasVenta) {
                                notasVentaCliente.add(new String[]{
                                    nota[0], // id → ID
                                    nota[2], // pedido_id → Pedido ID  
                                    nota[3], // fecha → Fecha
                                    nota[4], // total → Total
                                    nota[5], // estado → Estado
                                    nota[6], // observaciones → Observaciones
                                    nota[7], // nit → NIT
                                    nota[8], // nombre → Cliente
                                    nota[9]  // email → Email
                                });
                            }
                            
                            tableNotifySuccess(event.getSender(), "🧾 Mis Notas de Venta", clienteHeaders, (ArrayList<String[]>) notasVentaCliente, event.getCommand());
                            
                            // Mensaje informativo sobre ver productos de cada nota
                            simpleNotify(event.getSender(), "💡 Consejo", 
                                "📦 **Para ver los productos de una nota de venta específica:**\n\n" +
                                "📌 **Comando:** `notaventa productos <id>`\n\n" +
                                "📝 **Ejemplo:** `notaventa productos 1` (ve productos de la nota ID 1)\n\n" +
                                "ℹ️ **Otros comandos útiles:**\n" +
                                "• `notaventa get <id>` - Ver detalles de una nota específica\n" +
                                "• `pedido get` - Ver mis pedidos");
                        } else {
                            simpleNotify(event.getSender(), "Sin notas de venta", 
                                "📋 **No tienes notas de venta registradas**\n\n" +
                                "🛒 **Para crear una compra:**\n" +
                                "1. Agrega productos al carrito: `carrito add <producto_id, cantidad>`\n" +
                                "2. Realiza la compra: `comprar <tipo_pago_id, url_google_maps>`");
                        }
                    }
                    break;
                    
                case Token.ADD:
                    // Crear nota de venta desde carrito
                    if (event.getParams() != null && event.getParams().size() >= 1) {
                        String observaciones = event.getParams().get(0);
                        Integer pedidoId = null;
                        
                        if (event.getParams().size() >= 2) {
                            try {
                                pedidoId = Integer.parseInt(event.getParams().get(1));
                            } catch (NumberFormatException e) {
                                // Si no es número, ignorar
                            }
                        }
                        
                        List<String[]> notaVenta = nNotaVenta.crearNotaVentaDesdeCarrito(event.getSender(), pedidoId, observaciones);
                        
                        if (!notaVenta.isEmpty()) {
                            String[] enhancedHeaders = {"ID", "Cliente ID", "Pedido ID", "Fecha", "Total", "Estado", "Observaciones", "Creado", "Actualizado"};
                            tableNotifySuccess(event.getSender(), "✅ Nota de Venta Creada", enhancedHeaders, (ArrayList<String[]>) notaVenta, event.getCommand());
                        } else {
                            simpleNotify(event.getSender(), "Error al crear nota de venta", 
                                "❌ **No se pudo crear la nota de venta**");
                        }
                    } else {
                        simpleNotify(event.getSender(), "Parámetros insuficientes", 
                            "❌ **Uso: notaventa add <observaciones> [pedido_id]**");
                    }
                    break;
                    
                case Token.MODIFY:
                    if (event.getParams() != null && event.getParams().size() >= 3) {
                        try {
                            int id = Integer.parseInt(event.getParams().get(0));
                            String estado = event.getParams().get(1);
                            String observaciones = event.getParams().get(2);
                            
                            List<String[]> notaVenta = nNotaVenta.update(id, estado, observaciones);
                            
                            if (!notaVenta.isEmpty()) {
                                String[] enhancedHeaders = {"ID", "Cliente ID", "Pedido ID", "Fecha", "Total", "Estado", "Observaciones", "Creado", "Actualizado"};
                                tableNotifySuccess(event.getSender(), "✅ Nota de Venta Actualizada", enhancedHeaders, (ArrayList<String[]>) notaVenta, event.getCommand());
                            } else {
                                simpleNotify(event.getSender(), "Error al actualizar", 
                                    "❌ **No se pudo actualizar la nota de venta**");
                            }
                        } catch (NumberFormatException e) {
                            simpleNotify(event.getSender(), "Error de formato", 
                                "❌ **El ID debe ser un número válido**");
                        }
                    } else {
                        simpleNotify(event.getSender(), "Parámetros insuficientes", 
                            "❌ **Uso: notaventa modify <id, estado, observaciones>**");
                    }
                    break;
                    
                case Token.DELETE:
                    /* COMENTADO - Método delete no implementado en NNotaVenta
                    if (event.getParams() != null && event.getParams().size() >= 1) {
                        try {
                            int id = Integer.parseInt(event.getParams().get(0));
                            
                            boolean deleted = nNotaVenta.delete(id);
                            
                            if (deleted) {
                                simpleNotifySuccess(event.getSender(), "✅ Nota de Venta eliminada exitosamente");
                            } else {
                                simpleNotify(event.getSender(), "Error al eliminar", 
                                    "❌ **No se pudo eliminar la nota de venta**");
                            }
                        } catch (NumberFormatException e) {
                            simpleNotify(event.getSender(), "Error de formato", 
                                "❌ **El ID debe ser un número válido**");
                        }
                    } else {
                        simpleNotify(event.getSender(), "Parámetros insuficientes", 
                            "❌ **Uso: notaventa delete <id>**");
                    }
                    */
                    simpleNotify(event.getSender(), "Funcionalidad no disponible", 
                        "❌ **La eliminación de notas de venta no está permitida.**");
                    break;
                    
                default:
                    simpleNotify(event.getSender(), "Acción no válida", 
                        "❌ **Acción no válida para nota de venta**\n\n" +
                        "📋 **Comandos disponibles:**\n" +
                        "• notaventa get - Obtener mis notas de venta\n" +
                        "• notaventa get <id> - Obtener nota de venta por ID\n" +
                        "• notaventa add <observaciones> [pedido_id] - Crear desde carrito\n" +
                        "• notaventa modify <id, estado, observaciones> - Actualizar\n" +
                        "• notaventa delete <id> - Eliminar");
                    break;
            }
        } catch (SQLException ex) {
            Logger.getLogger(EmailApp.class.getName()).log(Level.SEVERE, 
                "Error SQL en notaVenta. Usuario: " + event.getSender() + 
                ", Action: " + event.getAction() + 
                ", Params: " + event.getParams(), ex);
            simpleNotify(event.getSender(), "Error de Base de Datos", 
                "❌ **Error al procesar la solicitud: " + ex.getMessage() + "**");
        } catch (Exception ex) {
            Logger.getLogger(EmailApp.class.getName()).log(Level.SEVERE, 
                "Error inesperado en notaVenta. Usuario: " + event.getSender() + 
                ", Action: " + event.getAction() + 
                ", Params: " + event.getParams(), ex);
            simpleNotify(event.getSender(), "Error del Sistema", 
                "❌ **Error inesperado: " + ex.getMessage() + "**");
        }
    }

    @Override
    public void pedido(ParamsAction event) {
        try {
            System.out.println("=== PROCESANDO PEDIDO PARA USUARIO ===");
            System.out.println("Usuario: " + event.getSender());
            System.out.println("Action: " + event.getAction());
            System.out.println("Params: " + event.getParams());
            
            switch (event.getAction()) {
                case Token.GET:
                    if (event.getParams() != null && event.getParams().size() >= 1) {
                        String param = event.getParams().get(0);
                        
                        // Intentar como ID numérico primero
                        try {
                            int id = Integer.parseInt(param);
                            List<String[]> pedidoData = nPedido.getById(id);
                            
                            if (!pedidoData.isEmpty()) {
                                String[] pedidoInfo = pedidoData.get(0);
                                
                                // Determinar estado de entrega más claro
                                String estadoEntrega = determinarEstadoEntrega(pedidoInfo[4], pedidoInfo[5], pedidoInfo[6]);
                                
                                // Headers simplificados y claros para el cliente
                                String[] clienteHeaders = {"Pedido #", "Fecha Pedido", "Total", "Estado", "Entrega"};
                                
                                // Datos simplificados centrados en lo que el cliente necesita
                                List<String[]> pedidoCliente = new ArrayList<>();
                                pedidoCliente.add(new String[]{
                                    "#" + pedidoInfo[0], // id → Pedido #
                                    pedidoInfo[2], // fecha → Fecha Pedido
                                    "Bs" + pedidoInfo[3], // total → Total (con moneda)
                                    capitalizarEstado(pedidoInfo[4]), // estado → Estado
                                    estadoEntrega // estado de entrega más claro
                                });
                                
                                tableNotifySuccess(event.getSender(), "🚚 Detalles del Pedido", clienteHeaders, (ArrayList<String[]>) pedidoCliente, event.getCommand());
                                
                                // Información de entrega más clara y relevante
                                simpleNotify(event.getSender(), "📍 Información de Entrega", 
                                    "🏠 **Dirección:** " + pedidoInfo[7] + "\n" +
                                    "📝 **Referencia:** " + (pedidoInfo[10] != null && !pedidoInfo[10].trim().isEmpty() ? pedidoInfo[10] : "Sin referencia específica") + "\n\n" +
                                    "📊 **Estado del pedido:** " + obtenerDescripcionEstado(pedidoInfo[4]) + "\n\n" +
                                    "ℹ️ **Comandos relacionados:**\n" +
                                    "• `pedido get` - Ver todos mis pedidos\n" +
                                    "• `notaventa get` - Ver mis notas de venta relacionadas");
                                
                            } else {
                                simpleNotify(event.getSender(), "Pedido no encontrado", 
                                    "❌ **No se encontró el pedido con ID: " + id + "**");
                            }
                        } catch (NumberFormatException e) {
                            simpleNotify(event.getSender(), "Error de formato", 
                                "❌ **El ID debe ser un número válido**");
                        }
                    } else {
                        // Obtener pedidos del usuario autenticado
                        List<String[]> pedidos = nPedido.getByClienteEmail(event.getSender());
                        
                        if (!pedidos.isEmpty()) {
                            // Headers simplificados y claros para el cliente
                            String[] clienteHeaders = {"Pedido #", "Fecha", "Total", "Estado", "Dirección"};
                            
                            // Datos simplificados centrados en lo que el cliente necesita
                            List<String[]> pedidosCliente = new ArrayList<>();
                            for (String[] pedido : pedidos) {
                                pedidosCliente.add(new String[]{
                                    "#" + pedido[0], // id → Pedido #
                                    pedido[2], // fecha → Fecha
                                    "Bs" + pedido[3], // total → Total (con moneda)
                                    capitalizarEstado(pedido[4]), // estado → Estado
                                    pedido[7] // direccion_nombre → Dirección
                                });
                            }
                            
                            tableNotifySuccess(event.getSender(), "🚚 Historial de Pedidos", clienteHeaders, (ArrayList<String[]>) pedidosCliente, event.getCommand());
                            
                            // Mensaje informativo más claro
                            simpleNotify(event.getSender(), "💡 Información Útil", 
                                "📋 **Para ver detalles completos de un pedido:**\n\n" +
                                "📌 **Comando:** `pedido get <numero>`\n\n" +
                                "📝 **Ejemplo:** `pedido get 1` (detalles del pedido #1)\n\n" +
                                "📊 **Estados de pedidos:**\n" +
                                "• **Pendiente** - En preparación\n" +
                                "• **Procesando** - Preparando envío\n" +
                                "• **Enviado** - En camino a tu dirección\n" +
                                "• **Entregado** - Completado exitosamente\n\n" +
                                "ℹ️ **Otros comandos:**\n" +
                                "• `notaventa get` - Ver facturas de compras\n" +
                                "• `comprar <tipo_pago_id, url_google_maps>` - Nueva compra");
                        } else {
                            simpleNotify(event.getSender(), "Sin pedidos", 
                                "📋 **No tienes pedidos registrados**\n\n" +
                                "🛒 **Para realizar tu primera compra:**\n" +
                                "1. Explora productos: `producto get`\n" +
                                "2. Agrega al carrito: `carrito add <producto_id, cantidad>`\n" +
                                "3. Realiza compra: `comprar <tipo_pago_id, url_google_maps>`");
                        }
                    }
                    break;
                    
                case Token.ADD:
                    /* COMENTADO - Método crearPedidoDesdeGoogleMaps no existe (depende de tabla direccion)
                    // Crear pedido con dirección
                    if (event.getParams() != null && event.getParams().size() >= 4) {
                        String nombreDireccion = event.getParams().get(0);
                        String urlGoogleMaps = event.getParams().get(1);
                        String referencia = event.getParams().get(2);
                        double total = Double.parseDouble(event.getParams().get(3));
                        
                        List<String[]> pedido = nPedido.crearPedidoDesdeGoogleMaps(nombreDireccion, urlGoogleMaps, referencia, total);
                        
                        if (!pedido.isEmpty()) {
                            String[] enhancedHeaders = {"ID", "Dirección ID", "Fecha", "Total", "Estado", "Fecha Envío", "Fecha Entrega", "Creado", "Actualizado"};
                            tableNotifySuccess(event.getSender(), "✅ Pedido Creado", enhancedHeaders, (ArrayList<String[]>) pedido, event.getCommand());
                        } else {
                            simpleNotify(event.getSender(), "Error al crear pedido", 
                                "❌ **No se pudo crear el pedido**");
                        }
                    } else {
                        simpleNotify(event.getSender(), "Parámetros insuficientes", 
                            "❌ **Uso: pedido add <nombre_direccion, url_google_maps, referencia, total>**");
                    }
                    */
                    simpleNotify(event.getSender(), "Funcionalidad no disponible", 
                        "❌ **La creación de pedidos desde este comando no está disponible.**\n\n" +
                        "🛒 **Use el flujo completo: carrito → comprar**");
                    break;
                    
                case Token.MODIFY:
                    if (event.getParams() != null && event.getParams().size() >= 2) {
                        try {
                            int id = Integer.parseInt(event.getParams().get(0));
                            String estado = event.getParams().get(1);
                            
                            List<String[]> pedido = nPedido.updateEstado(id, estado);
                            
                            if (!pedido.isEmpty()) {
                                String[] enhancedHeaders = {"ID", "Dirección ID", "Fecha", "Total", "Estado", "Fecha Envío", "Fecha Entrega", "Creado", "Actualizado"};
                                tableNotifySuccess(event.getSender(), "✅ Pedido Actualizado", enhancedHeaders, (ArrayList<String[]>) pedido, event.getCommand());
                            } else {
                                simpleNotify(event.getSender(), "Error al actualizar", 
                                    "❌ **No se pudo actualizar el pedido**");
                            }
                        } catch (NumberFormatException e) {
                            simpleNotify(event.getSender(), "Error de formato", 
                                "❌ **El ID debe ser un número válido**");
                        }
                    } else {
                        simpleNotify(event.getSender(), "Parámetros insuficientes", 
                            "❌ **Uso: pedido modify <id, estado>**");
                    }
                    break;
                    
                case Token.DELETE:
                    if (event.getParams() != null && event.getParams().size() >= 1) {
                        try {
                            int id = Integer.parseInt(event.getParams().get(0));
                            
                            boolean deleted = nPedido.delete(id);
                            
                            if (deleted) {
                                simpleNotifySuccess(event.getSender(), "✅ Pedido eliminado exitosamente");
                            } else {
                                simpleNotify(event.getSender(), "Error al eliminar", 
                                    "❌ **No se pudo eliminar el pedido**");
                            }
                        } catch (NumberFormatException e) {
                            simpleNotify(event.getSender(), "Error de formato", 
                                "❌ **El ID debe ser un número válido**");
                        }
                    } else {
                        simpleNotify(event.getSender(), "Parámetros insuficientes", 
                            "❌ **Uso: pedido delete <id>**");
                    }
                    break;
                    
                default:
                    simpleNotify(event.getSender(), "Acción no válida", 
                        "❌ **Acción no válida para pedido**\n\n" +
                        "📋 **Comandos disponibles:**\n" +
                        "• pedido get - Obtener todos los pedidos\n" +
                        "• pedido get <id> - Obtener pedido por ID\n" +
                        "• pedido add <nombre_direccion, url_google_maps, referencia, total> - Crear pedido\n" +
                        "• pedido modify <id, estado> - Actualizar estado\n" +
                        "• pedido delete <id> - Eliminar");
                    break;
            }
        } catch (SQLException ex) {
            Logger.getLogger(EmailApp.class.getName()).log(Level.SEVERE, 
                "Error SQL en pedido. Usuario: " + event.getSender() + 
                ", Action: " + event.getAction() + 
                ", Params: " + event.getParams(), ex);
            simpleNotify(event.getSender(), "Error de Base de Datos", 
                "❌ **Error al procesar la solicitud: " + ex.getMessage() + "**");
        } catch (Exception ex) {
            Logger.getLogger(EmailApp.class.getName()).log(Level.SEVERE, 
                "Error inesperado en pedido. Usuario: " + event.getSender() + 
                ", Action: " + event.getAction() + 
                ", Params: " + event.getParams(), ex);
            simpleNotify(event.getSender(), "Error del Sistema", 
                "❌ **Error inesperado: " + ex.getMessage() + "**");
        }
    }

    /* MÉTODO DIRECCION COMENTADO - TABLA NO EXISTE EN BD
    @Override
    public void direccion(ParamsAction event) {
        try {
            System.out.println("=== PROCESANDO DIRECCION PARA USUARIO ===");
            System.out.println("Usuario: " + event.getSender());
            System.out.println("Action: " + event.getAction());
            System.out.println("Params: " + event.getParams());
            
            switch (event.getAction()) {
                case Token.GET:
                    if (event.getParams() != null && event.getParams().size() >= 1) {
                        String param = event.getParams().get(0);
                        
                        // Intentar como ID numérico primero
                        try {
                            int id = Integer.parseInt(param);
                            List<String[]> direccionData = nDireccion.getById(id);
                            
                            if (!direccionData.isEmpty()) {
                                String[] enhancedHeaders = {"ID", "Nombre", "Longitud", "Latitud", "Referencia", "Creado", "Actualizado"};
                                tableNotifySuccess(event.getSender(), "Dirección encontrada", enhancedHeaders, (ArrayList<String[]>) direccionData, event.getCommand());
                            } else {
                                simpleNotify(event.getSender(), "Dirección no encontrada", 
                                    "❌ **No se encontró la dirección con ID: " + id + "**");
                            }
                        } catch (NumberFormatException e) {
                            // Si no es número, buscar por nombre
                            List<String[]> direcciones = nDireccion.getByNombre(param);
                            
                            if (!direcciones.isEmpty()) {
                                String[] enhancedHeaders = {"ID", "Nombre", "Longitud", "Latitud", "Referencia", "Creado", "Actualizado"};
                                tableNotifySuccess(event.getSender(), "Direcciones encontradas", enhancedHeaders, (ArrayList<String[]>) direcciones, event.getCommand());
                            } else {
                                simpleNotify(event.getSender(), "Dirección no encontrada", 
                                    "❌ **No se encontró la dirección: " + param + "**");
                            }
                        }
                    } else {
                        // Obtener todas las direcciones
                        List<String[]> direcciones = nDireccion.getAll();
                        
                        if (!direcciones.isEmpty()) {
                            String[] enhancedHeaders = {"ID", "Nombre", "Longitud", "Latitud", "Referencia", "Creado", "Actualizado"};
                            tableNotifySuccess(event.getSender(), "Todas las Direcciones", enhancedHeaders, (ArrayList<String[]>) direcciones, event.getCommand());
                        } else {
                            simpleNotify(event.getSender(), "Sin direcciones", 
                                "📋 **No hay direcciones registradas**");
                        }
                    }
                    break;
                    
                case Token.ADD:
                    // Crear dirección desde Google Maps
                    if (event.getParams() != null && event.getParams().size() >= 3) {
                        String nombre = event.getParams().get(0);
                        String urlGoogleMaps = event.getParams().get(1);
                        String referencia = event.getParams().get(2);
                        
                        List<String[]> direccion = nDireccion.crearDesdeGoogleMaps(nombre, urlGoogleMaps, referencia);
                        
                        if (!direccion.isEmpty()) {
                            String[] enhancedHeaders = {"ID", "Nombre", "Longitud", "Latitud", "Referencia", "Creado", "Actualizado"};
                            tableNotifySuccess(event.getSender(), "✅ Dirección Creada", enhancedHeaders, (ArrayList<String[]>) direccion, event.getCommand());
                        } else {
                            simpleNotify(event.getSender(), "Error al crear dirección", 
                                "❌ **No se pudo crear la dirección**");
                        }
                    } else {
                        simpleNotify(event.getSender(), "Parámetros insuficientes", 
                            "❌ **Uso: direccion add <nombre, url_google_maps, referencia>**");
                    }
                    break;
                    
                case Token.MODIFY:
                    if (event.getParams() != null && event.getParams().size() >= 4) {
                        try {
                            int id = Integer.parseInt(event.getParams().get(0));
                            String nombre = event.getParams().get(1);
                            String urlGoogleMaps = event.getParams().get(2);
                            String referencia = event.getParams().get(3);
                            
                            // Extraer coordenadas de la URL
                            double[] coordenadas = NDireccion.extraerCoordenadasDeUrl(urlGoogleMaps);
                            Double longitud = null;
                            Double latitud = null;
                            
                            if (coordenadas != null) {
                                latitud = coordenadas[0];
                                longitud = coordenadas[1];
                            }
                            
                            List<String[]> direccion = nDireccion.update(id, nombre, longitud, latitud, referencia);
                            
                            if (!direccion.isEmpty()) {
                                String[] enhancedHeaders = {"ID", "Nombre", "Longitud", "Latitud", "Referencia", "Creado", "Actualizado"};
                                tableNotifySuccess(event.getSender(), "✅ Dirección Actualizada", enhancedHeaders, (ArrayList<String[]>) direccion, event.getCommand());
                            } else {
                                simpleNotify(event.getSender(), "Error al actualizar", 
                                    "❌ **No se pudo actualizar la dirección**");
                            }
                        } catch (NumberFormatException e) {
                            simpleNotify(event.getSender(), "Error de formato", 
                                "❌ **El ID debe ser un número válido**");
                        }
                    } else {
                        simpleNotify(event.getSender(), "Parámetros insuficientes", 
                            "❌ **Uso: direccion modify <id, nombre, url_google_maps, referencia>**");
                    }
                    break;
                    
                case Token.DELETE:
                    if (event.getParams() != null && event.getParams().size() >= 1) {
                        try {
                            int id = Integer.parseInt(event.getParams().get(0));
                            
                            boolean deleted = nDireccion.delete(id);
                            
                            if (deleted) {
                                simpleNotifySuccess(event.getSender(), "✅ Dirección eliminada exitosamente");
                            } else {
                                simpleNotify(event.getSender(), "Error al eliminar", 
                                    "❌ **No se pudo eliminar la dirección**");
                            }
                        } catch (NumberFormatException e) {
                            simpleNotify(event.getSender(), "Error de formato", 
                                "❌ **El ID debe ser un número válido**");
                        }
                    } else {
                        simpleNotify(event.getSender(), "Parámetros insuficientes", 
                            "❌ **Uso: direccion delete <id>**");
                    }
                    break;
                    
                default:
                    simpleNotify(event.getSender(), "Acción no válida", 
                        "❌ **Acción no válida para dirección**\n\n" +
                        "📋 **Comandos disponibles:**\n" +
                        "• direccion get - Obtener todas las direcciones\n" +
                        "• direccion get <id> - Obtener dirección por ID\n" +
                        "• direccion get <nombre> - Buscar dirección por nombre\n" +
                        "• direccion add <nombre, url_google_maps, referencia> - Crear dirección\n" +
                        "• direccion modify <id, nombre, url_google_maps, referencia> - Actualizar\n" +
                        "• direccion delete <id> - Eliminar");
                    break;
            }
        } catch (SQLException ex) {
            Logger.getLogger(EmailApp.class.getName()).log(Level.SEVERE, 
                "Error SQL en direccion. Usuario: " + event.getSender() + 
                ", Action: " + event.getAction() + 
                ", Params: " + event.getParams(), ex);
            simpleNotify(event.getSender(), "Error de Base de Datos", 
                "❌ **Error al procesar la solicitud: " + ex.getMessage() + "**");
        } catch (Exception ex) {
            Logger.getLogger(EmailApp.class.getName()).log(Level.SEVERE, 
                "Error inesperado en direccion. Usuario: " + event.getSender() + 
                ", Action: " + event.getAction() + 
                ", Params: " + event.getParams(), ex);
            simpleNotify(event.getSender(), "Error del Sistema", 
                "❌ **Error inesperado: " + ex.getMessage() + "**");
        }
    }
    FIN COMENTARIO MÉTODO DIRECCION */

    @Override
    public void comprar(ParamsAction event) {
        try {
            System.out.println("=== PROCESANDO COMPRA PARA USUARIO ===");
            System.out.println("Usuario: " + event.getSender());
            System.out.println("Params: " + event.getParams());
            
            // Validar parámetros: comprar <metodo_pago_id>
            if (event.getParams() == null || event.getParams().size() < 1) {
                simpleNotify(event.getSender(), "Parámetros insuficientes", 
                    "❌ **Uso: comprar &lt;metodo_pago_id&gt;**\n\n" +
                    "📋 **Ejemplo:**\n" +
                    "comprar 1\n\n" +
                    "💳 **Para ver los métodos de pago disponibles use:**\n" +
                    "metodoPago get");
                return;
            }
            
            // Obtener el ID del método de pago
            int metodoPagoId;
            try {
                metodoPagoId = Integer.parseInt(event.getParams().get(0));
            } catch (NumberFormatException e) {
                simpleNotify(event.getSender(), "ID de método de pago inválido", 
                    "❌ **El parámetro debe ser un número (ID del método de pago).**\n\n" +
                    "💳 **Para ver los métodos de pago disponibles use:**\n" +
                    "metodoPago get");
                return;
            }
            
            // 1. Obtener usuario y validar que sea cliente (rol_id = 2)
            List<String[]> userData = nUsuario.findByEmail(event.getSender());
            if (userData.isEmpty()) {
                simpleNotify(event.getSender(), "Usuario no encontrado", 
                    "❌ **Su correo electrónico no está registrado en nuestro sistema.**");
                return;
            }
            
            int usuarioId = Integer.parseInt(userData.get(0)[0]);
            int rolId = Integer.parseInt(userData.get(0)[1]);
            
            if (rolId != 2) {
                simpleNotify(event.getSender(), "Acceso denegado", 
                    "❌ **Solo los clientes pueden realizar compras.**");
                return;
            }
            
            // 2. Obtener carrito activo
            List<String[]> carritoData = nCarrito.getCarritoActivo(usuarioId);
            if (carritoData.isEmpty()) {
                simpleNotify(event.getSender(), "Carrito vacío", 
                    "❌ **Su carrito está vacío. Agregue productos antes de comprar.**\n\n" +
                    "🛒 **Use el comando:**\n" +
                    "carrito add &lt;producto_id, cantidad&gt;");
                return;
            }
            
            int carritoId = Integer.parseInt(carritoData.get(0)[0]);
            
            // 3. Obtener detalles del carrito
            List<String[]> detallesCarrito = nCarrito.getDetallesCarrito(carritoId);
            if (detallesCarrito.isEmpty()) {
                simpleNotify(event.getSender(), "Carrito sin productos", 
                    "❌ **Su carrito no tiene productos. Agregue productos antes de comprar.**\n\n" +
                    "🛒 **Use el comando:**\n" +
                    "carrito add &lt;producto_id, cantidad&gt;");
                return;
            }
            
            // 4. Calcular total del carrito
            double totalCarrito = 0.0;
            for (String[] detalle : detallesCarrito) {
                totalCarrito += Double.parseDouble(detalle[5]); // subtotal
            }
            
            // 5. Crear pedido
            String descripcion = "Pedido desde carrito - Email: " + event.getSender();
            List<String[]> pedidoData = nPedido.crearPedidoSimple(descripcion, totalCarrito, metodoPagoId, usuarioId);
            
            if (pedidoData.isEmpty()) {
                simpleNotify(event.getSender(), "Error al crear pedido", 
                    "❌ **No se pudo crear el pedido.**");
                return;
            }
            
            int pedidoId = Integer.parseInt(pedidoData.get(0)[0]);
            
            // 6. Crear nota de venta desde carrito
            String observaciones = "Compra realizada - Método de pago ID: " + metodoPagoId;
            List<String[]> notaVenta = nNotaVenta.crearNotaVentaDesdeCarrito(event.getSender(), pedidoId, observaciones);
            
            if (notaVenta.isEmpty()) {
                simpleNotify(event.getSender(), "Error al crear nota de venta", 
                    "❌ **No se pudo crear la nota de venta.**");
                return;
            }
            
            int notaVentaId = Integer.parseInt(notaVenta.get(0)[0]);
            
            // 7. Marcar carrito como inactivo
            List<String[]> carritoActualizado = nCarrito.updateActivo(carritoId, false);
            if (carritoActualizado.isEmpty()) {
                System.out.println("⚠️ Advertencia: No se pudo desactivar el carrito");
            }
            
            // 8. Generar resumen de la compra
            StringBuilder resumen = new StringBuilder();
            resumen.append("🎉 **¡Compra realizada exitosamente!**\n\n");
            resumen.append("📋 **Resumen de la compra:**\n");
            resumen.append("• **Nota de Venta ID:** ").append(notaVentaId).append("\n");
            resumen.append("• **Pedido ID:** ").append(pedidoId).append("\n");
            resumen.append("• **Total:** Bs").append(String.format("%.2f", totalCarrito)).append("\n");
            resumen.append("• **Método de pago ID:** ").append(metodoPagoId).append("\n");
            resumen.append("• **Estado:** Completada ✅\n\n");
            
            resumen.append("🛒 **Productos comprados:**\n");
            for (String[] detalle : detallesCarrito) {
                String nombreProducto = detalle[6];  // producto_nombre
                String cantidad = detalle[3];        // cantidad
                String precioUnitario = detalle[4];  // precio_unitario
                String subtotal = detalle[5];        // subtotal
                
                resumen.append("• ").append(nombreProducto)
                       .append(" x").append(cantidad)
                       .append(" (Bs").append(precioUnitario).append(" c/u)")
                       .append(" = Bs").append(subtotal).append("\n");
            }
            
            resumen.append("\n✅ **Su pedido ha sido registrado y será procesado pronto.**\n");
            resumen.append("📧 **Recibirá una confirmación por correo electrónico.**");
            
            simpleNotifySuccess(event.getSender(), resumen.toString());
            
        } catch (SQLException ex) {
            Logger.getLogger(EmailApp.class.getName()).log(Level.SEVERE, 
                "Error SQL en comprar. Usuario: " + event.getSender() + 
                ", Params: " + event.getParams(), ex);
            simpleNotify(event.getSender(), "Error de Base de Datos", 
                "❌ **Error al procesar la compra: " + ex.getMessage() + "**");
        } catch (Exception ex) {
            Logger.getLogger(EmailApp.class.getName()).log(Level.SEVERE, 
                "Error inesperado en comprar. Usuario: " + event.getSender() + 
                ", Params: " + event.getParams(), ex);
            simpleNotify(event.getSender(), "Error del Sistema", 
                "❌ **Error inesperado: " + ex.getMessage() + "**");
        }
    }

    /* MÉTODO COMPRAR ORIGINAL COMENTADO - DEPENDE DE TABLAS NO EXISTENTES (direccion, cliente)
    public void comprar_OLD(ParamsAction event) {
        try {
            System.out.println("=== PROCESANDO COMPRA COMPLETA PARA USUARIO ===");
            System.out.println("Usuario: " + event.getSender());
            System.out.println("Action: " + event.getAction());
            System.out.println("Params: " + event.getParams());
            
            // El comando comprar no necesita action, es un comando único
            // Formato: comprar <tipo_pago_id, url_google_maps>
            
            if (event.getParams() == null || event.getParams().size() < 2) {
                simpleNotify(event.getSender(), "Parámetros insuficientes", 
                    "❌ **Uso: comprar <tipo_pago_id, url_google_maps>**\n\n" +
                    "📋 **Ejemplo:**\n" +
                    "comprar <1, https://www.google.com/maps/@-17.8521448,-63.167395,16z?entry=ttu>\n\n" +
                    "💳 **Para ver los tipos de pago disponibles use:**\n" +
                    "tipopago get");
                return;
            }
            
            // Obtener el ID del tipo de pago
            int tipoPagoId;
            try {
                tipoPagoId = Integer.parseInt(event.getParams().get(0));
            } catch (NumberFormatException e) {
                simpleNotify(event.getSender(), "ID de método de pago inválido", 
                    "❌ **El primer parámetro debe ser un número (ID del método de pago).**\n\n" +
                    "💳 **Para ver los métodos de pago disponibles use:**\n" +
                    "tipopago get");
                return;
            }
            
            // Obtener el tipo de pago de la base de datos
            List<String[]> tipoPagoData = nTipoPago.getById(tipoPagoId);
            if (tipoPagoData.isEmpty()) {
                simpleNotify(event.getSender(), "Método de pago no encontrado", 
                    "❌ **No se encontró el método de pago con ID: " + tipoPagoId + "**\n\n" +
                    "💳 **Para ver los métodos de pago disponibles use:**\n" +
                    "tipopago get");
                return;
            }
            
            String metodoPago = tipoPagoData.get(0)[1]; // El nombre del método de pago
            
            // Reconstruir la URL de Google Maps si se dividió en múltiples parámetros
            String urlGoogleMaps = event.getParams().get(1);
            if (event.getParams().size() > 2) {
                // Si hay más parámetros, reconstruir la URL completa
                StringBuilder urlBuilder = new StringBuilder(urlGoogleMaps);
                for (int i = 2; i < event.getParams().size(); i++) {
                    urlBuilder.append(",").append(event.getParams().get(i));
                }
                urlGoogleMaps = urlBuilder.toString();
            }
            
            System.out.println("URL reconstruida: " + urlGoogleMaps);
            
            // Generar nombre de dirección automáticamente
            String nombreDireccion = "Dirección de Entrega";
            String referencia = "Entrega a domicilio";
            String observaciones = "Compra realizada desde el carrito";
            
            // Validar método de pago
            if (!esMetodoPagoValido(metodoPago)) {
                simpleNotify(event.getSender(), "Método de pago inválido", 
                    "❌ **Método de pago no soportado: " + metodoPago + "**\n\n" +
                    "💳 **Métodos de pago soportados:**\n" +
                    "• efectivo\n" +
                    "• tarjeta\n" +
                    "• transferencia\n" +
                    "• pago_movil\n" +
                    "• qr\n" +
                    "• paypal\n" +
                    "• bitcoin");
                return;
            }
            
            // 1. Verificar que el carrito tenga productos
            // Primero obtener el cliente por email
            List<String[]> clienteData = nCliente.getByUserId(getUserIdByEmail(event.getSender()));
            if (clienteData.isEmpty()) {
                simpleNotify(event.getSender(), "Cliente no encontrado", 
                    "❌ **No se encontró su información de cliente. Regístrese primero.**");
                return;
            }
            
            int clienteId = Integer.parseInt(clienteData.get(0)[0]);
            System.out.println("🔍 DEBUG: Cliente ID: " + clienteId);
            
            List<String[]> carritoData = nCarrito.getCarritoActivo(clienteId);
            System.out.println("🔍 DEBUG: Carrito obtenido - ID: " + (carritoData.isEmpty() ? "NONE" : carritoData.get(0)[0]) + 
                             ", Estado: " + (carritoData.isEmpty() ? "NONE" : carritoData.get(0)[4]) + 
                             ", Total: " + (carritoData.isEmpty() ? "NONE" : carritoData.get(0)[3]));
            
            if (carritoData.isEmpty()) {
                simpleNotify(event.getSender(), "Carrito vacío", 
                    "❌ **Su carrito está vacío. Agregue productos antes de comprar.**\n\n" +
                    "🛒 **Use el comando:**\n" +
                    "carrito add <producto_id, cantidad>");
                return;
            }
            
            int carritoId = Integer.parseInt(carritoData.get(0)[0]);
            System.out.println("🔍 DEBUG: Usando carrito ID: " + carritoId);
            
            List<String[]> detallesCarrito = nCarrito.getDetallesCarrito(carritoId);
            System.out.println("🔍 DEBUG: Detalles del carrito encontrados: " + detallesCarrito.size());
            
            if (detallesCarrito.isEmpty()) {
                simpleNotify(event.getSender(), "Carrito sin productos", 
                    "❌ **Su carrito no tiene productos. Agregue productos antes de comprar.**\n\n" +
                    "🛒 **Use el comando:**\n" +
                    "carrito add <producto_id, cantidad>");
                return;
            }
            
            // Calcular total del carrito
            double totalCarrito = 0.0;
            for (String[] detalle : detallesCarrito) {
                totalCarrito += Double.parseDouble(detalle[5]); // subtotal correcto
            }
            
            // 2. Crear dirección
            List<String[]> direccion = nDireccion.crearDesdeGoogleMaps(nombreDireccion, urlGoogleMaps, referencia);
            if (direccion.isEmpty()) {
                simpleNotify(event.getSender(), "Error al crear dirección", 
                    "❌ **No se pudo crear la dirección. Verifique la URL de Google Maps.**");
                return;
            }
            
            int direccionId = Integer.parseInt(direccion.get(0)[0]);
            
            // 3. Crear pedido
            List<String[]> pedido = nPedido.crearPedidoConDireccionExistente(direccionId, totalCarrito);
            if (pedido.isEmpty()) {
                simpleNotify(event.getSender(), "Error al crear pedido", 
                    "❌ **No se pudo crear el pedido.**");
                return;
            }
            
            int pedidoId = Integer.parseInt(pedido.get(0)[0]);
            
            // 4. Crear nota de venta desde carrito
            List<String[]> notaVenta = nNotaVenta.crearNotaVentaDesdeCarrito(event.getSender(), pedidoId, observaciones);
            if (notaVenta.isEmpty()) {
                simpleNotify(event.getSender(), "Error al crear nota de venta", 
                    "❌ **No se pudo crear la nota de venta.**");
                return;
            }
            
            int notaVentaId = Integer.parseInt(notaVenta.get(0)[0]);
            
            // 5. Procesar pago (simulado)
            System.out.println("🔍 DEBUG COMPRA: Procesando pago - NotaVenta ID: " + notaVentaId + ", Total: " + totalCarrito + ", Método: " + metodoPago);
            // El pago se procesa automáticamente al crear la nota de venta
            
            // 6. Cambiar estado del carrito a "procesado"
            boolean carritoActualizado = nCarrito.cambiarEstadoCarrito(carritoId, "procesado");
            if (!carritoActualizado) {
                System.out.println("⚠️ Advertencia: No se pudo cambiar el estado del carrito a procesado");
            }
            
            // 6. Generar resumen de la compra
            StringBuilder resumen = new StringBuilder();
            resumen.append("🎉 **¡Compra realizada exitosamente!**\n\n");
            resumen.append("📋 **Resumen de la compra:**\n");
            resumen.append("• **Nota de Venta ID:** ").append(notaVentaId).append("\n");
            resumen.append("• **Pedido ID:** ").append(pedidoId).append("\n");
            resumen.append("• **Dirección ID:** ").append(direccionId).append("\n");
            resumen.append("• **Total:** Bs").append(String.format("%.2f", totalCarrito)).append("\n");
            resumen.append("• **Método de pago:** ").append(metodoPago).append("\n");
            resumen.append("• **Estado:** Completada ✅\n\n");
            
            resumen.append("📍 **Dirección de entrega:**\n");
            resumen.append("• **Nombre:** ").append(nombreDireccion).append("\n");
            resumen.append("• **Referencia:** ").append(referencia).append("\n");
            resumen.append("• **Google Maps:** ").append(urlGoogleMaps).append("\n\n");
            
            resumen.append("🛒 **Productos comprados:**\n");
            for (String[] detalle : detallesCarrito) {
                String nombreProducto = detalle[6];  // producto_nombre
                String cantidad = detalle[3];        // cantidad
                String precioUnitario = detalle[4];  // precio_unitario
                String subtotal = detalle[5];        // subtotal
                
                resumen.append("• ").append(nombreProducto)
                       .append(" x").append(cantidad)
                       .append(" (Bs").append(precioUnitario).append(" c/u)")
                       .append(" = Bs").append(subtotal).append("\n");
            }
            resumen.append("\n");
            
            resumen.append("📝 **Observaciones:** ").append(observaciones).append("\n\n");
            resumen.append("🚚 **Su pedido será procesado y enviado pronto.**\n");
            resumen.append("📧 **Recibirá actualizaciones por email.**");
            
            simpleNotifySuccess(event.getSender(), resumen.toString());
            
        } catch (SQLException ex) {
            Logger.getLogger(EmailApp.class.getName()).log(Level.SEVERE, 
                "Error SQL en comprar. Usuario: " + event.getSender() + 
                ", Params: " + event.getParams(), ex);
            simpleNotify(event.getSender(), "Error de Base de Datos", 
                "❌ **Error al procesar la compra: " + ex.getMessage() + "**");
        } catch (Exception ex) {
            Logger.getLogger(EmailApp.class.getName()).log(Level.SEVERE, 
                "Error inesperado en comprar. Usuario: " + event.getSender() + 
                ", Params: " + event.getParams(), ex);
            simpleNotify(event.getSender(), "Error del Sistema", 
                "❌ **Error inesperado: " + ex.getMessage() + "**");
        }
    }
    FIN COMENTARIO MÉTODO COMPRAR */

    @Override
    public void carrito(ParamsAction event) {
        try {
            // Obtener usuario_id del usuario que hace la petición (debe ser rol_id = 2, cliente)
            String userEmail = event.getSender();
            System.out.println("=== PROCESANDO CARRITO PARA USUARIO: " + userEmail + " ===");
            
            List<String[]> userData = nUsuario.findByEmail(userEmail);
            System.out.println("Usuario encontrado: " + !userData.isEmpty());
            
            if (userData.isEmpty()) {
                System.out.println("❌ Usuario no encontrado: " + userEmail);
                simpleNotify(userEmail, "Usuario no encontrado", 
                    "❌ **Su correo electrónico no está registrado en nuestro sistema.**\n\n" +
                    "🔐 **Para acceder al carrito, debe registrarse primero.**");
                return;
            }
            
            int usuarioId = Integer.parseInt(userData.get(0)[0]);
            int rolId = Integer.parseInt(userData.get(0)[1]); // rol_id
            System.out.println("Usuario ID encontrado: " + usuarioId + ", Rol: " + rolId);
            
            // Verificar que el usuario sea cliente (rol_id = 2)
            if (rolId != 2) {
                System.out.println("❌ Usuario no es cliente. Rol ID: " + rolId);
                simpleNotify(userEmail, "Acceso denegado", 
                    "❌ **Solo los clientes pueden acceder al carrito.**\n\n" +
                    "Su rol actual no permite esta operación.");
                return;
            }
            
            System.out.println("Acción solicitada: " + event.getAction());
            
            switch (event.getAction()) {
                case Token.GET:
                    // Obtener el carrito activo del usuario
                    List<String[]> carritoData = nCarrito.getCarritoActivo(usuarioId);
                    
                    if (!carritoData.isEmpty()) {
                        int carritoId = Integer.parseInt(carritoData.get(0)[0]);
                        String estado = carritoData.get(0)[4];
                        
                        // Obtener los detalles del carrito con productos
                        List<String[]> detallesData = nCarrito.getDetallesCarrito(carritoId);
                        
                        // Mostrar información del carrito con headers amigables para el cliente
                        String[] carritoHeaders = {"Carrito ID", "Fecha", "Total", "Estado", "NIT", "Cliente", "Email"};
                        
                        // Crear datos del carrito sin mostrar cliente_id al cliente
                        List<String[]> carritoClienteData = new ArrayList<>();
                        for (String[] carrito : carritoData) {
                            carritoClienteData.add(new String[]{
                                carrito[0], // id → Carrito ID
                                carrito[2], // fecha → Fecha  
                                carrito[3], // total → Total
                                carrito[4], // estado → Estado
                                carrito[5], // nit → NIT
                                carrito[6], // nombre → Cliente
                                carrito[7]  // email → Email
                            });
                        }
                        
                        tableNotifySuccess(event.getSender(), "🛒 **Tu Carrito de Compras**", carritoHeaders, (ArrayList<String[]>) carritoClienteData, event.getCommand());
                        
                        // Mostrar productos del carrito con headers amigables
                        if (!detallesData.isEmpty()) {
                            String[] detallesHeaders = {"Detalle ID", "Producto ID", "Cantidad", "Precio Unit.", "Subtotal", "Producto", "Descripción"};
                            
                            // Crear datos de detalles sin mostrar carrito_id al cliente
                            List<String[]> detallesClienteData = new ArrayList<>();
                            for (String[] detalle : detallesData) {
                                detallesClienteData.add(new String[]{
                                    detalle[0], // id → Detalle ID
                                    detalle[2], // producto_almacen_id → Producto ID  
                                    detalle[3], // cantidad → Cantidad
                                    detalle[4], // precio_unitario → Precio Unit.
                                    detalle[5], // subtotal → Subtotal
                                    detalle[6], // producto_nombre → Producto
                                    detalle[7]  // producto_descripcion → Descripción
                                    // Quitamos: detalle[8] (stock) y detalle[9] (precio_venta)
                                });
                            }
                            
                            tableNotifySuccess(event.getSender(), "📦 **Productos en tu Carrito**", detallesHeaders, (ArrayList<String[]>) detallesClienteData, event.getCommand());
                            
                            // Agregar explicación sobre los IDs para el cliente
                            simpleNotify(event.getSender(), "ℹ️ Información sobre IDs", 
                                "📋 **Explicación de identificadores:**\n\n" +
                                "🔹 **Detalle ID:** Identificador único de cada producto en tu carrito\n" +
                                "   • Lo necesitas para modificar cantidad: `carrito modify <detalle_id, nueva_cantidad>`\n" +
                                "   • Lo necesitas para eliminar: `carrito delete <detalle_id>`\n\n" +
                                "🔹 **Producto ID:** Identificador del producto en nuestro catálogo\n" +
                                "   • Es útil para agregar más del mismo producto: `carrito add <producto_id, cantidad>`\n" +
                                "   • Puedes ver todos los productos con: `producto get`");
                        } else {
                            simpleNotify(event.getSender(), "Carrito vacío", 
                                "🛒 **Tu carrito está vacío.**\n\n" +
                                "📦 **Agrega productos usando:**\n" +
                                "carrito add &lt;producto_id, cantidad&gt;");
                        }
                        
                        // Mensaje informativo sobre el estado
                        if ("abandonado".equals(estado)) {
                            simpleNotify(event.getSender(), "Carrito reactivado", 
                                "✅ **Tu carrito abandonado ha sido reactivado automáticamente.**");
                        } else if ("procesado".equals(estado)) {
                            simpleNotify(event.getSender(), "Nuevo carrito creado", 
                                "🆕 **Se ha creado un nuevo carrito para ti.**");
                        }
                        
                    } else {
                        simpleNotify(event.getSender(), "Error al obtener carrito", 
                            "❌ **No se pudo obtener o crear tu carrito.**\n\n" +
                            "🔧 **Contacte al administrador si el problema persiste.**");
                    }
                    break;
                    
                case Token.ADD:
                    System.out.println("=== PROCESANDO ADD AL CARRITO ===");
                    System.out.println("Parámetros recibidos: " + event.getParams());
                    
                    if (event.getParams() != null && event.getParams().size() >= 2) {
                        // Comando: carrito add <producto_id, cantidad>
                        int productoId = Integer.parseInt(event.getParams().get(0));
                        int cantidad = Integer.parseInt(event.getParams().get(1));
                        
                        System.out.println("Producto ID: " + productoId);
                        System.out.println("Cantidad: " + cantidad);
                        
                        // Obtener el carrito activo
                        System.out.println("Obteniendo carrito activo para usuario: " + usuarioId);
                        List<String[]> carritoActivo = nCarrito.getCarritoActivo(usuarioId);
                        System.out.println("Carrito activo encontrado: " + !carritoActivo.isEmpty());
                        
                        if (!carritoActivo.isEmpty()) {
                            int carritoId = Integer.parseInt(carritoActivo.get(0)[0]);
                            System.out.println("Carrito ID: " + carritoId);
                            
                            System.out.println("Agregando producto al carrito...");
                            List<String[]> resultado = nCarrito.agregarProducto(carritoId, productoId, cantidad);
                            System.out.println("Producto agregado exitosamente");
                            
                            if (!resultado.isEmpty()) {
                                // Obtener los detalles completos del carrito después del agregado
                                List<String[]> detallesCompletos = nCarrito.getDetallesCarrito(carritoId);
                                
                                // Headers amigables para el cliente (igual que en carrito get)
                                String[] detallesHeaders = {"Detalle ID", "Producto ID", "Cantidad", "Precio Unit.", "Subtotal", "Producto", "Descripción"};
                                
                                // Crear datos de detalles sin mostrar carrito_id al cliente
                                List<String[]> detallesClienteData = new ArrayList<>();
                                for (String[] detalle : detallesCompletos) {
                                    detallesClienteData.add(new String[]{
                                        detalle[0], // id → Detalle ID
                                        detalle[2], // producto_almacen_id → Producto ID  
                                        detalle[3], // cantidad → Cantidad
                                        detalle[4], // precio_unitario → Precio Unit.
                                        detalle[5], // subtotal → Subtotal
                                        detalle[6], // producto_nombre → Producto
                                        detalle[7]  // producto_descripcion → Descripción
                                        // Quitamos: detalle[8] (stock) y detalle[9] (precio_venta)
                                    });
                                }
                                
                                // Obtener el ID del detalle recién agregado desde el resultado
                                String detalleIdAgregado = resultado.get(0)[0];
                                System.out.println("🔍 DEBUG: Detalle agregado ID: " + detalleIdAgregado);
                                
                                // Buscar el detalle específico que se acaba de agregar por su ID
                                String[] detalleEncontrado = null;
                                for (String[] detalle : detallesCompletos) {
                                    if (detalleIdAgregado.equals(detalle[0])) {
                                        detalleEncontrado = detalle;
                                        break;
                                    }
                                }
                                
                                if (detalleEncontrado == null) {
                                    simpleNotify(event.getSender(), "Error", 
                                        "❌ **No se pudo encontrar la información del producto agregado.**");
                                    return;
                                }
                                
                                String[] detalleAgregado = new String[]{
                                    detalleEncontrado[0], // id → Detalle ID
                                    detalleEncontrado[2], // producto_almacen_id → Producto ID  
                                    detalleEncontrado[3], // cantidad → Cantidad
                                    detalleEncontrado[4], // precio_unitario → Precio Unit.
                                    detalleEncontrado[5], // subtotal → Subtotal
                                    detalleEncontrado[6], // producto_nombre → Producto
                                    detalleEncontrado[7]  // producto_descripcion → Descripción
                                };
                                
                                // Mostrar solo el producto agregado
                                List<String[]> productoAgregadoData = new ArrayList<>();
                                productoAgregadoData.add(detalleAgregado);
                                
                                tableNotifySuccess(event.getSender(), "✅ Producto Agregado al Carrito", detallesHeaders, (ArrayList<String[]>) productoAgregadoData, event.getCommand());
                                
                                // Información adicional sobre el producto agregado
                                simpleNotify(event.getSender(), "🛒 Producto Agregado Exitosamente", 
                                    "📦 **Producto:** " + detalleAgregado[5] + "\n" +
                                    "📋 **Descripción:** " + detalleAgregado[6] + "\n" +
                                    "🔢 **Cantidad agregada:** " + detalleAgregado[2] + " unidades\n" +
                                    "💰 **Precio unitario:** Bs" + detalleAgregado[3] + "\n" +
                                    "💵 **Subtotal:** Bs" + detalleAgregado[4] + "\n\n" +
                                    "ℹ️ **Comandos útiles:**\n" +
                                    "• `carrito get` - Ver todo tu carrito\n" +
                                    "• `carrito modify " + detalleAgregado[0] + ", <cantidad>` - Modificar cantidad\n" +
                                    "• `carrito delete " + detalleAgregado[0] + "` - Eliminar este producto");
                            } else {
                                simpleNotify(event.getSender(), "Error", 
                                    "❌ **No se pudo agregar el producto al carrito.**");
                            }
                            
                        } else {
                            System.out.println("❌ No se pudo obtener carrito activo");
                            simpleNotify(event.getSender(), "Error al agregar producto", 
                                "❌ **No se pudo obtener tu carrito activo.**");
                        }
                        
                    } else {
                        simpleNotify(event.getSender(), "Error de Parámetros", 
                            "❌ **Parámetros incorrectos.**\n\n" +
                            "📋 **Formato correcto:**\n" +
                            "carrito add &lt;producto_id, cantidad&gt;\n\n" +
                            "📧 **Ejemplo:**\n" +
                            "carrito add 1, 2");
                    }
                    break;
                    
                case Token.MODIFY:
                    if (event.getParams() != null && event.getParams().size() >= 2) {
                        // Comando: carrito modify <detalle_id, cantidad>
                        int detalleId = Integer.parseInt(event.getParams().get(0));
                        int cantidad = Integer.parseInt(event.getParams().get(1));
                        
                        // Primero obtener información del producto antes de modificar
                        DDetalleCarrito dDetalleCarrito = new DDetalleCarrito();
                        List<String[]> infoAnterior = dDetalleCarrito.getById(detalleId);
                        
                        if (infoAnterior.isEmpty()) {
                            simpleNotify(event.getSender(), "Error", 
                                "❌ **No se encontró el producto en tu carrito.**\n\n" +
                                "🔍 **Para ver tu carrito actual usa:**\n" +
                                "carrito get");
                        } else {
                            // Obtener información del producto antes de modificar
                            String[] productoAnterior = infoAnterior.get(0);
                            String nombreProducto = productoAnterior[6]; // producto_nombre
                            String descripcionProducto = productoAnterior[7]; // producto_descripcion
                            int cantidadAnterior = Integer.parseInt(productoAnterior[3]); // cantidad
                            String precioUnitario = productoAnterior[4]; // precio_unitario
                            
                            // Realizar la actualización
                        List<String[]> resultado = nCarrito.actualizarCantidad(detalleId, cantidad);
                            
                            if (!resultado.isEmpty()) {
                                // Crear headers amigables para el cliente
                                String[] detallesHeaders = {"Detalle ID", "Producto ID", "Cantidad", "Precio Unit.", "Subtotal", "Producto", "Descripción"};
                                
                                // Crear datos amigables (sin mostrar carrito_id)
                                List<String[]> resultadoClienteData = new ArrayList<>();
                                for (String[] detalle : resultado) {
                                    resultadoClienteData.add(new String[]{
                                        detalle[0], // id → Detalle ID
                                        detalle[2], // producto_almacen_id → Producto ID  
                                        detalle[3], // cantidad → Cantidad
                                        detalle[4], // precio_unitario → Precio Unit.
                                        detalle[5], // subtotal → Subtotal
                                        detalle[6], // producto_nombre → Producto
                                        detalle[7]  // producto_descripcion → Descripción
                                        // Quitamos: detalle[8] (stock) y detalle[9] (precio_venta)
                                    });
                                }
                                
                                // Mensaje personalizado y detallado
                                simpleNotify(event.getSender(), "✅ Cantidad Actualizada", 
                                    "🛒 **Producto modificado exitosamente:**\n\n" +
                                    "📦 **Producto:** " + nombreProducto + "\n" +
                                    "📋 **Descripción:** " + descripcionProducto + "\n\n" +
                                    "🔄 **Cambio realizado:**\n" +
                                    "• **Cantidad anterior:** " + cantidadAnterior + " unidades\n" +
                                    "• **Nueva cantidad:** " + cantidad + " unidades\n" +
                                    "• **Precio unitario:** $" + precioUnitario + "\n\n" +
                                    "💰 **Nuevo subtotal:** $" + resultado.get(0)[5]);
                                
                                // Mostrar tabla con información actualizada
                                tableNotifySuccess(event.getSender(), "📊 **Detalle actualizado en tu carrito**", detallesHeaders, (ArrayList<String[]>) resultadoClienteData, event.getCommand());
                                
                            } else {
                                simpleNotify(event.getSender(), "Error al actualizar", 
                                    "❌ **No se pudo actualizar la cantidad del producto.**\n\n" +
                                    "🔧 **Verifica que la cantidad sea válida y mayor a 0.**");
                            }
                        }
                        
                    } else {
                        simpleNotify(event.getSender(), "Error de Parámetros", 
                            "❌ **Parámetros incorrectos.**\n\n" +
                            "📋 **Formato correcto:**\n" +
                            "carrito modify &lt;detalle_id, cantidad&gt;\n\n" +
                            "📧 **Ejemplo:**\n" +
                            "carrito modify 1, 3");
                    }
                    break;
                    
                case Token.DELETE:
                    if (event.getParams() != null && event.getParams().size() >= 1) {
                        // Comando: carrito delete <detalle_id>
                        int detalleId = Integer.parseInt(event.getParams().get(0));
                        
                        boolean success = nCarrito.eliminarProducto(detalleId);
                        if (success) {
                            simpleNotifySuccess(event.getSender(), 
                                "✅ **Producto eliminado del carrito exitosamente**\n\n" +
                                "🗑️ **Detalle eliminado:** " + detalleId);
                        } else {
                            simpleNotify(event.getSender(), "Error al eliminar", 
                                "❌ **No se pudo eliminar el producto del carrito.**\n\n" +
                                "📝 **Verifique que el detalle existe.**");
                        }
                        
                    } else {
                        simpleNotify(event.getSender(), "Error de Parámetros", 
                            "❌ **Parámetros incorrectos.**\n\n" +
                            "📋 **Formato correcto:**\n" +
                            "carrito delete &lt;detalle_id&gt;\n\n" +
                            "📧 **Ejemplo:**\n" +
                            "carrito delete 1");
                    }
                    break;
                    
                default:
                    simpleNotify(event.getSender(), "Comando no reconocido", 
                        "❌ **Comando de carrito no reconocido.**\n\n" +
                        "📋 **Comandos disponibles:**\n" +
                        "• carrito get\n" +
                        "• carrito add &lt;producto_id, cantidad&gt;\n" +
                        "• carrito modify &lt;detalle_id, cantidad&gt;\n" +
                        "• carrito delete &lt;detalle_id&gt;");
                    break;
            }
        } catch (SQLException e) {
            System.err.println("=== ERROR SQL EN CARRITO ===");
            System.err.println("Usuario: " + event.getSender());
            System.err.println("Comando: " + event.getCommand());
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
            
            Logger.getLogger(EmailApp.class.getName()).log(Level.SEVERE, "Error en método carrito para usuario: " + event.getSender(), e);
            simpleNotify(event.getSender(), "Error de Sistema", 
                "❌ **Se ha producido un error al procesar su solicitud.**\n\n" +
                "🔧 **Error en carrito:** " + e.getMessage() + "\n\n" +
                "📋 **Comando ejecutado:** " + event.getCommand());
        } catch (Exception e) {
            System.err.println("=== ERROR INESPERADO EN CARRITO ===");
            System.err.println("Usuario: " + event.getSender());
            System.err.println("Comando: " + event.getCommand());
            System.err.println("Error: " + e.getMessage());
            e.printStackTrace();
            
            Logger.getLogger(EmailApp.class.getName()).log(Level.SEVERE, "Error inesperado en método carrito para usuario: " + event.getSender(), e);
            simpleNotify(event.getSender(), "Error de Sistema", 
                "❌ **Se ha producido un error inesperado al procesar su solicitud.**\n\n" +
                "🔧 **Error:** " + e.getMessage() + "\n\n" +
                "📋 **Comando ejecutado:** " + event.getCommand());
        }
    }

    /**
     * Obtiene el ID del usuario por email
     */
    private int getUserIdByEmail(String email) throws SQLException {
        String query = "SELECT id FROM \"user\" WHERE email = ?";
        SqlConnection sqlConnection = new SqlConnection(DBConnection.database, DBConnection.server, DBConnection.port, DBConnection.user, DBConnection.password);
        
        try (Connection connection = sqlConnection.connect();
             PreparedStatement ps = connection.prepareStatement(query)) {
            
            ps.setString(1, email);
            ResultSet rs = ps.executeQuery();
            
            if (rs.next()) {
                return rs.getInt("id");
            }
            
            throw new SQLException("Usuario no encontrado para el email: " + email);
        }
    }
    
    /**
     * Valida si el método de pago es válido
     */
    private boolean esMetodoPagoValido(String metodoPago) {
        if (metodoPago == null || metodoPago.trim().isEmpty()) {
            return false;
        }
        
        String metodo = metodoPago.toLowerCase().trim();
        return metodo.equals("efectivo") || 
               metodo.equals("tarjeta") || 
               metodo.equals("transferencia") || 
               metodo.equals("pago_movil") || 
               metodo.equals("qr") || 
               metodo.equals("paypal") || 
               metodo.equals("bitcoin");
    }
    
    /**
     * Filtra los datos de productos para mostrar solo las columnas relevantes para el cliente
     * Elimina: precio_compra (índice 3) e imagen (índice 5)
     * Mantiene: id, cod_producto, nombre, precio_venta, descripcion, categoria
     */
    private List<String[]> filtrarDatosParaCliente(List<String[]> productosData) {
        List<String[]> productosCliente = new ArrayList<>();
        
        for (String[] producto : productosData) {
            // Headers originales: {"id", "cod_producto", "nombre", "precio_compra", "precio_venta", "imagen", "descripcion", "categoria"}
            // Headers cliente:    {"ID", "Código", "Producto", "Precio", "Descripción", "Categoría"}
            
            productosCliente.add(new String[]{
                producto[0], // id → ID
                producto[1], // cod_producto → Código
                producto[2], // nombre → Producto
                producto[4], // precio_venta → Precio (salteamos precio_compra)
                producto[6], // descripcion → Descripción (salteamos imagen)
                producto[7]  // categoria → Categoría
            });
        }
        
        return productosCliente;
    }
    
    /**
     * Muestra mensaje informativo contextual sobre el carrito
     */
    private void mostrarMensajeInformativo(String userEmail, boolean esCategoria) {
        String mensaje = "🛒 **Para agregar productos al carrito:**\n\n" +
                        "📌 **Comando:** `carrito add <producto_id, cantidad>`\n\n" +
                        "📝 **Ejemplo:** `carrito add 1, 2` (agrega 2 unidades del producto con ID 1)\n\n";
        
        if (esCategoria) {
            mensaje += "🏷️ **Filtros útiles:**\n" +
                      "• `producto get` - Ver todos los productos\n" +
                      "• `categoria get` - Ver todas las categorías\n\n";
        } else {
            mensaje += "🏷️ **Filtros útiles:**\n" +
                      "• `producto get <categoria_id>` - Ver productos por categoría\n" +
                      "• `categoria get` - Ver categorías disponibles\n\n";
        }
        
        mensaje += "ℹ️ **Otros comandos útiles:**\n" +
                  "• `carrito get` - Ver tu carrito actual\n" +
                  "• `carrito modify <detalle_id, cantidad>` - Modificar cantidad\n" +
                  "• `carrito delete <detalle_id>` - Eliminar producto del carrito";
        
        simpleNotify(userEmail, "💡 Consejo", mensaje);
    }
    
    /**
     * Determina el estado de entrega de manera clara para el cliente
     */
    private String determinarEstadoEntrega(String estado, String fechaEnvio, String fechaEntrega) {
        if ("entregado".equals(estado)) {
            return "✅ Entregado";
        } else if ("enviado".equals(estado)) {
            return "🚛 En camino";
        } else if ("procesando".equals(estado)) {
            return "📦 Preparando";
        } else if ("pendiente".equals(estado)) {
            return "⏳ En preparación";
        } else if ("cancelado".equals(estado)) {
            return "❌ Cancelado";
        } else {
            return "📋 " + capitalizarEstado(estado);
        }
    }
    
    /**
     * Capitaliza el estado para mostrarlo de forma más amigable
     */
    private String capitalizarEstado(String estado) {
        if (estado == null || estado.isEmpty()) {
            return "Desconocido";
        }
        return estado.substring(0, 1).toUpperCase() + estado.substring(1).toLowerCase();
    }
    
    /**
     * Obtiene una descripción detallada del estado del pedido
     */
    private String obtenerDescripcionEstado(String estado) {
        switch (estado.toLowerCase()) {
            case "pendiente":
                return "Tu pedido está en cola de preparación. Pronto comenzaremos a procesarlo.";
            case "procesando":
                return "Estamos preparando tu pedido para el envío. Te notificaremos cuando esté listo.";
            case "enviado":
                return "Tu pedido está en camino a la dirección especificada. ¡Pronto llegará!";
            case "entregado":
                return "Tu pedido ha sido entregado exitosamente. ¡Esperamos que disfrutes tu compra!";
            case "cancelado":
                return "Este pedido ha sido cancelado. Si tienes dudas, contáctanos.";
            default:
                return "Estado: " + capitalizarEstado(estado);
        }
    }

}
